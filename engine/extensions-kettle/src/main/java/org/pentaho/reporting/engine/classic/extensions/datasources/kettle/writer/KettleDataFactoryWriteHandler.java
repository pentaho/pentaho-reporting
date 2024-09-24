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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.DataFactoryWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromRepositoryProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

/**
 * Creation-Date: Jan 19, 2007, 4:44:05 PM
 *
 * @author Thomas Morgner
 */
public class KettleDataFactoryWriteHandler implements DataFactoryWriteHandler {
  public KettleDataFactoryWriteHandler() {
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
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) dataFactory;

    final AttributeList rootAttrs = new AttributeList();
    rootAttrs.addNamespaceDeclaration( "data", KettleDataFactoryModule.NAMESPACE );
    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "kettle-datasource", rootAttrs, XmlWriter.OPEN );

    final String[] queryNames = kettleDataFactory.getQueryNames();
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[ i ];
      final KettleTransformationProducer prod = kettleDataFactory.getQuery( queryName );
      if ( prod instanceof KettleTransFromFileProducer ) {
        writeKettleFileProducer( xmlWriter, queryName, (KettleTransFromFileProducer) prod );
      } else if ( prod instanceof KettleTransFromRepositoryProducer ) {
        writeKettleRepositoryProducer( xmlWriter, queryName, (KettleTransFromRepositoryProducer) prod );
      } else {
        throw new ReportWriterException( "Failed to write Kettle-Producer: Unknown implementation." );
      }
    }
    xmlWriter.writeCloseTag();
  }

  private void writeKettleFileProducer( final XmlWriter xmlWriter,
                                        final String queryName,
                                        final KettleTransFromFileProducer fileProducer )
    throws IOException {
    final AttributeList coreAttrs = new AttributeList();
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "name", queryName );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "repository", fileProducer.getRepositoryName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "filename", fileProducer.getTransformationFile() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "step", fileProducer.getStepName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "username", fileProducer.getUsername() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "password", fileProducer.getPassword() );
    final FormulaArgument[] definedArgumentNames = fileProducer.getArguments();
    final FormulaParameter[] parameterMappings = fileProducer.getParameter();
    if ( definedArgumentNames.length == 0 && parameterMappings.length == 0 ) {
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-file", coreAttrs, XmlWriter.CLOSE );
      return;
    }

    xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-file", coreAttrs, XmlWriter.OPEN );
    writeParameterAndArguments( xmlWriter, fileProducer );
    xmlWriter.writeCloseTag();
  }

  private void writeParameterAndArguments( final XmlWriter xmlWriter,
                                           final AbstractKettleTransformationProducer fileProducer )
    throws IOException {
    final FormulaArgument[] definedArgumentNames = fileProducer.getArguments();
    final FormulaParameter[] parameterMappings = fileProducer.getParameter();
    for ( int i = 0; i < definedArgumentNames.length; i++ ) {
      final FormulaArgument arg = definedArgumentNames[ i ];
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "argument", "formula", arg.getFormula(), XmlWriter.CLOSE );
    }

    for ( int i = 0; i < parameterMappings.length; i++ ) {
      final FormulaParameter parameterMapping = parameterMappings[ i ];
      final AttributeList paramAttr = new AttributeList();
      paramAttr.setAttribute( KettleDataFactoryModule.NAMESPACE, "variable-name", parameterMapping.getName() );
      paramAttr.setAttribute( KettleDataFactoryModule.NAMESPACE, "formula", parameterMapping.getFormula() );
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "variable", paramAttr, XmlWriter.CLOSE );
    }
  }

  private void writeKettleRepositoryProducer( final XmlWriter xmlWriter,
                                              final String queryName,
                                              final KettleTransFromRepositoryProducer repositoryProducer )
    throws IOException {
    final AttributeList coreAttrs = new AttributeList();
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "name", queryName );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "repository", repositoryProducer.getRepositoryName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "directory", repositoryProducer.getDirectoryName() );
    coreAttrs
      .setAttribute( KettleDataFactoryModule.NAMESPACE, "transformation", repositoryProducer.getTransformationName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "step", repositoryProducer.getStepName() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "username", repositoryProducer.getUsername() );
    coreAttrs.setAttribute( KettleDataFactoryModule.NAMESPACE, "password", repositoryProducer.getPassword() );

    final String[] definedArgumentNames = repositoryProducer.getDefinedArgumentNames();
    final ParameterMapping[] parameterMappings = repositoryProducer.getDefinedVariableNames();
    if ( definedArgumentNames.length == 0 && parameterMappings.length == 0 ) {
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "query-repository", coreAttrs, XmlWriter.CLOSE );
      return;
    }
    writeParameterAndArguments( xmlWriter, repositoryProducer );
    xmlWriter.writeCloseTag();
  }

}
