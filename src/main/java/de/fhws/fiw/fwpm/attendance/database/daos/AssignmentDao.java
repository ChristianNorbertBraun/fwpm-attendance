package de.fhws.fiw.fwpm.attendance.database.daos;

import de.fhws.fiw.fwpm.attendance.model.Assignment;
import de.fhws.fiw.fwpm.attendance.model.ExtendedStudent;
import de.fhws.fiw.fwpm.attendance.model.Fwpm;
import de.fhws.fiw.fwpm.attendance.model.Hardship;
import de.fhws.fiw.fwpm.attendance.model.Student;

import java.sql.SQLException;
import java.util.List;

public interface AssignmentDao
{
    // Assignments
    void createAssignment( Assignment assignment ) throws SQLException;
    List<Assignment> readAllAssignments() throws SQLException;
    Assignment readAssignment( long id ) throws SQLException;
    void updateAssignment( long id, Assignment assignment ) throws SQLException;
    void deleteAssignment( long id ) throws SQLException;

    // FWPMs
    Fwpm readFwpm( long assignmentId, long fwpmId ) throws SQLException;
    void addStudentToFwpm( long assignmentId, long fwpmId, Student student ) throws SQLException;
    void removeStudentFromFwpm( long assignmentId, long fwpmId, String kNummer ) throws SQLException;

    // Students
    List<ExtendedStudent> readAllExtendedStudentsForAssignment(long assignmentId) throws SQLException;
    List<Student> readAllStudentsForAssignment( long assignmentId ) throws SQLException;
    List<Fwpm> readAllFwpmsForStudent( long assignmentId, String kNummer ) throws SQLException;

    // Hardship cases
    boolean hasHardshipCases( long assignmentId ) throws SQLException;
    void solveHardship( Hardship hardship ) throws SQLException;
}
