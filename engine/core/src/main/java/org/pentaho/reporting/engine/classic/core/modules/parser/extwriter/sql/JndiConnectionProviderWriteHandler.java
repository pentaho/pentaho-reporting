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
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.SQLDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Writes a JNDI connection definition into the XML file.
 *
 * @author Thomas Morgner
 */
public class JndiConnectionProviderWriteHandler implements ConnectionProviderWriteHandler {
  public JndiConnectionProviderWriteHandler() {
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

    final AttributeList rootAttribs = new AttributeList();
    if ( xmlWriter.isNamespaceDefined( SQLDataFactoryModule.NAMESPACE ) == false ) {
      rootAttribs.addNamespaceDeclaration( "data", SQLDataFactoryModule.NAMESPACE );
    }

    final JndiConnectionProvider driverProvider = (JndiConnectionProvider) connectionProvider;
    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "jndi", rootAttribs, XmlWriterSupport.OPEN );

    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "path", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getConnectionPath(), false );
    xmlWriter.writeCloseTag();

    if ( driverProvider.getUsername() != null ) {
      xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "username", XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( driverProvider.getUsername(), false );
      xmlWriter.writeCloseTag();

      if ( driverProvider.getPassword() != null ) {
        xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "password", XmlWriterSupport.OPEN );
        xmlWriter.writeTextNormalized( driverProvider.getUsername(), false );
        xmlWriter.writeCloseTag();
      }
    }

    xmlWriter.writeCloseTag();
  }
}
