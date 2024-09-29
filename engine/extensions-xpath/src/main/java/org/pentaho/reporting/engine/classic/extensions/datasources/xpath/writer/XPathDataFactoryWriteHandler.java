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


package org.pentaho.reporting.engine.classic.extensions.datasources.xpath.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.XPathDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.XPathDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class XPathDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public XPathDataFactoryWriteHandler() {
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
  public void write( final ReportWriterContext reportWriter,
                     final XmlWriter xmlWriter,
                     final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final XPathDataFactory pmdDataFactory = (XPathDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", XPathDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( XPathDataFactoryModule.NAMESPACE, "xpath-datasource", rootAttrs, XmlWriter.OPEN );

    final AttributeList configAttrs = new AttributeList();
    configAttrs.setAttribute( XPathDataFactoryModule.NAMESPACE,
      "source-file", String.valueOf( pmdDataFactory.getXqueryDataFile() ) );
    xmlWriter.writeTag( XPathDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    final String[] queryNames = pmdDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final XPathDataFactory.QueryDefinition query = pmdDataFactory.getQuery( queryName );
      final AttributeList attributes = new AttributeList();
      attributes.setAttribute( XPathDataFactoryModule.NAMESPACE, "name", queryName );
      if ( query.isLegacyQuery() ) {
        attributes.setAttribute( XPathDataFactoryModule.NAMESPACE, "query-mode", "legacy" );
      } else {
        attributes.setAttribute( XPathDataFactoryModule.NAMESPACE, "query-mode", "flex" );
      }
      xmlWriter.writeTag( XPathDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query.getXpathExpression(), false );
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
  }
}
