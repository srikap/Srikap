package org.myorg.main;


import org.myorg.db.MetricsDAO;
import org.myorg.db.MongoStore;
import org.myorg.monitor.rest.NodeMetrics;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class Configurator extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {

            @Override
            protected void configureServlets() {

                /* bind the REST resources */
                bind(NodeMetrics.class);
        		bind(MetricsDAO.class).to(MongoStore.class).asEagerSingleton();
                serve("/rest/*").with(GuiceContainer.class);

            }
        });
    }
}