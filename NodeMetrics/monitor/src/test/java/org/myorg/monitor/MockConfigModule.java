package org.myorg.monitor;

import org.myorg.db.MetricsDAO;
import org.myorg.db.MongoStore;
import org.myorg.monitor.impl.NodeCommandFactoryImpl;
import org.myorg.monitor.impl.NodeMonitorServiceImpl;
import org.myorg.monitor.impl.NodeRequestDispatcherImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Configures with mock objects to facilitate testing.
 * @author srikap
 *
 */
public class MockConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MetricsDAO.class).to(MongoStore.class).asEagerSingleton();
		bind(NodeCommandFactory.class).to(NodeCommandFactoryImpl.class).asEagerSingleton();
		bind(NodeCommand.class).to(MockCommand.class);
		bind(NodeRequestDispatcher.class).to(NodeRequestDispatcherImpl.class).asEagerSingleton();
		bind(NodeMonitorService.class).to(NodeMonitorServiceImpl.class);
	}

	/**
	 * Singleton
	 */
	private static final Injector inj = Guice.createInjector(new MockConfigModule());
	public static Injector getInjector()
	{
		return inj;
	}


}
