package de.fhws.fiw.fwpm.attendance.api;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import de.fhws.fiw.fwpm.attendance.Settings;
import de.fhws.fiw.fwpm.attendance.authentication.Roles;
import de.fhws.fiw.fwpm.attendance.csvUtility.CSVExporter;
import de.fhws.fiw.fwpm.attendance.database.DaoFactory;
import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDao;
import de.fhws.fiw.fwpm.attendance.log.Logger;
import de.fhws.fiw.fwpm.attendance.mailService.MailSender;
import de.fhws.fiw.fwpm.attendance.model.*;
import de.fhws.fiw.fwpm.attendance.properties.PropertySingleton;
import jersey.repackaged.com.google.common.collect.ImmutableList;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Path( "participants" )
public class AttendanceEndpoints
{
    @Context
    UriInfo uriInfo;

    private AssignmentDao assignmentDao;

    @PostConstruct
    public void init()
    {
        assignmentDao = DaoFactory.getInstance().createAssignmentDao();
    }

    /**
     * Returns a collection of all assignments as JSON array.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces( MediaType.APPLICATION_JSON )
    @TypeHint( Assignment[].class )
    public Response getAllAssignments()
    {
        Response response = null;

        try {
            List<Assignment> assignments = assignmentDao.readAllAssignments();
            response = Response.ok( assignments ).build();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Returns a single assignment as JSON.
     * @HTTP 200 for successful request
     * @HTTP 404 if no assignment with given ID found
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/{assignmentId}" )
    @TypeHint( Assignment.class )
    public Response getAssignment( @PathParam( "assignmentId" ) final long assignmentId )
    {
        Response response = null;

        try {
            Assignment assignment = assignmentDao.readAssignment( assignmentId );

            if ( assignment.getId() == assignmentId )
            {
                response = Response.ok( assignment ).build();
            }
            else
            {
                throw new WebApplicationException( Response.Status.NOT_FOUND );
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Returns all students for this assignment as JSON.
     * @HTTP 200 for successful request
     * @HTTP 404 if no assignment with given ID found
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/{assignmentId}/students" )
    @TypeHint( ExtendedStudent[].class )
    public Response getStudents( @PathParam( "assignmentId" ) final long assignmentId )
    {
        Response response = null;

        try {
            List<ExtendedStudent> extendedStudents = assignmentDao.readAllExtendedStudentsForAssignment(assignmentId);
            if ( extendedStudents != null && extendedStudents.size() > 0 )
            {
                response = Response.ok( extendedStudents ).build();
            }
            else
            {
                throw new WebApplicationException( Response.Status.NOT_FOUND );
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Returns a single FWPM as JSON.
     * @HTTP 200 for successful request
     * @HTTP 404 if no FWPM with given ID found
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/{assignmentId}/fwpms/{fwpmId}" )
    @TypeHint( Fwpm.class )
    public Response getFwpm( @PathParam( "assignmentId" ) final long assignmentId, @PathParam( "fwpmId" ) final long fwpmId )
    {
        Response response = null;

        try
        {
            Fwpm fwpm = assignmentDao.readFwpm( assignmentId, fwpmId );

            if ( fwpm != null )
            {
                response = Response.ok( fwpm ).build();
            }
            else
            {
                throw new WebApplicationException( Response.Status.NOT_FOUND );
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Returns a single FWPM as CSV.
     * @HTTP 200 for successful request
     * @HTTP 404 if no FWPM with given ID found
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/{assignmentId}/fwpms/{fwpmId}" )
    @TypeHint( Fwpm.class )
    public Response getFwpmAsCSV( @PathParam( "assignmentId" ) final long assignmentId, @PathParam( "fwpmId" ) final long fwpmId )
    {
        Response response = null;

        try
        {
            Fwpm fwpm = assignmentDao.readFwpm( assignmentId, fwpmId );

            if ( fwpm != null )
            {
                String fileName = fwpm.getModuleNumber() + ".csv";

                CSVEntryConverter<Student> converter = student -> new String[]
                {
                    String.valueOf(student.getkNummer()),
                    student.getFirstName() + " " + student.getLastName(),
                    student.getMajor(),
                    String.valueOf(student.getSemester()),
                    student.geteMail()
                };
                ImmutableList<String> headers = ImmutableList.of( "Id", "Full Name", "Studiengang", "Semester", "E-Mail" );
                CSVExporter<Student> exporter = new CSVExporter<>(fwpm.getParticipants(), headers, converter);
                response = Response.ok(exporter.toStreamingOutput()).header("Content-disposition", "attachment; filename=" + fileName).build();
            }
            else
            {
                throw new WebApplicationException( Response.Status.NOT_FOUND );
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private String getParticipatingFwpmTitle(List<Fwpm> fwpms, int index)
    {
        if (index >= fwpms.size())
        {
            return "";
        }
        return fwpms.get(index).getTitle();
    }

    private String getParticipatingFwpmModuleNumber(List<Fwpm> fwpms, int index)
    {
        if (index >= fwpms.size())
        {
            return "";
        }
        return String.valueOf(fwpms.get(index).getModuleNumber());
    }


    /**
     * Returns all Students with its fwpms as CSV.
     * @HTTP 200 for successful request
     * @HTTP 404 if no FWPM with given ID found
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/{assignmentId}/students/csv" )
    @TypeHint( ExtendedStudent[].class )
    public Response getStudentsAsCSV( @PathParam( "assignmentId" ) final long assignmentId, @PathParam( "fwpmId" ) final long fwpmId )
    {
        Response response = null;

        try
        {
            List<ExtendedStudent> extendedStudents = assignmentDao.readAllExtendedStudentsForAssignment(assignmentId);

            if ( extendedStudents != null && extendedStudents.size() > 0 )
            {
                String fileName = "Studenten.csv";

                CSVEntryConverter<ExtendedStudent> converter = student -> new String[]
                        {
                                String.valueOf(student.getkNummer()),
                                student.getFirstName() + " " + student.getLastName(),
                                student.getMajor(),
                                String.valueOf(student.getSemester()),
                                student.geteMail(),
                                getParticipatingFwpmTitle(student.getParticipatingFwpms(), 0),
                                getParticipatingFwpmModuleNumber(student.getParticipatingFwpms(), 0),
                                getParticipatingFwpmTitle(student.getParticipatingFwpms(), 1),
                                getParticipatingFwpmModuleNumber(student.getParticipatingFwpms(), 1),
                                getParticipatingFwpmTitle(student.getParticipatingFwpms(), 2),
                                getParticipatingFwpmModuleNumber(student.getParticipatingFwpms(), 2),
                                getParticipatingFwpmTitle(student.getParticipatingFwpms(), 3),
                                getParticipatingFwpmModuleNumber(student.getParticipatingFwpms(), 3)
                        };
                ImmutableList<String> headers = ImmutableList.of( "Id", "Full Name", "Studiengang", "Semester", "E-Mail", "1. FWPM", "Modulnummer", "2. FWPM", "Modulnummer", "2. FWPM", "Modulnummer" );
                CSVExporter<ExtendedStudent> exporter = new CSVExporter<>(extendedStudents, headers, converter);
                response = Response.ok(exporter.toStreamingOutput()).header("Content-disposition", "attachment; filename=" + fileName).build();
            }
            else
            {
                throw new WebApplicationException( Response.Status.NOT_FOUND );
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Creates a new assignment resource. Stores submitted assignment in database.
     * @HTTP 201 for successful request
     * @HTTP 500 for database error
     * @param assignment The assignment to store
     */
    @POST
    @RolesAllowed( Roles.API_KEY_USER )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response storeNewAssignment( Assignment assignment )
    {
        Response response = null;

        try
        {
            assignmentDao.createAssignment( assignment );
            response = Response.created( getLocationUri( assignment ) ).build();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Updates assignment with given ID.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     * @param assignment The updated assignment
     */
    @PUT
    @RolesAllowed( Roles.EMPLOYEE )
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/{assignmentId}" )
    public Response updateAssignment( Assignment assignment, @PathParam( "assignmentId" ) final long assignmentID )
    {
        Response response = null;

        try
        {
            assignmentDao.updateAssignment( assignmentID, assignment );
            response = Response.ok().build();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Adds a student to a specific FWPM within a specific assignment.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     * @param student The student to add
     */
    @PUT
    @RolesAllowed( Roles.EMPLOYEE )
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/{assignmentId}/fwpms/{fwpmId}" )
    public Response addStudentToFwpm( Student student, @PathParam( "assignmentId" ) final long assignmentId, @PathParam( "fwpmId" ) final long fwpmId )
    {
        Response response = null;

        try
        {
            assignmentDao.addStudentToFwpm( assignmentId, fwpmId, student );
            response = Response.ok().build();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Removes a student from a specific FWPM within a specific assignment.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     */
    @DELETE
    @RolesAllowed( Roles.EMPLOYEE )
    @Path( "/{assignmentId}/fwpms/{fwpmId}/students/{kNummer}" )
    public Response removeStudentFromFwpm( @PathParam( "assignmentId" ) final long assignmentId, @PathParam( "fwpmId" ) final long fwpmId, @PathParam( "kNummer" ) final String kNummer )
    {
        Response response = null;

        try {
            assignmentDao.removeStudentFromFwpm( assignmentId, fwpmId, kNummer );
            response = Response.ok().build();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Removes the assignment with given ID.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     */
    @DELETE
    @RolesAllowed( {Roles.EMPLOYEE, Roles.API_KEY_USER} )
    @Path( "/{assignmentId}" )
    public Response deleteAssignments( @PathParam( "assignmentId" ) final long assignmentId )
    {
        Response response = null;

        try
        {
            assignmentDao.deleteAssignment( assignmentId );
            response = Response.ok().build();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Checks if the assignment with given ID still contains hardship cases to solve.
     * @HTTP 200 if there are no hardship cases
     * @HTTP 409 if there still are hardship cases to solve
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Path( "/{assignmentId}/lecturermail" )
    public Response hasHardshipCases( @PathParam( "assignmentId" ) final long assignmentId )
    {
        Response response = null;

        try
        {
            boolean hasHardshipCases = assignmentDao.hasHardshipCases( assignmentId );

            if ( hasHardshipCases )
            {
                response = Response.status( Response.Status.CONFLICT ).build();
            }
            else
            {
                response = Response.ok().build();
                sendMailsToLecturer(assignmentId);
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }


    /**
     * Sends an email with their FWPMs to all students, if no hardship cases exists
     * @HTTP 200 if there are no hardship cases, and mail was send
     * @HTTP 409 if there still are hardship cases to solve
     * @HTTP 500 for database error
     */
    @GET
    @RolesAllowed( Roles.EMPLOYEE )
    @Path( "/{assignmentId}/studentmail" )
    public Response sendStudentMails( @PathParam( "assignmentId" ) final long assignmentId )
    {
        Response response = null;

        try
        {
            boolean hasHardshipCases = assignmentDao.hasHardshipCases( assignmentId );

            if ( hasHardshipCases )
            {
                response = Response.status( Response.Status.CONFLICT ).build();
            }
            else
            {
                response = Response.ok().build();
                sendMailsToStudents(assignmentId);
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Updates several database entries to solve a hardship case.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     * @param hardship The info to solve a hardship case
     */
    @PUT
    @RolesAllowed( Roles.EMPLOYEE )
    @Produces(MediaType.APPLICATION_JSON)
    public Response solveHardship( Hardship hardship )
    {
        Response response;

        try
        {
            if ( hardship.getFwpmIds().size() > 0) {
                assignmentDao.solveHardship(hardship);
            }
            response = Response.ok().build();
        } catch (SQLException e ) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }


    private void sendMailsToLecturer(long assignmentId)
    {
        String environment = getEnvironment();

        if ( environment.equals( Settings.ENVIRONMENT_LOCAL ) )
        {
            Logger.log( Settings.MSG_NO_MAILS_LOCAL );
        }
        else
        {
            new MailSender().sendLecturerMails(assignmentId);
        }
    }

    private void sendMailsToStudents( long assignmentId )
    {
        String environment = getEnvironment();

        if ( environment.equals( Settings.ENVIRONMENT_LOCAL ) )
        {
            Logger.log( Settings.MSG_NO_MAILS_LOCAL );
        }
        else
        {
            new MailSender().sendStudentMails(assignmentId);
        }
    }

    public String getEnvironment()
    {
        String environment = "";
        try
        {
            Properties properties = PropertySingleton.getInstance();
            environment = properties.getProperty( "ENVIRONMENT" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return environment;
    }

    private URI getLocationUri( Assignment assignment )
    {
        String subPath = String.valueOf( assignment.getId() );
        return uriInfo.getAbsolutePathBuilder().path( subPath ).build();
    }
}
