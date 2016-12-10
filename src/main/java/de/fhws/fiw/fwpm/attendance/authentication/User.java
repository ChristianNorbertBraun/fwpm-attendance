package de.fhws.fiw.fwpm.attendance.authentication;

import com.owlike.genson.annotation.JsonProperty;

import java.security.Principal;

public class User implements Principal
{
    private String token;
    private String firstName;
    private String lastName;
    private String email;
    // KNummer
    private String cn;
    private String role;
    private int semester;
    private String facultyName;


    @Override
    public String getName() {
        return cn;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    //Annotations werden benötigt um sich der Schnittstelle von Herrn Braun anzupassen
    @JsonProperty("emailAddress")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("studentNumber")
    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getSemester() {
        return semester;
    }


    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}