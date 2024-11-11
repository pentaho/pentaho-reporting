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

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class FileTransformationProducerWriteHandler implements TransformationProducerWriteHandler {
  public FileTransformationProducerWriteHandler() {
  }

  public void writeKettleRepositoryProducer( final WriteableDocumentBundle bundle,
                                             final String dataSourceFileName,
                                             final XmlWriter xmlWriter,
                                             final String queryName,
                                             final KettleTransformationProducer producer )
    throws IOException, BundleWriterException {
    if ( producer instanceof KettleTransFromFileProducer == false ) {
      throw new BundleWriterException
        ( "Invalid object type registered for handler of " + KettleTransFromFileProducer.class.getSimpleName() );
    }
    KettleTransFromFileProducer fileProducer = (KettleTransFromFileProducer) producer;

    final AttributeList coreAttrs = new AttributeList();
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "name", queryName );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "repository", fileProducer.getRepositoryName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "filename", fileProducer.getTransformationFile() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "step", fileProducer.getStepName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "username", fileProducer.getUsername() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "password",
      PasswordEncryptionService.getInstance().encrypt( fileProducer.getPassword() ) );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "stop-on-error",
      String.valueOf( fileProducer.isStopOnError() ) );

    final String[] definedArgumentNames = fileProducer.getDefinedArgumentNames();
    final ParameterMapping[] parameterMappings = fileProducer.getDefinedVariableNames();
    if ( definedArgumentNames.length == 0 && parameterMappings.length == 0 ) {
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-file", coreAttrs, XmlWriter.CLOSE );
      return;
    }

    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-file", coreAttrs, XmlWriter.OPEN );
    TransformationProducerWriteHandlerLib.writeParameterAndArguments( xmlWriter, fileProducer );
    xmlWriter.writeCloseTag();
  }

}
