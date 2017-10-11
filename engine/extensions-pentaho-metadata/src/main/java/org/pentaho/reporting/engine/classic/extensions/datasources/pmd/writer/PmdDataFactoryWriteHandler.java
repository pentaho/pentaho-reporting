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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactoryModule;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class PmdDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public PmdDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter the writer context that holds all factories.
   * @param xmlWriter    the XML writer that will receive the generated XML data.
   * @param dataFactory  the data factory that should be written.
   * @throws IOException           if any error occured
   * @throws ReportWriterException if the data factory cannot be written.
   */
  public void write( final ReportWriterContext reportWriter, final XmlWriter xmlWriter, final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final PmdDataFactory df = (PmdDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", PmdDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "pmd-datasource", rootAttrs, XmlWriter.OPEN );

    final AttributeList configAttrs = new AttributeList();
    configAttrs.setAttribute( PmdDataFactoryModule.NAMESPACE, "domain", String.valueOf( df.getDomainId() ) );
    configAttrs.setAttribute( PmdDataFactoryModule.NAMESPACE, "xmi-file", String.valueOf( df.getXmiFile() ) );
    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );


    final String globalScript = df.getGlobalScript();
    final String globalScriptLanguage = df.getGlobalScriptLanguage();
    if ( StringUtils.isEmpty( globalScript ) == false && StringUtils.isEmpty( globalScriptLanguage ) == false ) {
      xmlWriter.writeTag
        ( PmdDataFactoryModule.NAMESPACE, "global-script", "language", globalScriptLanguage, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( globalScript, false );
      xmlWriter.writeCloseTag();
    }

    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "query-definitions", XmlWriterSupport.OPEN );
    final String[] queryNames = df.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final String query = df.getQuery( queryName );
      xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );

      xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "static-query", XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();

      final String queryScriptLanguage = df.getScriptingLanguage( queryName );
      final String queryScript = df.getScript( queryName );

      if ( StringUtils.isEmpty( queryScript ) == false &&
        ( StringUtils.isEmpty( queryScriptLanguage ) == false
          || StringUtils.isEmpty( globalScriptLanguage ) == false ) ) {
        if ( StringUtils.isEmpty( queryScriptLanguage ) ) {
          xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "script", XmlWriterSupport.OPEN );
        } else {
          xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "script", "language", queryScriptLanguage,
            XmlWriterSupport.OPEN );
        }
        xmlWriter.writeTextNormalized( queryScript, false );
        xmlWriter.writeCloseTag();
      }

      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();

    xmlWriter.writeCloseTag();
  }

}
