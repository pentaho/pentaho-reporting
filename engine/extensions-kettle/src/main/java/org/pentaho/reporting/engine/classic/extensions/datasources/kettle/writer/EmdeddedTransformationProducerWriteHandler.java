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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.OutputStream;

public class EmdeddedTransformationProducerWriteHandler implements TransformationProducerWriteHandler {
  public EmdeddedTransformationProducerWriteHandler() {
  }

  public void writeKettleRepositoryProducer( final WriteableDocumentBundle bundle,
                                             final String dataSourceFileName,
                                             final XmlWriter xmlWriter,
                                             final String queryName,
                                             final KettleTransformationProducer producer )
    throws IOException, BundleWriterException {
    if ( producer instanceof EmbeddedKettleTransformationProducer == false ) {
      throw new BundleWriterException
        ( "Invalid object type registered for handler of " + EmbeddedKettleTransformationProducer.class
          .getSimpleName() );
    }
    EmbeddedKettleTransformationProducer fileProducer = (EmbeddedKettleTransformationProducer) producer;


    String absoluteResourceName = writeFile( bundle, dataSourceFileName, queryName, fileProducer );

    final AttributeList coreAttrs = new AttributeList();
    // the name is static for now
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "name", queryName );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "plugin-id", fileProducer.getPluginId() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "stop-on-error",
      String.valueOf( fileProducer.isStopOnError() ) );

    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-embedded", coreAttrs, XmlWriter.OPEN );

    // Now writes the name of the file that the KTR is stored in.
    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "resource", XmlWriter.OPEN );
    final String resourceName = IOUtils.getInstance().createRelativePath( absoluteResourceName, dataSourceFileName );
    xmlWriter.writeText( resourceName );
    xmlWriter.writeCloseTag();

    TransformationProducerWriteHandlerLib.writeParameterAndArguments( xmlWriter, fileProducer );
    xmlWriter.writeCloseTag();
  }

  private String writeFile( final WriteableDocumentBundle bundle,
                            final String contextFileName,
                            final String queryName,
                            final EmbeddedKettleTransformationProducer fileProducer )
    throws IOException {

    final String fileName = BundleUtilities.getUniqueName( bundle, contextFileName, queryName + "{0}.ktr" );
    if ( fileName == null ) {
      throw new IOException( "Unable to generate unique name for unified datasource. " );
    }

    OutputStream outputStream = null;
    try {
      outputStream = bundle.createEntry( fileName, "text/xml" );
      outputStream.write( fileProducer.getTransformationRaw() );
    } finally {
      if ( outputStream != null ) {
        outputStream.flush();
        outputStream.close();
      }
    }
    return fileName;
  }

}
