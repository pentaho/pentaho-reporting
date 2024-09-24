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
