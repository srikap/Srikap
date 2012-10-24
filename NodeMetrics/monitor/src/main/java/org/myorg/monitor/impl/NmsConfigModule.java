package org.myorg.monitor.impl;

import org.myorg.db.MetricsDAO;
import org.myorg.db.MongoStore;
import org.myorg.monitor.NodeCommand;
import org.myorg.monitor.NodeCommandFactory;
import org.myorg.monitor.NodeMonitorService;
import org.myorg.monitor.NodeRequestDispatcher;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Guice Injector. Configures Dependency Injection.
 * Singleton access to let Objects gain access to Objects created with in guice container
 * @author srikap
 *
 */
public class NmsConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MetricsDAO.class).to(MongoStore.class).asEagerSingleton();
		bind(NodeCommandFactory.class).to(NodeCommandFactoryImpl.class).asEagerSingleton();
		bind(NodeRequestDispatcher.class).to(NodeRequestDispatcherImpl.class).asEagerSingleton();
		bind(NodeCommand.class).to(NodeCommandImpl.class);
		bind(NodeMonitorService.class).to(NodeMonitorServiceImpl.class);
	}

	/**
	 * Singleton
	 */
	private static final Injector inj = Guice.createInjector(new NmsConfigModule());
	public static Injector getInjector()
	{
		return inj;
	}


}
