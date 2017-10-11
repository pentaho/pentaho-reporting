/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
