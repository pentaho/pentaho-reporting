/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
