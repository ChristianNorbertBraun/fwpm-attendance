package de.fhws.fiw.fwpm.attendance.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.fhws.fiw.fwpm.attendance.database.tables.*;
import de.fhws.fiw.fwpm.attendance.properties.PropertySingleton;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Persistency
{
    private static Persistency instance;

    private static final String PROPERTIES_FILE_NAME = "attendance.properties";

    private static String COM_MYSQL_JDBC_DRIVER;
    private static String DATABASE_PORT;
    private static String DATABASE_HOST;
    private static String DATABASE_NAME;
    private static String USERNAME;
    private static String PASSWORD;

    private ComboPooledDataSource cpds;

    private Persistency( boolean deleteDatabase )
    {
        setProperties();
        createConnectionPool();
        createAllTables( deleteDatabase );
    }

    private void setProperties()
    {
        try
        {
            Properties properties = PropertySingleton.getInstance();

            COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
            DATABASE_PORT         = "3306";
            DATABASE_HOST         = properties.getProperty( "DATABASE_HOST" );
            DATABASE_NAME         = properties.getProperty( "DATABASE_NAME" );
            USERNAME              = properties.getProperty( "DATABASE_USER" );
            PASSWORD              = properties.getProperty( "DATABASE_PASSWORD" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public static Persistency getInstance()
    {
        return getInstance( false );
    }

    public static Persistency getInstance( boolean deleteDatabase )
    {
        if ( instance == null )
        {
            instance = new Persistency( deleteDatabase );
        }
        return instance;
    }

    public final Connection getConnection() throws SQLException
    {
        return cpds.getConnection();
    }

    protected void createConnectionPool()
    {
        try
        {
            Class.forName( COM_MYSQL_JDBC_DRIVER );
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass( COM_MYSQL_JDBC_DRIVER );
            cpds.setJdbcUrl( "jdbc:mysql://" + DATABASE_HOST + ":" + DATABASE_PORT + "/" + DATABASE_NAME + "?autoReconnect=true&useSSL=false" );
            cpds.setUser( USERNAME );
            cpds.setPassword( PASSWORD );
            cpds.setTestConnectionOnCheckout( true );
            cpds.setMinPoolSize( 5 );
            cpds.setAcquireIncrement( 5 );
            cpds.setMaxPoolSize( 50 );
        }
        catch ( Exception ex )
        {
            // Mysql driver not found
            ex.printStackTrace();
            cpds = null;
        }
    }

    public void closeConnectionPool()
    {
        System.out.println( "Shutting down connection pool." );
        cpds.close();
    }

    protected void createAllTables(boolean deleteDatabase)
    {
        Connection connection = null;
        final List<AbstractTable > tables = getAllTables();

        try
        {
            connection = getConnection();

            for ( AbstractTable table : tables )
            {
                table.initTable( deleteDatabase, connection );
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            if ( connection != null )
            {
                try
                {
                    connection.close();
                }
                catch ( SQLException ex )
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    private List<AbstractTable> getAllTables()
    {
        List<AbstractTable> tables = new ArrayList<>();
        tables.add( new FwpmsTable() );
        tables.add( new StudentsTable() );
        tables.add( new AssignmentsTable() );
        tables.add( new AssignmentEntriesTable() );

        return tables;
    }
}