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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 19, 2007, 5:03:22 PM
 *
 * @author Thomas Morgner
 */
public class DriverConnectionProviderWriteHandler
  implements OlapConnectionProviderWriteHandler {
  public DriverConnectionProviderWriteHandler() {
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

    final DriverConnectionProvider driverProvider =
      (DriverConnectionProvider) connectionProvider;
    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "connection", XmlWriterSupport.OPEN );

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "driver", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getDriver(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "url", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getUrl(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "properties", XmlWriterSupport.OPEN );
    final String[] propertyNames = driverProvider.getPropertyNames();
    for ( int i = 0; i < propertyNames.length; i++ ) {
      final String name = propertyNames[ i ];
      final String value = driverProvider.getProperty( name );
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "property", "name", name, XmlWriterSupport.OPEN );
      if ( name.toLowerCase().contains( "password" ) ) {
        xmlWriter.writeTextNormalized( PasswordEncryptionService.getInstance().encrypt( value ), false );
      } else {
        xmlWriter.writeTextNormalized( value, false );
      }
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
    return null;
  }
}
