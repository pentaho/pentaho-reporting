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
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.SQLDataFactoryModule;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class JndiConnectionProviderWriteHandler implements ConnectionProviderWriteHandler {
  public JndiConnectionProviderWriteHandler() {
  }

  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final ConnectionProvider connectionProvider ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    final JndiConnectionProvider driverProvider = (JndiConnectionProvider) connectionProvider;
    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "jndi", XmlWriterSupport.OPEN );

    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "path", XmlWriterSupport.OPEN );
    xmlWriter.writeTextNormalized( driverProvider.getConnectionPath(), false );
    xmlWriter.writeCloseTag();

    if ( driverProvider.getUsername() != null ) {
      xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "username", XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( driverProvider.getUsername(), false );
      xmlWriter.writeCloseTag();

      if ( driverProvider.getPassword() != null ) {
        xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "password", XmlWriterSupport.OPEN );
        xmlWriter.writeTextNormalized( PasswordEncryptionService.getInstance().encrypt( driverProvider.getPassword() ),
            false );
        xmlWriter.writeCloseTag();
      }
    }

    xmlWriter.writeCloseTag();
    return null;
  }
}
