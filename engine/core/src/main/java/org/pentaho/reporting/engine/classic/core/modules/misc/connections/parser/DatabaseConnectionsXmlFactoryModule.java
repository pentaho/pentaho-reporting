/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/
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
