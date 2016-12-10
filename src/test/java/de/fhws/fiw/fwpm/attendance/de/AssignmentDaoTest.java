package de.fhws.fiw.fwpm.attendance.de;

import de.fhws.fiw.fwpm.attendance.database.DaoFactory;
import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDao;
import de.fhws.fiw.fwpm.attendance.model.Assignment;
import de.fhws.fiw.fwpm.attendance.model.Fwpm;
import de.fhws.fiw.fwpm.attendance.model.Student;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class AssignmentDaoTest {

	private AssignmentDao assignmentDao;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void init() throws SQLException {
		assignmentDao = DaoFactory.getInstance().createAssignmentDao();
		dropDatabase();
	}

	@Test
	public void createAssignmentTest() throws SQLException {
		assignmentDao.createAssignment(getAssignment());
	}


	@Ignore
	@Test
	public void readAllStudentsForAssignmentTest() throws SQLException {
		List<Student> students = assignmentDao.readAllStudentsForAssignment(4200000000000l);

		assertEquals(8, students.size());
	}

	@Ignore
	@Test
	public void readAllFwpmsForStudent() throws SQLException {
		List<Fwpm> fwpms = assignmentDao.readAllFwpmsForStudent(4200000000000l, "k1231");

		assertTrue(fwpms.size() > 0);
	}


	private Assignment getAssignment() {
		return new Assignment(4200000000000l, new Date(), getFwpms());
	}

	private List<Fwpm> getFwpms() {
		List<Fwpm> fwpms = new ArrayList<>();
		fwpms.add(new Fwpm(9999999l, 123, "TestFWPM1", "Modulverantwortliche/r", "test@mail.de", 10, getStudents(1)));
		fwpms.add(new Fwpm(9999998l, 124, "TestFWPM2", "Modulverantwortliche/r", "test@mail.de", 10, getStudents(2)));
		fwpms.add(new Fwpm(0l, 0, "Härtefälle", "Studiendekan", "test@mail.org", 100,getStudents(0)));

		return fwpms;
	}

	private List<Student> getStudents(int postFix) {
		List<Student> students = new ArrayList<>();
		students.add(new Student("k123" + postFix, "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		students.add(new Student("k124" + postFix, "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		students.add(new Student("k125" + postFix, "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		students.add(new Student("k126" + postFix, "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		return students;
	}

	private void dropDatabase() throws SQLException {
		List<Assignment> allAssignments = assignmentDao.readAllAssignments();
		if (allAssignments.size() > 0) {
			for (Assignment assignment : allAssignments) {
				assignmentDao.deleteAssignment(assignment.getId());
			}
		}
	}

}
