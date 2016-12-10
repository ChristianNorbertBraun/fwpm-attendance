package de.fhws.fiw.fwpm.attendance.test;


import com.googlecode.jcsv.writer.CSVEntryConverter;
import com.owlike.genson.Genson;
import de.fhws.fiw.fwpm.attendance.csvUtility.CSVExporter;
import de.fhws.fiw.fwpm.attendance.database.DaoFactory;
import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDao;
import de.fhws.fiw.fwpm.attendance.model.Assignment;
import de.fhws.fiw.fwpm.attendance.model.Fwpm;
import de.fhws.fiw.fwpm.attendance.model.Hardship;
import de.fhws.fiw.fwpm.attendance.model.Student;
import de.fhws.fiw.fwpm.attendance.properties.PropertySingleton;
import jersey.repackaged.com.google.common.collect.ImmutableList;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

public class TestAttendanceService
{
    public static void main( String[] args )
    {
        new TestAttendanceService().runTest();
    }

    private void runTest()
    {
        try {
            Properties properties = PropertySingleton.getInstance();
            String environment = properties.getProperty( "ENVIRONMENT" );
            System.out.println( environment );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }


        Assignment[] assignments = new Assignment[2];
        assignments[0] = fakeAssignment( 100 );
        assignments[1] = fakeAssignment( 200 );

        List<Long> fwpmIds = new ArrayList<>();
        fwpmIds.add( 100L );
        fwpmIds.add( 200L );



        System.out.println( new Genson().serialize( new Hardship( new Student( "k10004", "Donald", "D", "Major X", 4, "donald.d@mail.org" ), 100, fwpmIds ) ) );
    }

    private Assignment fakeAssignment( int id )
    {
        Assignment assignment = new Assignment();
        assignment.setId( id );
        assignment.setDate( new Date() );
        assignment.setFwpms( fakeFwpms() );

        return assignment;
    }

    private List<Fwpm > fakeFwpms()
    {
        List<Fwpm> fwpms = new LinkedList<>();
        fwpms.add( new Fwpm( 100, 100, "FWPM 1", "Lecturer A", "lecturer_a@mail.org", 10, fakeStudents() ) );
//        fwpms.add( new Fwpm( 200, 200, "FWPM 2", "Lecturer B", "lecturer_b@mail.org", 10, fakeStudents() ) );
//        fwpms.add( new Fwpm( 300, 300, "FWPM 3", "Lecturer C", "lecturer_c@mail.org", 10, fakeStudents() ) );
//        fwpms.add( new Fwpm( 0, 0, "Härtefälle", "Studiendekan", "studiendekan@mail.org", 100, fakeHardshipCases() ) );
        return fwpms;
    }

    private List<Student> fakeStudents()
    {
        List<Student> students = new LinkedList<>();
        students.add( new Student( "k10001", "Alfred", "A", "Major X", 1, "alfred.a@mail.org" ) );
        students.add( new Student( "k10002", "Bertel", "B", "Major Y", 2, "bertel.b@mail.org" ) );
//        students.add( new Student( "k10003", "Cicero", "C", "Major Z", 3, "cicero.c@mail.org" ) );
        return students;
    }

    private List<Student> fakeHardshipCases()
    {
        List<Student> students = new LinkedList<>();
        students.add( new Student( "k10004", "Donald", "D", "Major X", 4, "donald.d@mail.org" ) );
//        students.add( new Student( "k10005", "Emilia", "E", "Major Y", 5, "emilia.e@mail.org" ) );
        return students;
    }
}