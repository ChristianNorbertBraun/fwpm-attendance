package de.fhws.fiw.fwpm.attendance.de;

import de.fhws.fiw.fwpm.attendance.mailService.MailSender;
import de.fhws.fiw.fwpm.attendance.model.Student;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
/**
 * Created by marcelgross on 24.08.16.
 */
public class MailSenderTest {

	private MailSender mailSender;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void init() {
		mailSender = new MailSender();
	}

	@Ignore
	@Test
	public void sendMailsTest() {
		mailSender.sendLecturerMails(4200000000000l);
	}

	@Test
	public void getCSVTest() {
		List<Student> students = new ArrayList<>();
		students.add(new Student("k123", "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		students.add(new Student("k124", "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		students.add(new Student("k125", "Vorname", "Nachname", "Info", 6, "test@mail.de"));
		students.add(new Student("k126", "Vorname", "Nachname", "Info", 6, "test@mail.de"));

		InputStream inputStream = mailSender.testMethod(students);
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

		assertFalse(result.isEmpty());
	}
}
