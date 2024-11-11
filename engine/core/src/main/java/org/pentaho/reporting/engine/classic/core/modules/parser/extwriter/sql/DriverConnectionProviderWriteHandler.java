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


package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.sql;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.SQLDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 19, 2007, 5:03:22 PM
 *
 * @author Thomas Morgner
 */
public class DriverConnectionProviderWriteHandler implements ConnectionProviderWriteHandler {
  public DriverConnectionProviderWriteHandler() {
  }

  public void write( final ReportWriterContext reportWriter, final XmlWriter xmlWriter,
      final ConnectionProvider connectionProvider ) throws IOException, ReportWriterException {
    if ( reportWriter == null ) {
      throw new NullPointerException();
    }
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }

    final DriverConnectionProvider driverProvider = (DriverConnectionProvider) connectionProvider;

    final AttributeList rootAttribs = new AttributeList();
    if ( xmlWriter.isNamespaceDefined( SQLDataFactoryModule.NAMESPACE ) == false ) {
      rootAttribs.addNamespaceDeclaration( "data", SQLDataFactoryModule.NAMESPACE );
    }

    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "connection", rootAttribs, XmlWriterSupport.OPEN );

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
      xmlWriter.writeTextNormalized( value, false );
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
  }
}
