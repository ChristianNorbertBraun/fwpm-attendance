package de.fhws.fiw.fwpm.attendance.model;

import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Assignment
{
    private long         id;
    private Date         date;
    private List< Fwpm > fwpms;
    private int          totalSatisfaction;
    private int          totalMaxSatisfaction;

    public Assignment()
    {
        this.fwpms = new LinkedList<>();
    }

    public Assignment( long id, Date date, List< Fwpm > fwpms )
    {
        this.id    = id;
        this.date  = date;
        this.fwpms = fwpms;
        this.totalSatisfaction = 0;
        this.totalMaxSatisfaction = 0;
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
            Assignment other = (Assignment) obj;

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

    @DocumentationExample( "01/01/1990-12:34:56 PST" )
    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }

    public List< Fwpm > getFwpms() {
        return fwpms;
    }

    public void setFwpms( List< Fwpm > fwpms ) {
        this.fwpms = fwpms;
    }

    @DocumentationExample( "99" )
    public int getTotalSatisfaction() {
        return totalSatisfaction;
    }

    public void setTotalSatisfaction( int totalSatisfaction ) {
        this.totalSatisfaction = totalSatisfaction;
    }

    @DocumentationExample( "100" )
    public int getTotalMaxSatisfaction() {
        return totalMaxSatisfaction;
    }

    public void setTotalMaxSatisfaction( int totalMaxSatisfaction ) {
        this.totalMaxSatisfaction = totalMaxSatisfaction;
    }
}