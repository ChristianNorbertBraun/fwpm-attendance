package de.fhws.fiw.fwpm.attendance.log;

import java.util.Date;

public class Logger
{
    public static void log( String message )
    {
        System.out.println( new Date( System.currentTimeMillis() ) + " ATTENDANCE SERVICE: " + message );
    }
}