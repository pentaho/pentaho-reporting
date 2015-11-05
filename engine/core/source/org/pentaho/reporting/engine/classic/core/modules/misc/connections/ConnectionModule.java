package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser.DatabaseConnectionsXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser.DatabaseConnectionsXmlResourceFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class ConnectionModule extends AbstractModule {
  public static final String NAMESPACE =
      "http://reporting.pentaho.org/namespaces/engine/classic/extensions/connections/1.0";

  public ConnectionModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    DatabaseConnectionsXmlResourceFactory.register( DatabaseConnectionsXmlFactoryModule.class );
  }

}
