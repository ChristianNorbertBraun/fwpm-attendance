package de.fhws.fiw.fwpm.attendance.api;

import de.fhws.fiw.fwpm.attendance.authentication.AuthFilter;
import de.fhws.fiw.fwpm.attendance.database.Persistency;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

@ApplicationPath( "/api" )
public class AttendanceApplication extends ResourceConfig {
    public AttendanceApplication() {
        registerClasses( getResourceClasses() );
        registerContainerLifecycleListener();
        register( RolesAllowedDynamicFeature.class );
        register( new AuthFilter() );
        register( new CorsFilter() );

        Persistency.getInstance( false );
    }

    public Set< Class< ? > > getResourceClasses() {
        final Set< Class< ? > > classes = new HashSet<>();
        classes.add( AttendanceEndpoints.class );
        classes.add( HardshipEndpoints.class );
        return classes;
    }

    private void registerContainerLifecycleListener() {
        register( new ContainerLifecycleListener() {
            @Override
            public void onStartup( Container container ) {
            }

            @Override
            public void onReload( Container container ) {
            }

            @Override
            public void onShutdown( Container container ) {
                Persistency.getInstance( false ).closeConnectionPool();
                AuthFilter.userCache.clear();
            }
        } );
    }
}