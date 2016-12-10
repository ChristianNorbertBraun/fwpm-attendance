package de.fhws.fiw.fwpm.attendance.database.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AssignmentEntriesTable extends AbstractTable
{
    public static final String TABLE_NAME = "AssignmentEntries";

    public static final String FIELD_ASSIGNMENT_ID = "assignment_id";
    public static final String FIELD_FWPM_ID       = "fwpm_id";
    public static final String FIELD_STUDENT_KNR   = "student_id";


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
                    FIELD_ASSIGNMENT_ID + " bigint unsigned NOT NULL, " +
                    FIELD_FWPM_ID       + " bigint unsigned NOT NULL, " +
                    FIELD_STUDENT_KNR   + " varchar(255) NOT NULL, " +
                    "PRIMARY KEY (" + FIELD_ASSIGNMENT_ID + ", " + FIELD_FWPM_ID + ", " + FIELD_STUDENT_KNR + ")" +
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
