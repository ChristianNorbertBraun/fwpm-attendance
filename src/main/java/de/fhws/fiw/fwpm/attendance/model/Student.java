package de.fhws.fiw.fwpm.attendance.model;

import com.webcohesion.enunciate.metadata.DocumentationExample;

public class Student
{
    private String kNummer;
    private String firstName;
    private String lastName;
    private String major;
    private int    semester;
    private String eMail;

    public Student()
    {}

    public Student( String kNummer, String firstName, String lastName, String major, int semester, String eMail ) {
        this.kNummer = kNummer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.semester = semester;
        this.eMail = eMail;
    }

    @DocumentationExample( "k12345" )
    public String getkNummer() {
        return kNummer;
    }

    public void setkNummer( String kNummer ) {
        this.kNummer = kNummer;
    }

    @DocumentationExample( "Alfred" )
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    @DocumentationExample( "Spaghetti" )
    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    @DocumentationExample( "Computer Science" )
    public String getMajor() {
        return major;
    }

    public void setMajor( String major ) {
        this.major = major;
    }

    @DocumentationExample( "6" )
    public int getSemester() {
        return semester;
    }

    public void setSemester( int semester ) {
        this.semester = semester;
    }

    @DocumentationExample( "alfred.spaghetti@fhws.de" )
    public String geteMail() {
        return eMail;
    }

    public void seteMail( String eMail ) {
        this.eMail = eMail;
    }
}
