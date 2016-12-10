package de.fhws.fiw.fwpm.attendance.model;

import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

public class Fwpm
{
    private long            id;
    private int             moduleNumber;
    private String          title;
    private String          responsibleTeacher;
    private String          responsibleTeacherEmail;
    private int             numberOfSeats;
    private List< Student > participants;

    public Fwpm()
    {
        this.participants = new LinkedList<>();
    }

    public Fwpm( long id, int moduleNumber, String title, String responsibleTeacher, String responsibleTeacherEmail, int numberOfSeats, List< Student > participants ) {
        this.id = id;
        this.moduleNumber = moduleNumber;
        this.title = title;
        this.responsibleTeacher = responsibleTeacher;
        this.responsibleTeacherEmail = responsibleTeacherEmail;
        this.numberOfSeats = numberOfSeats;
        this.participants = participants;
    }

    @Override
    public boolean equals( Object obj )
    {
        boolean equals = false;

        if ( obj == null )
        {
            equals = false;
        }
        else if ( obj == this )
        {
            equals = true;
        }
        else if ( obj.getClass() != getClass() )
        {
            equals = false;
        }
        else
        {
            Fwpm other = (Fwpm) obj;

            equals = new EqualsBuilder()
                    .append( id, other.id )
                    .isEquals();
        }
        return equals;
    }

    @Override
    public int hashCode()
    {
        int hashCode = new HashCodeBuilder( 17, 37 )
                .append( id )
                .toHashCode();

        return hashCode;
    }

    @DocumentationExample( "100" )
    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    @DocumentationExample( "100" )
    public int getModuleNumber() {
        return moduleNumber;
    }

    public void setModuleNumber( int moduleNumber ) {
        this.moduleNumber = moduleNumber;
    }

    @DocumentationExample( "FWPM Example" )
    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    @DocumentationExample( "Alfred Spaghetti" )
    public String getResponsibleTeacher() {
        return responsibleTeacher;
    }

    public void setResponsibleTeacher( String responsibleTeacher ) {
        this.responsibleTeacher = responsibleTeacher;
    }

    @DocumentationExample( "alfred.spaghetti@fhws.de" )
    public String getResponsibleTeacherEmail() {
        return responsibleTeacherEmail;
    }

    public void setResponsibleTeacherEmail( String responsibleTeacherEmail ) {
        this.responsibleTeacherEmail = responsibleTeacherEmail;
    }

    @DocumentationExample( "15" )
    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats( int numberOfSeats ) {
        this.numberOfSeats = numberOfSeats;
    }

    public List< Student > getParticipants() {
        return participants;
    }

    public void setParticipants( List< Student > participants ) {
        this.participants = participants;
    }
}
