/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.writer;

import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class ScriptableDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public ScriptableDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter
   *          the writer context that holds all factories.
   * @param xmlWriter
   *          the XML writer that will receive the generated XML data.
   * @param dataFactory
   *          the data factory that should be written.
   * @throws IOException
   *           if any error occurred
   * @throws ReportWriterException
   *           if the data factory cannot be written.
   */
  public void write( final ReportWriterContext reportWriter, final XmlWriter xmlWriter, final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final ScriptableDataFactory scriptableDataFactory = (ScriptableDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", ScriptableDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( ScriptableDataFactoryModule.NAMESPACE, "scriptable-datasource", rootAttrs, XmlWriter.OPEN );

    final AttributeList configAttrs = new AttributeList();
    configAttrs.setAttribute( ScriptableDataFactoryModule.NAMESPACE, "language", String.valueOf( scriptableDataFactory
        .getLanguage() ) );
    if ( !StringUtils.isEmpty( scriptableDataFactory.getScript() ) ) {
      configAttrs.setAttribute( ScriptableDataFactoryModule.NAMESPACE, "script", String.valueOf( scriptableDataFactory
          .getScript() ) );
    }
    if ( !StringUtils.isEmpty( scriptableDataFactory.getShutdownScript() ) ) {
      configAttrs.setAttribute( ScriptableDataFactoryModule.NAMESPACE, "shutdown-script", String
          .valueOf( scriptableDataFactory.getShutdownScript() ) );
    }
    xmlWriter.writeTag( ScriptableDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    final String[] queryNames = scriptableDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[i];
      final String query = scriptableDataFactory.getQuery( queryName );
      xmlWriter.writeTag( ScriptableDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
  }
}
