package de.fhws.fiw.fwpm.attendance.model;

import java.util.ArrayList;
import java.util.List;

public class ExtendedStudent extends Student {

	private List<Fwpm> participatingFwpms;

	public ExtendedStudent() {
		super();
		this.participatingFwpms = new ArrayList<>();
	}

	public ExtendedStudent(String kNummer, String firstName, String lastName, String major, int semester, String eMail,
			List<Fwpm> participatingFwpms) {
		super(kNummer, firstName, lastName, major, semester, eMail);
		this.participatingFwpms = participatingFwpms;
	}

	public ExtendedStudent(Student student, List<Fwpm> participatingFwpms) {
		super(student.getkNummer(), student.getFirstName(), student.getLastName(), student.getMajor(), student.getSemester(), student.geteMail());
		this.participatingFwpms = participatingFwpms;
	}

	public List<Fwpm> getParticipatingFwpms() {
		return participatingFwpms;
	}

	public void setParticipatingFwpms(List<Fwpm> participatingFwpms) {
		this.participatingFwpms = participatingFwpms;
	}
}