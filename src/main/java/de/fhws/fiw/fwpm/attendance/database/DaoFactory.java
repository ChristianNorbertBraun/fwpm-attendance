package de.fhws.fiw.fwpm.attendance.database;

import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDao;
import de.fhws.fiw.fwpm.attendance.database.daos.AssignmentDaoImplementation;

public class DaoFactory
{
    private static DaoFactory instance;

    private Persistency persistency;
    private AssignmentDao assignmentDao;

    private DaoFactory( boolean deleteDatabase )
    {
        persistency = Persistency.getInstance( deleteDatabase );
    }

    public static DaoFactory getInstance()
    {
        return getInstance( false );
    }

    public static DaoFactory getInstance( boolean deleteDatabase )
    {
        if ( instance == null )
        {
            instance = new DaoFactory( deleteDatabase );
        }
        return instance;
    }

    public AssignmentDao createAssignmentDao()
    {
        if ( assignmentDao == null )
        {
            assignmentDao = new AssignmentDaoImplementation( persistency );
        }
        return assignmentDao;
    }
}