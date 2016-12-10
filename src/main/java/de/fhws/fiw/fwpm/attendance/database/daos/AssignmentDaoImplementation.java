package de.fhws.fiw.fwpm.attendance.database.daos;

import de.fhws.fiw.fwpm.attendance.database.Persistency;
import de.fhws.fiw.fwpm.attendance.database.tables.AssignmentEntriesTable;
import de.fhws.fiw.fwpm.attendance.database.tables.AssignmentsTable;
import de.fhws.fiw.fwpm.attendance.database.tables.FwpmsTable;
import de.fhws.fiw.fwpm.attendance.database.tables.StudentsTable;
import de.fhws.fiw.fwpm.attendance.model.Assignment;
import de.fhws.fiw.fwpm.attendance.model.ExtendedStudent;
import de.fhws.fiw.fwpm.attendance.model.Fwpm;
import de.fhws.fiw.fwpm.attendance.model.Hardship;
import de.fhws.fiw.fwpm.attendance.model.Student;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class AssignmentDaoImplementation implements AssignmentDao
{
    private final long FWPM_ID_HARDSHIP_CASES = 0;

    private Persistency persistency;

    public AssignmentDaoImplementation( Persistency persistency )
    {
        this.persistency = persistency;
    }

    @Override
    public void createAssignment( Assignment assignment ) throws SQLException
    {
        Connection connection = persistency.getConnection();
        connection.setAutoCommit( false );

        try
        {
            insertIntoAssignmentsTable( connection, assignment );

            for ( Fwpm fwpm : assignment.getFwpms() )
            {
                updateFwpmsTable( connection, fwpm );

                for ( Student student : fwpm.getParticipants() )
                {
                    updateStudentsTable( connection, student );
                    insertIntoAssignmentEntriesTable( connection, assignment.getId(), fwpm.getId(), student.getkNummer() );
                }
            }
            connection.commit();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            connection.rollback();
            throw new SQLException( e );
        }
        finally
        {
            connection.close();
        }
    }

    @Override
    public void solveHardship(Hardship hardship) throws SQLException {
        Connection connection = persistency.getConnection();
        connection.setAutoCommit( false );

        try
        {
            updateStudentsTable( connection, hardship.getStudent() );
            for (Long fwpmId : hardship.getFwpmIds()) {
                insertIntoAssignmentEntriesTable(connection, hardship.getAssignmentId(), fwpmId, hardship.getStudent().getkNummer());
            }
            deleteFromAssignmentEntriesTable( connection, hardship.getAssignmentId(), FWPM_ID_HARDSHIP_CASES, hardship.getStudent().getkNummer() );
            connection.commit();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            connection.rollback();
            throw new SQLException(e);
        }
        finally
        {
            connection.close();
        }
    }

    @Override
    public List<Assignment> readAllAssignments() throws SQLException
    {
        List<Assignment> allAssignments = new LinkedList<>();
        Connection connection = persistency.getConnection();

        try
        {
            String query = "SELECT * " +
                    "FROM " + AssignmentEntriesTable.TABLE_NAME + " ae " +
                    "LEFT JOIN " + AssignmentsTable.TABLE_NAME + " a ON ae." + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = a." + AssignmentsTable.FIELD_ID + " " +
                    "LEFT JOIN " + FwpmsTable.TABLE_NAME + " f ON ae." + AssignmentEntriesTable.FIELD_FWPM_ID + " = f." + FwpmsTable.FIELD_ID + " " +
                    "LEFT JOIN " + StudentsTable.TABLE_NAME + " s ON ae." + AssignmentEntriesTable.FIELD_STUDENT_KNR + " = s." + StudentsTable.FIELD_KNR;

            PreparedStatement preparedStatement = connection.prepareStatement( query );
            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() )
            {
                Assignment currentAssignment = new Assignment();
                currentAssignment.setId( resultSet.getLong( AssignmentEntriesTable.FIELD_ASSIGNMENT_ID ) );
                currentAssignment.setDate( new Date( resultSet.getTimestamp( AssignmentsTable.FIELD_ASSIGNMENT_DATE ).getTime() ) );
                currentAssignment.setTotalSatisfaction( resultSet.getInt( AssignmentsTable.FIELD_SATISFACTION ) );
                currentAssignment.setTotalMaxSatisfaction( resultSet.getInt( AssignmentsTable.FIELD_MAX_SATISFACTION ) );

                Fwpm currentFwpm = new Fwpm();
                currentFwpm.setId( resultSet.getLong( AssignmentEntriesTable.FIELD_FWPM_ID ) );
                currentFwpm.setTitle( resultSet.getString( FwpmsTable.FIELD_TITLE ) );
                currentFwpm.setModuleNumber( resultSet.getInt( FwpmsTable.FIELD_MODULE_NUMBER ) );
                currentFwpm.setResponsibleTeacherEmail( resultSet.getString( FwpmsTable.FIELD_RESPONSIBLE_TEACHER_EMAIL ) );
                currentFwpm.setResponsibleTeacher( resultSet.getString( FwpmsTable.FIELD_RESPONSIBLE_TEACHER ) );
                currentFwpm.setNumberOfSeats( resultSet.getInt( FwpmsTable.FIELD_MODULE_NUMBER ) );

                Student currentStudent = new Student();
                currentStudent.setkNummer( resultSet.getString( AssignmentEntriesTable.FIELD_STUDENT_KNR ) );
                currentStudent.setFirstName( resultSet.getString( StudentsTable.FIELD_FIRST_NAME ) );
                currentStudent.setLastName( resultSet.getString( StudentsTable.FIELD_LAST_NAME ) );
                currentStudent.setMajor( resultSet.getString( StudentsTable.FIELD_MAJOR ) );
                currentStudent.setSemester( resultSet.getInt( StudentsTable.FIELD_SEMESTER ) );
                currentStudent.seteMail( resultSet.getString( StudentsTable.FIELD_EMAIL ) );

                int assignmentIndex = allAssignments.indexOf( currentAssignment );

                if ( assignmentIndex == -1 )
                {
                    currentFwpm.getParticipants().add( currentStudent );
                    currentAssignment.getFwpms().add( currentFwpm );
                    allAssignments.add( currentAssignment );
                }
                else
                {
                    int fwpmIndex = allAssignments.get( assignmentIndex ).getFwpms().indexOf( currentFwpm );

                    if ( fwpmIndex == -1 )
                    {
                        currentFwpm.getParticipants().add( currentStudent );
                        allAssignments.get( assignmentIndex ).getFwpms().add( currentFwpm );
                    }
                    else
                    {
                        allAssignments.get( assignmentIndex ).getFwpms().get( fwpmIndex ).getParticipants().add( currentStudent );
                    }
                }
            }
            resultSet.close();
            preparedStatement.close();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new SQLException( e );
        }
        finally
        {
            connection.close();
        }
        return allAssignments;
}

    @Override
    public List<ExtendedStudent> readAllExtendedStudentsForAssignment(long assignmentId) throws SQLException {
        List<ExtendedStudent> result = new ArrayList<>();
        List<Student> studentsForAssignment = readAllStudentsForAssignment(assignmentId);
        for (Student currentStudent : studentsForAssignment) {
            List<Fwpm> participatingFwpms = readAllFwpmsForStudent(assignmentId, currentStudent.getkNummer());
            result.add(new ExtendedStudent(currentStudent, participatingFwpms));
        }

        return result;
    }

    @Override
    public List<Student> readAllStudentsForAssignment(long assignmentId) throws SQLException {
        List<Student> result = new ArrayList<>();
        Connection connection = persistency.getConnection();
        try
        {
            String query = "SELECT " + StudentsTable.FIELD_KNR + ", " + StudentsTable.FIELD_FIRST_NAME + ", " + StudentsTable.FIELD_LAST_NAME + ", " + StudentsTable.FIELD_MAJOR + ", " + StudentsTable.FIELD_SEMESTER + ", " + StudentsTable.FIELD_EMAIL + " " +
                    "FROM " + AssignmentEntriesTable.TABLE_NAME + " ae " +
                    "LEFT JOIN " + StudentsTable.TABLE_NAME + " s ON ae." + AssignmentEntriesTable.FIELD_STUDENT_KNR + " = s." + StudentsTable.FIELD_KNR + " " +
                    "WHERE ae." + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = ? " +
                    "GROUP BY " + StudentsTable.FIELD_KNR;

            PreparedStatement preparedStatement = connection.prepareStatement( query );
            preparedStatement.setLong(1, assignmentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() )
            {
                Student currentStudent = new Student();
                currentStudent.setkNummer( resultSet.getString( StudentsTable.FIELD_KNR ) );
                currentStudent.setFirstName(resultSet.getString(StudentsTable.FIELD_FIRST_NAME));
                currentStudent.setLastName(resultSet.getString(StudentsTable.FIELD_LAST_NAME));
                currentStudent.setMajor(resultSet.getString(StudentsTable.FIELD_MAJOR));
                currentStudent.setSemester(resultSet.getInt(StudentsTable.FIELD_SEMESTER));
                currentStudent.seteMail(resultSet.getString(StudentsTable.FIELD_EMAIL));
                result.add(currentStudent);
            }
            resultSet.close();
            preparedStatement.close();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new SQLException( e );
        }
        finally
        {
            connection.close();
        }
        return result;
    }

    @Override
    public List<Fwpm> readAllFwpmsForStudent(long assignmentId, String kNummer) throws SQLException
    {
        List<Fwpm> result = new ArrayList<>();
        Connection connection = persistency.getConnection();
        try
        {
            String query = "SELECT * " +
                    "FROM " + AssignmentEntriesTable.TABLE_NAME + " ae " +
                    "LEFT JOIN " + FwpmsTable.TABLE_NAME + " f ON ae." + AssignmentEntriesTable.FIELD_FWPM_ID + " = f." + FwpmsTable.FIELD_ID + " " +
                    "LEFT JOIN " + StudentsTable.TABLE_NAME + " s ON ae." + AssignmentEntriesTable.FIELD_STUDENT_KNR + " = s." + StudentsTable.FIELD_KNR+ " " +
                    "WHERE ae." + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = ? AND s." + StudentsTable.FIELD_KNR + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement( query );
            preparedStatement.setLong(1, assignmentId);
            preparedStatement.setString(2, kNummer);
            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() )
            {
                Fwpm currentFwpm = new Fwpm();
                currentFwpm.setId( resultSet.getLong( AssignmentEntriesTable.FIELD_FWPM_ID ) );
                currentFwpm.setTitle( resultSet.getString( FwpmsTable.FIELD_TITLE ) );
                currentFwpm.setModuleNumber( resultSet.getInt( FwpmsTable.FIELD_MODULE_NUMBER ) );
                currentFwpm.setResponsibleTeacherEmail( resultSet.getString( FwpmsTable.FIELD_RESPONSIBLE_TEACHER_EMAIL ) );
                currentFwpm.setResponsibleTeacher( resultSet.getString( FwpmsTable.FIELD_RESPONSIBLE_TEACHER ) );
                currentFwpm.setNumberOfSeats( resultSet.getInt( FwpmsTable.FIELD_NUMBER_OF_SEATS ) );
                result.add(currentFwpm);
            }
            resultSet.close();
            preparedStatement.close();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new SQLException( e );
        }
        finally
        {
            connection.close();
        }
        return result;
    }

    @Override
    public Assignment readAssignment( long id ) throws SQLException
    {
        Assignment assignment = new Assignment();
        Connection connection = persistency.getConnection();

        try
        {
            String query = "SELECT * " +
                    "FROM " + AssignmentEntriesTable.TABLE_NAME + " ae " +
                    "LEFT JOIN " + AssignmentsTable.TABLE_NAME + " a ON ae." + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = a." + AssignmentsTable.FIELD_ID + " " +
                    "LEFT JOIN " + FwpmsTable.TABLE_NAME + " f ON ae." + AssignmentEntriesTable.FIELD_FWPM_ID + " = f." + FwpmsTable.FIELD_ID + " " +
                    "LEFT JOIN " + StudentsTable.TABLE_NAME + " s ON ae." + AssignmentEntriesTable.FIELD_STUDENT_KNR + " = s." + StudentsTable.FIELD_KNR + " " +
                    "WHERE ae." + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement( query );
            preparedStatement.setLong( 1, id );
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean didNotReadAssignmentMetaInfoYet = true;

            while ( resultSet.next() )
            {
                if ( didNotReadAssignmentMetaInfoYet )
                {
                    assignment.setId( resultSet.getLong( AssignmentEntriesTable.FIELD_ASSIGNMENT_ID ) );
                    assignment.setDate( new Date( resultSet.getTimestamp( AssignmentsTable.FIELD_ASSIGNMENT_DATE ).getTime() ) );
                    assignment.setTotalSatisfaction( resultSet.getInt( AssignmentsTable.FIELD_SATISFACTION ) );
                    assignment.setTotalMaxSatisfaction( resultSet.getInt( AssignmentsTable.FIELD_MAX_SATISFACTION ) );
                    didNotReadAssignmentMetaInfoYet = false;
                }

                Fwpm currentFwpm = new Fwpm();
                currentFwpm.setId( resultSet.getLong( AssignmentEntriesTable.FIELD_FWPM_ID ) );
                currentFwpm.setTitle( resultSet.getString( FwpmsTable.FIELD_TITLE ) );
                currentFwpm.setModuleNumber( resultSet.getInt( FwpmsTable.FIELD_MODULE_NUMBER ) );
                currentFwpm.setResponsibleTeacherEmail( resultSet.getString( FwpmsTable.FIELD_RESPONSIBLE_TEACHER_EMAIL ) );
                currentFwpm.setResponsibleTeacher( resultSet.getString( FwpmsTable.FIELD_RESPONSIBLE_TEACHER ) );
                currentFwpm.setNumberOfSeats( resultSet.getInt( FwpmsTable.FIELD_NUMBER_OF_SEATS ) );

                Student currentStudent = new Student();
                currentStudent.setkNummer( resultSet.getString( AssignmentEntriesTable.FIELD_STUDENT_KNR ) );
                currentStudent.setFirstName( resultSet.getString( StudentsTable.FIELD_FIRST_NAME ) );
                currentStudent.setLastName( resultSet.getString( StudentsTable.FIELD_LAST_NAME ) );
                currentStudent.setMajor( resultSet.getString( StudentsTable.FIELD_MAJOR ) );
                currentStudent.setSemester( resultSet.getInt( StudentsTable.FIELD_SEMESTER ) );
                currentStudent.seteMail( resultSet.getString( StudentsTable.FIELD_EMAIL ) );

                int fwpmIndex = assignment.getFwpms().indexOf( currentFwpm );

                if ( fwpmIndex == -1 )
                {
                    currentFwpm.getParticipants().add( currentStudent );
                    assignment.getFwpms().add( currentFwpm );
                }
                else
                {
                    assignment.getFwpms().get( fwpmIndex ).getParticipants().add( currentStudent );
                }
            }
            resultSet.close();
            preparedStatement.close();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new SQLException( e );
        }
        finally
        {
            connection.close();
        }
        return assignment;
    }

    @Override
    public Fwpm readFwpm( long assigmentId, long fwpmId ) throws SQLException
    {
        Fwpm result = null;
        Assignment assignment = readAssignment( assigmentId );

        for ( Fwpm fwpm : assignment.getFwpms() )
        {
            if ( fwpm.getId() == fwpmId )
            {
                result = fwpm;
                break;
            }
        }
        return result;
    }

    @Override
    public void updateAssignment( long id, Assignment assignment ) throws SQLException
    {
        deleteAssignment( id );
        createAssignment( assignment );
    }

    @Override
    public void deleteAssignment( long id ) throws SQLException
    {
        Connection connection = persistency.getConnection();
        connection.setAutoCommit( false );

        try
        {
            String query = "DELETE FROM " + AssignmentsTable.TABLE_NAME + " " +
                    "WHERE " + AssignmentsTable.FIELD_ID + " = ? ";

            String query2 = "DELETE FROM " + AssignmentEntriesTable.TABLE_NAME + " " +
                    "WHERE " + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement( query );
            preparedStatement.setLong( 1, id );
            preparedStatement.executeUpdate( );
            preparedStatement.close( );

            preparedStatement = connection.prepareStatement( query2 );
            preparedStatement.setLong( 1, id );
            preparedStatement.executeUpdate( );
            preparedStatement.close( );

            connection.commit();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            connection.rollback();
            throw new SQLException( e );
        }
        finally {
            connection.close();
        }
    }

    private void insertIntoAssignmentsTable( Connection connection, Assignment assignment ) throws SQLException
    {
        String query = "INSERT INTO " + AssignmentsTable.TABLE_NAME + "(" +
                AssignmentsTable.FIELD_ID + ", " +
                AssignmentsTable.FIELD_ASSIGNMENT_DATE + ", " +
                AssignmentsTable.FIELD_SATISFACTION + ", " +
                AssignmentsTable.FIELD_MAX_SATISFACTION + ")" +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong( 1, assignment.getId() );
        preparedStatement.setTimestamp( 2, new Timestamp( assignment.getDate().getTime() ) );
        preparedStatement.setInt( 3, assignment.getTotalSatisfaction() );
        preparedStatement.setInt( 4, assignment.getTotalMaxSatisfaction() );
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void updateFwpmsTable( Connection connection, Fwpm fwpm ) throws SQLException
    {
        String query = "INSERT INTO " + FwpmsTable.TABLE_NAME + "(" +
                FwpmsTable.FIELD_ID + ", " +
                FwpmsTable.FIELD_TITLE + ", " +
                FwpmsTable.FIELD_MODULE_NUMBER + ", " +
                FwpmsTable.FIELD_RESPONSIBLE_TEACHER_EMAIL + ", " +
                FwpmsTable.FIELD_RESPONSIBLE_TEACHER + ", " +
                FwpmsTable.FIELD_NUMBER_OF_SEATS + ") " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                FwpmsTable.FIELD_TITLE + " = ?, " +
                FwpmsTable.FIELD_MODULE_NUMBER + " = ?, " +
                FwpmsTable.FIELD_RESPONSIBLE_TEACHER_EMAIL + " = ?, " +
                FwpmsTable.FIELD_RESPONSIBLE_TEACHER + " = ?, " +
                FwpmsTable.FIELD_NUMBER_OF_SEATS + " = ?";

        PreparedStatement preparedStatement = connection.prepareStatement( query );
        preparedStatement.setLong(    1, fwpm.getId() );
        preparedStatement.setString(  2, fwpm.getTitle() );
        preparedStatement.setInt(     3, fwpm.getModuleNumber() );
        preparedStatement.setString(  4, fwpm.getResponsibleTeacherEmail() );
        preparedStatement.setString(  5, fwpm.getResponsibleTeacher() );
        preparedStatement.setInt(     6, fwpm.getNumberOfSeats() );
        preparedStatement.setString(  7, fwpm.getTitle() );
        preparedStatement.setInt(     8, fwpm.getModuleNumber() );
        preparedStatement.setString(  9, fwpm.getResponsibleTeacherEmail() );
        preparedStatement.setString( 10, fwpm.getResponsibleTeacher() );
        preparedStatement.setInt(    11, fwpm.getNumberOfSeats() );
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void updateStudentsTable( Connection connection, Student student ) throws SQLException
    {
        String query = "INSERT INTO " + StudentsTable.TABLE_NAME + "(" +
                StudentsTable.FIELD_KNR + ", " +
                StudentsTable.FIELD_FIRST_NAME + ", " +
                StudentsTable.FIELD_LAST_NAME + ", " +
                StudentsTable.FIELD_MAJOR + ", " +
                StudentsTable.FIELD_SEMESTER + ", " +
                StudentsTable.FIELD_EMAIL + " ) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                StudentsTable.FIELD_FIRST_NAME + " = ?, " +
                StudentsTable.FIELD_LAST_NAME + " = ?, " +
                StudentsTable.FIELD_MAJOR + " = ?, " +
                StudentsTable.FIELD_SEMESTER + " = ?, " +
                StudentsTable.FIELD_EMAIL + " = ?";


        PreparedStatement preparedStatement = connection.prepareStatement( query );
        preparedStatement.setString(  1, student.getkNummer() );
        preparedStatement.setString(  2, student.getFirstName() );
        preparedStatement.setString(  3, student.getLastName() );
        preparedStatement.setString(  4, student.getMajor() );
        preparedStatement.setInt(     5, student.getSemester() );
        preparedStatement.setString(  6, student.geteMail() );
        preparedStatement.setString(  7, student.getFirstName() );
        preparedStatement.setString(  8, student.getLastName() );
        preparedStatement.setString(  9, student.getMajor() );
        preparedStatement.setInt(    10, student.getSemester() );
        preparedStatement.setString( 11, student.geteMail() );
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void insertIntoAssignmentEntriesTable( Connection connection, long assignmentId, long fwpmId, String kNummer ) throws SQLException
    {
        String query = "INSERT INTO " + AssignmentEntriesTable.TABLE_NAME + "(" +
                AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + ", " +
                AssignmentEntriesTable.FIELD_FWPM_ID + ", " +
                AssignmentEntriesTable.FIELD_STUDENT_KNR + ") " +
                "VALUES (?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement( query );
        preparedStatement.setLong(   1, assignmentId );
        preparedStatement.setLong(   2, fwpmId );
        preparedStatement.setString( 3, kNummer);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private void deleteFromAssignmentEntriesTable( Connection connection, long assignmentId, long fwpmId, String kNummer ) throws SQLException
    {
        String query = "DELETE FROM " + AssignmentEntriesTable.TABLE_NAME + " " +
                "WHERE " + AssignmentEntriesTable.FIELD_ASSIGNMENT_ID + " = ? " +
                "AND " + AssignmentEntriesTable.FIELD_FWPM_ID + " = ? " +
                "AND " + AssignmentEntriesTable.FIELD_STUDENT_KNR + " = ?";

        PreparedStatement preparedStatement = connection.prepareStatement( query );
        preparedStatement.setLong(   1, assignmentId);
        preparedStatement.setLong(   2, fwpmId);
        preparedStatement.setString( 3, kNummer );
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public void addStudentToFwpm( long assignmentId, long fwpmId, Student student ) throws SQLException
    {
        Connection connection = persistency.getConnection();
        connection.setAutoCommit( false );

        try
        {
            updateStudentsTable( connection, student );
            insertIntoAssignmentEntriesTable( connection, assignmentId, fwpmId, student.getkNummer() );
            deleteFromAssignmentEntriesTable( connection, assignmentId, FWPM_ID_HARDSHIP_CASES, student.getkNummer() );
            connection.commit();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            connection.rollback();
            throw new SQLException( e );
        }
        finally
        {
            connection.close();
        }
    }

    @Override
    public boolean hasHardshipCases( long assignmentId ) throws SQLException
    {
        boolean result = true;
        Fwpm hardshipCases = readFwpm( assignmentId, FWPM_ID_HARDSHIP_CASES );

        if ( hardshipCases == null )
        {
            result = false;
        }
        return result;
    }

    @Override
    public void removeStudentFromFwpm( long assignmentId, long fwpmId, String kNummer ) throws SQLException
    {
        Connection connection = persistency.getConnection();
        connection.setAutoCommit(false);

        try
        {
            deleteFromAssignmentEntriesTable( connection, assignmentId, fwpmId, kNummer );
            connection.commit();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            connection.rollback();
            throw new SQLException(e);
        }
        finally
        {
            connection.close();
        }
    }
}
