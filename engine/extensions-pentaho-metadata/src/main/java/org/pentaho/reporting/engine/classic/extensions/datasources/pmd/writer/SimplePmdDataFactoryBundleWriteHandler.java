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
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.SimplePmdDataFactory;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SimplePmdDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler {
  public SimplePmdDataFactoryBundleWriteHandler() {
  }

  /**
   * Writes a data-source into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle      the bundle where to write to.
   * @param dataFactory the data factory that should be written.
   * @param state       the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws java.io.IOException                                                                          if any error
   *                                                                                                      occured
   * @throws org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException if a
   * bundle-management
   *                                                                                                      error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle, final DataFactory dataFactory,
                                  final BundleWriterState state ) throws IOException,
    BundleWriterException {
    final String fileName = BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/pmd-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Inline-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setNamespaceHasCData( PmdDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( PmdDataFactoryModule.NAMESPACE, "global-script", true );
    tagDescription.setElementHasCData( PmdDataFactoryModule.NAMESPACE, "script", true );
    tagDescription.setElementHasCData( PmdDataFactoryModule.NAMESPACE, "static-query", true );

    final XmlWriter xmlWriter =
      new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", PmdDataFactoryModule.NAMESPACE );

    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "simple-pmd-datasource", rootAttrs, XmlWriter.OPEN );

    final SimplePmdDataFactory pmdDataFactory = (SimplePmdDataFactory) dataFactory;

    final AttributeList configAttrs = new AttributeList();
    configAttrs
      .setAttribute( PmdDataFactoryModule.NAMESPACE, "domain", String.valueOf( pmdDataFactory.getDomainId() ) );
    configAttrs
      .setAttribute( PmdDataFactoryModule.NAMESPACE, "xmi-file", String.valueOf( pmdDataFactory.getXmiFile() ) );
    xmlWriter.writeTag( PmdDataFactoryModule.NAMESPACE, "config", configAttrs, XmlWriterSupport.CLOSE );

    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }

}
