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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.writer;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.SQLDataFactoryModule;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class DriverConnectionProviderWriteHandler implements ConnectionProviderWriteHandler {
  public DriverConnectionProviderWriteHandler() {
  }

  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final ConnectionProvider connectionProvider ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    final DriverConnectionProvider driverProvider = (DriverConnectionProvider) connectionProvider;
    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "connection", XmlWriterSupport.OPEN );

    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "driver", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getDriver(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "url", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getUrl(), false );
    xmlWriter.writeCloseTag();

    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "properties", XmlWriterSupport.OPEN );
    final String[] propertyNames = driverProvider.getPropertyNames();
    for ( int i = 0; i < propertyNames.length; i++ ) {
      final String name = propertyNames[i];
      final String value = driverProvider.getProperty( name );
      xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "property", "name", name, XmlWriterSupport.OPEN );
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
