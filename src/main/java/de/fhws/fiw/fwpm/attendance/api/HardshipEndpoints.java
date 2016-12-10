package de.fhws.fiw.fwpm.attendance.api;

import de.fhws.fiw.fwpm.attendance.authentication.Roles;
import de.fhws.fiw.fwpm.attendance.database.DaoFactory;
import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDao;
import de.fhws.fiw.fwpm.attendance.model.Hardship;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.SQLException;

@Path("hardships")
public class HardshipEndpoints {

    @Context
    UriInfo uriInfo;

    private AssignmentDao assignmentDao;

    @PostConstruct
    public void init() {
        assignmentDao = DaoFactory.getInstance().createAssignmentDao();
    }

    /**
     * Updates several database entries to solve a hardship case.
     * @HTTP 200 for successful request
     * @HTTP 500 for database error
     * @param hardship The info to solve a hardship case
     */
    @PUT
    @RolesAllowed( Roles.EMPLOYEE )
    @Consumes(MediaType.APPLICATION_JSON)
    public Response solveHardship(Hardship hardship) {

        try {
            if (hardship.getFwpmIds().size() > 0) {
                assignmentDao.solveHardship(hardship);
            }
        } catch (SQLException ex) {
            throw new WebApplicationException("SQL Problems", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.ok().build();
    }
}