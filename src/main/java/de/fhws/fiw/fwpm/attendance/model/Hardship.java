package de.fhws.fiw.fwpm.attendance.model;

import java.util.List;

public class Hardship {

	private Student student;
	private long assignmentId;
	private List<Long> fwpmIds;

	public Hardship() {
	}

	public Hardship(Student student, long assignmentId, List<Long> fwpmIds) {
		this.student = student;
		this.assignmentId = assignmentId;
		this.fwpmIds = fwpmIds;
	}

	public List<Long> getFwpmIds() {
		return fwpmIds;
	}

	public void setFwpmIds(List<Long> fwpmIds) {
		this.fwpmIds = fwpmIds;
	}

	public long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
