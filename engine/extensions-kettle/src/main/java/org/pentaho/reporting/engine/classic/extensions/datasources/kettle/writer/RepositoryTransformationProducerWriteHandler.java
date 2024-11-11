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

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromRepositoryProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class RepositoryTransformationProducerWriteHandler implements TransformationProducerWriteHandler {
  public RepositoryTransformationProducerWriteHandler() {
  }

  public void writeKettleRepositoryProducer( final WriteableDocumentBundle bundle,
                                             final String dataSourceFileName,
                                             final XmlWriter xmlWriter,
                                             final String queryName,
                                             final KettleTransformationProducer producer )
    throws IOException, BundleWriterException {
    if ( producer instanceof KettleTransFromRepositoryProducer == false ) {
      throw new BundleWriterException
        ( "Invalid object type registered for handler of " + KettleTransFromRepositoryProducer.class.getSimpleName() );
    }
    KettleTransFromRepositoryProducer repositoryProducer = (KettleTransFromRepositoryProducer) producer;

    final AttributeList coreAttrs = new AttributeList();
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "name", queryName );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "repository", repositoryProducer.getRepositoryName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "directory", repositoryProducer.getDirectoryName() );
    coreAttrs
      .setAttribute( KettleDataFactoryModule.NAMESPACE, "transformation", repositoryProducer.getTransformationName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "step", repositoryProducer.getStepName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "username", repositoryProducer.getUsername() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "password",
      PasswordEncryptionService.getInstance().encrypt( repositoryProducer.getPassword() ) );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "stop-on-error",
      String.valueOf( repositoryProducer.isStopOnError() ) );

    final String[] definedArgumentNames = repositoryProducer.getDefinedArgumentNames();
    final ParameterMapping[] parameterMappings = repositoryProducer.getDefinedVariableNames();
    if ( definedArgumentNames.length == 0 && parameterMappings.length == 0 ) {
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-repository", coreAttrs, XmlWriter.CLOSE );
      return;
    }

    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-repository", coreAttrs, XmlWriter.OPEN );
    TransformationProducerWriteHandlerLib.writeParameterAndArguments( xmlWriter, repositoryProducer );
    xmlWriter.writeCloseTag();
  }

}
