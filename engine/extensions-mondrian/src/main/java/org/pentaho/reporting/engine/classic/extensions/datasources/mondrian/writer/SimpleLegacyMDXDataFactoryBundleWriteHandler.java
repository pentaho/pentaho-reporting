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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.SimpleLegacyBandedMDXDataFactory;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class SimpleLegacyMDXDataFactoryBundleWriteHandler
  extends AbstractMDXDataFactoryBundleWriteHandler {
  public SimpleLegacyMDXDataFactoryBundleWriteHandler() {
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
  public String writeDataFactory( final WriteableDocumentBundle bundle,
                                  final DataFactory dataFactory,
                                  final BundleWriterState state )
    throws IOException, BundleWriterException {
    final String fileName = BundleUtilities.getUniqueName( bundle, state.getFileName(),
      "datasources/mondrian-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Mondrian-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = createTagDescription();
    final XmlWriter xmlWriter = new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ",
      "\n" );

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", MondrianDataFactoryModule.NAMESPACE );

    xmlWriter
      .writeTag( MondrianDataFactoryModule.NAMESPACE, "simple-legacy-mdx-datasource", rootAttrs, XmlWriter.OPEN );

    final SimpleLegacyBandedMDXDataFactory mdxDataFactory = (SimpleLegacyBandedMDXDataFactory) dataFactory;
    writeBody( bundle, state, mdxDataFactory, xmlWriter );
    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }
}
