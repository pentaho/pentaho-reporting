/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
