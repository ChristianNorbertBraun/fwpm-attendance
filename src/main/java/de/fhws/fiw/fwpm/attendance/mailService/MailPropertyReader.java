package de.fhws.fiw.fwpm.attendance.mailService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by marcelgross on 23.08.16.
 */
public class MailPropertyReader
{
	private static Properties instance;
	private static InputStream inputStream;

	private MailPropertyReader(){}

	public static Properties getInstance(boolean createNew, String fileName) throws IOException
	{
		if ( instance == null || createNew )
		{
			instance = new Properties();

			inputStream = MailPropertyReader.class.getResourceAsStream("/" + fileName);
			instance.load(inputStream);
		}
		return instance;
	}

	public static Properties getInstance() throws IOException
	{
		return getInstance( false, "mail.properties" );
	}
}