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


package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.staticdata;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.NamedStaticDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.staticdata.StaticDataFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: Jan 18, 2007, 6:41:57 PM
 *
 * @author Thomas Morgner
 */
public class StaticDataFactoryWriterHandler implements DataFactoryWriteHandler {
  public StaticDataFactoryWriterHandler() {
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
   *           if any error occured
   * @throws ReportWriterException
   *           if the data factory cannot be written.
   */
  public void write( final ReportWriterContext reportWriter, final XmlWriter xmlWriter, final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    if ( reportWriter == null ) {
      throw new NullPointerException();
    }
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }

    final NamedStaticDataFactory staticDataFactory = (NamedStaticDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    if ( xmlWriter.isNamespaceDefined( StaticDataFactoryModule.NAMESPACE ) == false ) {
      rootAttrs.addNamespaceDeclaration( "data", StaticDataFactoryModule.NAMESPACE );
    }
    xmlWriter.writeTag( StaticDataFactoryModule.NAMESPACE, "static-datasource", rootAttrs, XmlWriterSupport.OPEN );

    final String[] queryNames = staticDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[i];
      final String query = staticDataFactory.getQuery( queryName );
      xmlWriter.writeTag( StaticDataFactoryModule.NAMESPACE, "query", "name", queryName, XmlWriterSupport.OPEN );
      xmlWriter.writeTextNormalized( query, false );
      xmlWriter.writeCloseTag();
    }
    xmlWriter.writeCloseTag();
  }
}
