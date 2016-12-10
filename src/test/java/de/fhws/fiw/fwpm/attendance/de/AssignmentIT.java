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

public class AssignmentIT {

	private AssignmentDao assignmentDao;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void init() throws SQLException {
		assignmentDao = DaoFactory.getInstance().createAssignmentDao();
	}

	@Ignore
	@Test
	public void createAssignmentForIT() throws SQLException {
		assignmentDao.createAssignment(getAssignment());
	}


	private Assignment getAssignment() {
		return new Assignment(1, new Date(), getFwpms());
	}

	private List<Fwpm> getFwpms() {
		List<Fwpm> fwpms = new ArrayList<>();
		fwpms.add(new Fwpm(1l, 123, "Microservices", "Modulverantwortliche/r", "test@mail.de", 10, getStudents()));
		fwpms.add(new Fwpm(2l, 124, "International Communication", "Modulverantwortliche/r", "test@mail.de", 10, getStudents()));
		fwpms.add(new Fwpm(0l, 0, "Härtefälle", "Studiendekan", "test@mail.org", 100,getHardShips()));

		return fwpms;
	}

	private List<Student> getStudents() {
		List<Student> students = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			students.add(getStudent(i));
		}

		return students;
	}

	private List<Student> getHardShips() {
		List<Student> students = new ArrayList<>();
		students.add(getStudent(3));

		return students;
	}

	private Student getStudent(int postfix) {
		Student student = new Student();
		student.setFirstName("Firstname " + postfix);
		student.setLastName("LastName " + postfix);
		student.seteMail(postfix + "mail@web.de");
		student.setMajor("BIN");
		student.setSemester(7);
		student.setkNummer("k1234" + postfix);

		return student;
	}
}
