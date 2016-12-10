package de.fhws.fiw.fwpm.attendance.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertySingleton
{
    private static Properties instance;
    private static InputStream inputStream;

    private PropertySingleton(){};

    public static Properties getInstance(boolean createNew, String fileName) throws IOException
    {
        if ( instance == null || createNew )
        {
            instance = new Properties();

            inputStream = PropertySingleton.class.getResourceAsStream("/" + fileName);
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");

            instance.load(reader);
        }
        return instance;
    }

    public static Properties getInstance() throws IOException
    {
        return getInstance( false, "attendance.properties" );
    }
}
