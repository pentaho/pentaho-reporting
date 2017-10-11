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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.writer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.SQLDataFactoryModule;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class DirectSqlDataFactoryWriterHandler implements BundleDataFactoryWriterHandler {

  public DirectSqlDataFactoryWriterHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occured
   * @throws BundleWriterException
   *           if a bundle-management error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle, final DataFactory dataFactory,
      final BundleWriterState state ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    final SimpleSQLReportDataFactory sqlDataFactory = (SimpleSQLReportDataFactory) dataFactory;

    final String fileName =
        BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/direct-sql-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for SQL-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );

    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setNamespaceHasCData( SQLDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "driver", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "password", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "path", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "property", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "static-query", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "script", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "global-script", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "url", true );
    tagDescription.setElementHasCData( SQLDataFactoryModule.NAMESPACE, "username", true );

    final XmlWriter xmlWriter =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", SQLDataFactoryModule.NAMESPACE );
    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "direct-sql-datasource", rootAttrs, XmlWriterSupport.OPEN );

    final AttributeList configAttrs = new AttributeList();
    configAttrs.setAttribute( SQLDataFactoryModule.NAMESPACE, "user-field", sqlDataFactory.getUserField() );
    configAttrs.setAttribute( SQLDataFactoryModule.NAMESPACE, "password-field", sqlDataFactory.getPasswordField() );
    xmlWriter.writeTag( SQLDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    writeConnectionInfo( bundle, state, xmlWriter, sqlDataFactory.getConnectionProvider() );

    xmlWriter.writeCloseTag();
    xmlWriter.close();

    return fileName;
  }

  private void writeConnectionInfo( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final ConnectionProvider connectionProvider ) throws IOException,
    BundleWriterException {
    final String configKey = SQLDataFactoryModule.CONNECTION_WRITER_PREFIX + connectionProvider.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value != null ) {
      final ConnectionProviderWriteHandler handler =
          ObjectUtilities.loadAndInstantiate( value, SQLReportDataFactory.class, ConnectionProviderWriteHandler.class );
      if ( handler != null ) {
        handler.writeReport( bundle, state, xmlWriter, connectionProvider );
      }
    }
  }

}
