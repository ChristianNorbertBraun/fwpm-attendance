package de.fhws.fiw.fwpm.attendance.database.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class FwpmsTable extends AbstractTable
{
    public static final String TABLE_NAME = "Fwpms";

    public static final String FIELD_ID                        = "id";
    public static final String FIELD_MODULE_NUMBER             = "module_number";
    public static final String FIELD_TITLE                     = "title";
    public static final String FIELD_RESPONSIBLE_TEACHER       = "responsible_teacher";
    public static final String FIELD_RESPONSIBLE_TEACHER_EMAIL = "responsible_teacher_email";
    public static final String FIELD_NUMBER_OF_SEATS           = "number_of_seats";

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
                    FIELD_ID                        + " bigint unsigned NOT NULL, " +
                    FIELD_TITLE                     + " varchar(255) NOT NULL, " +
                    FIELD_MODULE_NUMBER             + " int NOT NULL, " +
                    FIELD_RESPONSIBLE_TEACHER_EMAIL + " varchar(255) NOT NULL, " +
                    FIELD_RESPONSIBLE_TEACHER       + " varchar(255) NOT NULL, " +
                    FIELD_NUMBER_OF_SEATS           + " int NOT NULL, " +
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