package org.pentaho.reporting.engine.classic.core.modules.misc.connections.parser;

import org.pentaho.reporting.engine.classic.core.modules.misc.connections.ConnectionModule;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class DatabaseConnectionsXmlFactoryModule extends AbstractXmlFactoryModule {
  public DatabaseConnectionsXmlFactoryModule() {
    super( ConnectionModule.NAMESPACE, "connections" );
  }

  public XmlReadHandler createReadHandler( final XmlDocumentInfo documentInfo ) {
    return new DatabaseConnectionsReadHandler();
  }
}
