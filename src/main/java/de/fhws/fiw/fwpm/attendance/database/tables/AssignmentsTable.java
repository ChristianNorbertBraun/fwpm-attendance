package de.fhws.fiw.fwpm.attendance.database.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AssignmentsTable extends AbstractTable
{
    public static final String TABLE_NAME             = "Assignments";
    public static final String FIELD_ID               = "id";
    public static final String FIELD_ASSIGNMENT_DATE  = "assignment_date";
    public static final String FIELD_SATISFACTION     = "satisfaction";
    public static final String FIELD_MAX_SATISFACTION = "max_satisfaction";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    protected void createTable( Connection connection )
    {
        try
        {
            String query = "CREATE TABLE IF NOT EXISTS " + getTableName() + "(" +
                    FIELD_ID               + " bigint unsigned NOT NULL, " +
                    FIELD_ASSIGNMENT_DATE  + " timestamp NOT NULL, " +
                    FIELD_SATISFACTION     + " int NOT NULL, " +
                    FIELD_MAX_SATISFACTION + " int NOT NULL, " +
                    "PRIMARY KEY (" + FIELD_ID + ")" +
                    ")";

            final Statement statement;
            statement = connection.createStatement();
            statement.executeUpdate( query );
            statement.close();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }
}