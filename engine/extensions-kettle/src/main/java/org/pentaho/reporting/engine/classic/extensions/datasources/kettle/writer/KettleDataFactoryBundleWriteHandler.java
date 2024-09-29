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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromRepositoryProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class KettleDataFactoryBundleWriteHandler implements BundleDataFactoryWriterHandler {
  public KettleDataFactoryBundleWriteHandler() {
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
   * @throws IOException           if any error occured
   * @throws BundleWriterException if a bundle-management error occured.
   */
  public String writeDataFactory( final WriteableDocumentBundle bundle,
                                  final DataFactory dataFactory,
                                  final BundleWriterState state )
    throws IOException, BundleWriterException {
    final String fileName =
      BundleUtilities.getUniqueName( bundle, state.getFileName(), "datasources/kettle-ds{0}.xml" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for Inline-Data-Source" );
    }

    final OutputStream outputStream = bundle.createEntry( fileName, "text/xml" );
    final DefaultTagDescription tagDescription = new DefaultTagDescription
      ( ClassicEngineBoot.getInstance().getGlobalConfig(), KettleDataFactoryModule.TAG_DEF_PREFIX );
    final XmlWriter xmlWriter =
      new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );

    final KettleDataFactory kettleDataFactory = (KettleDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", KettleDataFactoryModule.NAMESPACE );
    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "kettle-datasource", rootAttrs, XmlWriter.OPEN );

    final String[] queryNames = kettleDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final KettleTransformationProducer prod = kettleDataFactory.getQuery( queryName );
      final TransformationProducerWriteHandler handler = lookupWriteHandler( prod );
      handler.writeKettleRepositoryProducer( bundle, fileName, xmlWriter, queryName, prod );
    }
    xmlWriter.writeCloseTag();
    xmlWriter.close();
    return fileName;
  }

  protected TransformationProducerWriteHandler lookupWriteHandler( KettleTransformationProducer prod )
    throws BundleWriterException {
    if ( prod instanceof KettleTransFromFileProducer ) {
      return new FileTransformationProducerWriteHandler();
    } else if ( prod instanceof KettleTransFromRepositoryProducer ) {
      return new RepositoryTransformationProducerWriteHandler();
    } else if ( prod instanceof EmbeddedKettleTransformationProducer ) {
      return new EmdeddedTransformationProducerWriteHandler();
    } else {
      throw new BundleWriterException( "Failed to write Kettle-Producer: Unknown implementation." );
    }

  }

}
