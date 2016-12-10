package de.fhws.fiw.fwpm.attendance.mailService;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import de.fhws.fiw.fwpm.attendance.csvUtility.CSVExporter;
import de.fhws.fiw.fwpm.attendance.database.DaoFactory;
import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDao;
import de.fhws.fiw.fwpm.attendance.model.Assignment;
import de.fhws.fiw.fwpm.attendance.model.Fwpm;
import de.fhws.fiw.fwpm.attendance.model.Student;
import de.fhws.fiw.mail.SendMail;
import jersey.repackaged.com.google.common.collect.ImmutableList;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class MailSender {

	private AssignmentDao assignmentDao;
	private Properties mailProperties;

	public MailSender() {
		assignmentDao = DaoFactory.getInstance().createAssignmentDao();
		try {
			mailProperties = MailPropertyReader.getInstance();
		} catch (IOException ex) {
			throw new WebApplicationException("Could not read mail properties");
		}
	}

	public void sendLecturerMails(long periodId)
	{
		try
		{
			Assignment assignment = assignmentDao.readAssignment( periodId );
			sendToLecturers( assignment.getFwpms() );
		}
		catch ( SQLException ex )
		{
			throw new WebApplicationException( "Could not read Assignment" );
		}
	}

	public void sendStudentMails( long periodId )
	{
		try
		{
			Assignment assignment = assignmentDao.readAssignment( periodId );
			sendMailToStudents( assignment.getId() );
		}
		catch ( SQLException ex )
		{
			throw new WebApplicationException( "Could not read Assignment" );
		}
	}

	private void sendMailToStudents( long assignmentId )
	{
		try
		{
			List<Student> students = assignmentDao.readAllStudentsForAssignment( assignmentId );

			for ( Student student : students )
			{
				List<Fwpm> fwpms = assignmentDao.readAllFwpmsForStudent(assignmentId, student.getkNummer());

				SendMail sendMail = new SendMail();
				sendMail.prepareMailer();
				sendMail.setRecipients( new Address[] { new InternetAddress( student.geteMail() ) } );
				sendMail.setMailSubject( getSubject() );
				sendMail.setMailBody( getStudentText( fwpms ) );
				sendMail.sendMail();
			}
		}
		catch ( SQLException ex )
		{
			throw new WebApplicationException( "SQL Problem", ex );
		}
		catch ( MessagingException ex )
		{
			throw new WebApplicationException( "Could not send student mail", ex );
		}
	}


	private void sendToLecturers(List<Fwpm> fwpms) {

		for (Fwpm currentFwpm : fwpms) {
			try {
				String fileName = currentFwpm.getTitle() + ".csv";
				SendMail sendMail = new SendMail();
				sendMail.prepareMailer();
				sendMail.setRecipients(new Address[]{new InternetAddress(currentFwpm.getResponsibleTeacherEmail())});
				sendMail.setMailSubject(getSubject(currentFwpm.getTitle()));
				sendMail.setMailBody(getText(currentFwpm.getTitle()));
				sendMail.addAttachment(getCsvFile(currentFwpm.getParticipants()), "text/plain", fileName);
				sendMail.sendMail();
			} catch (MessagingException ex) {
				throw new WebApplicationException("Could not send Mail", ex);
			} catch (IOException ex) {
				throw new WebApplicationException("Could not attach csv", ex);
			}
		}
	}

	private InputStream getCsvFile(List<Student> students) {

		CSVEntryConverter<Student> converter = student -> new String[]
				{
						String.valueOf(student.getkNummer()),
						student.getFirstName() + " " + student.getLastName(),
						student.getMajor(),
						String.valueOf(student.getSemester()),
						student.geteMail()
				};
		ImmutableList<String> headers = ImmutableList.of("Id", "Full Name", "Studiengang", "Semester", "E-Mail");

		CSVExporter<Student> exporter = new CSVExporter<>(students, headers, converter);
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(byteArrayOutputStream);
			exporter.write(writer);
			writer.flush();
			return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		} catch (IOException ex) {
			throw new WebApplicationException("could not parse csv to inputstream");
		}

	}

	private String getSubject(String fwpmTitle) {
		return mailProperties.getProperty("LECTURERS_MAIL_SUBJECT").replace("{FWPM_NAME}", fwpmTitle);
	}

	private String getText(String fwpmTitle) {
		return mailProperties.getProperty("LECTURERS_MAIL").replace("{FWPM_NAME}", fwpmTitle);
	}

	private String getSubject() {
		return mailProperties.getProperty("STUDENT_MAIL_SUBJECT");
	}

	private String getStudentText(List<Fwpm> fwpms) {
		String result;
		if (fwpms.size() == 1) {
			result = mailProperties.getProperty("STUDENT_MAIL_SINGULAR");
		} else {
			result = mailProperties.getProperty("STUDENT_MAIL_PLURAL");
		}

		StringBuffer fwpmString = new StringBuffer();
		fwpmString.append("<ul>");
		for (Fwpm fwpm : fwpms) {
			fwpmString.append("<li>" + fwpm.getTitle() + "</li>");
		}
		fwpmString.append("</ul>");

		return result.replace("{FWPMS}", fwpmString.toString()).replace("{DEAN}", mailProperties.getProperty("DEAN_MAIL"));
	}

	/**
	 * This method is just for testing proposes.
	 * It wraps the private getCsvFile method to make it assessable from outside
	 * You MUST NOT use this method elsewhere!
	 */
	public InputStream testMethod(List<Student> students) {

		return getCsvFile(students);
	}

}
