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
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.SimplePmdDataFactory;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

public class SimplePmdDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public SimplePmdDataFactoryWriteHandler() {
  }

  /**
   * Writes a data-source into a XML-stream.
   *
   * @param reportWriter the writer context that holds all factories.
   * @param xmlWriter    the XML writer that will receive the generated XML data.
   * @param dataFactory  the data factory that should be written.
   * @throws java.io.IOException                                                                      if any error
   * occured
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException if the data
   * factory
   *                                                                                                  cannot be written.
   */
  public void write( final ReportWriterContext reportWriter, final XmlWriter xmlWriter, final DataFactory dataFactory )
    throws IOException, ReportWriterException {
    final SimplePmdDataFactory pmdDataFactory = (SimplePmdDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", PmdDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "simple-pmd-datasource", rootAttrs, XmlWriter.OPEN );

    final AttributeList configAttrs = new AttributeList();
    configAttrs
      .setAttribute( PmdDataFactoryModule.NAMESPACE, "domain", String.valueOf( pmdDataFactory.getDomainId() ) );
    configAttrs
      .setAttribute( PmdDataFactoryModule.NAMESPACE, "xmi-file", String.valueOf( pmdDataFactory.getXmiFile() ) );
    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    xmlWriter.writeCloseTag();
  }

}
