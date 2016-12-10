package de.fhws.fiw.fwpm.attendance.database.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentsTable extends AbstractTable
{
    public static final String TABLE_NAME = "Students";

    public static final String FIELD_KNR        = "knr";
    public static final String FIELD_FIRST_NAME = "first_name";
    public static final String FIELD_LAST_NAME  = "last_name";
    public static final String FIELD_MAJOR      = "major";
    public static final String FIELD_SEMESTER   = "semester";
    public static final String FIELD_EMAIL      = "email";


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
                    FIELD_KNR        + " varchar(255) NOT NULL, " +
                    FIELD_FIRST_NAME + " varchar(255) NOT NULL, " +
                    FIELD_LAST_NAME  + " varchar(255) NOT NULL, " +
                    FIELD_MAJOR      + " varchar(255) NOT NULL, " +
                    FIELD_SEMESTER   + " int unsigned NOT NULL, " +
                    FIELD_EMAIL      + " varchar(255) NOT NULL, " +
                    "PRIMARY KEY (" + FIELD_KNR + ")" +
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