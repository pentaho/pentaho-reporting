/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 19, 2007, 5:03:22 PM
 *
 * @author Thomas Morgner
 */
public class JndiConnectionProviderWriteHandler
  implements OlapConnectionProviderWriteHandler {
  public JndiConnectionProviderWriteHandler() {
  }

  public String writeReport( final XmlWriter xmlWriter,
                             final OlapConnectionProvider connectionProvider )
    throws IOException, BundleWriterException {
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    final JndiConnectionProvider driverProvider =
      (JndiConnectionProvider) connectionProvider;
    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "jndi", XmlWriterSupport.OPEN );

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "path", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getConnectionPath(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
    return null;
  }
}
