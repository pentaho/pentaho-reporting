/*
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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public class DataDefinitionFileWriter implements BundleWriterHandler {
  public DataDefinitionFileWriter() {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 100000;
  }

  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }

    final BundleWriterState contentState = new BundleWriterState( state, state.getReport(), "datadefinition.xml" ); // NON-NLS

    final OutputStream outputStream =
        new BufferedOutputStream( bundle.createEntry( contentState.getFileName(), "text/xml" ) );
    final DefaultTagDescription tagDescription = BundleWriterHandlerRegistry.getInstance().createWriterTagDescription();
    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );
    writer.writeXmlDeclaration( "UTF-8" );

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration( "", BundleNamespaces.DATADEFINITION );

    writer.writeTag( BundleNamespaces.DATADEFINITION, "data-definition", rootAttributes, XmlWriterSupport.OPEN ); // NON-NLS

    // write parameters
    writeParameterDefinitions( contentState, writer );
    // write data-source
    final AbstractReportDefinition report = contentState.getReport();

    final String query = report.getQuery();
    final int queryLimit = report.getQueryLimit();
    final int timeout = report.getQueryTimeout();
    final AttributeList dataSourceAtts = new AttributeList();
    if ( query != null ) {
      dataSourceAtts.setAttribute( BundleNamespaces.DATADEFINITION, "report-query", query ); // NON-NLS
    }
    dataSourceAtts.setAttribute( BundleNamespaces.DATADEFINITION, "limit", String.valueOf( queryLimit ) ); // NON-NLS
    dataSourceAtts.setAttribute( BundleNamespaces.DATADEFINITION, "timeout", String.valueOf( timeout ) ); // NON-NLS
    if ( report instanceof MasterReport ) {
      final MasterReport masterReport = (MasterReport) report;
      final String dataSourceDefFile = writeDataFactoryWrapper( bundle, contentState, masterReport.getDataFactory() );
      final String relativePath =
          IOUtils.getInstance().createRelativePath( dataSourceDefFile, contentState.getFileName() );
      dataSourceAtts.setAttribute( BundleNamespaces.DATADEFINITION, "ref", relativePath ); // NON-NLS
    } else if ( report instanceof SubReport ) {
      final SubReport subreport = (SubReport) report;
      if ( subreport.getDataFactory() != null ) {
        final String dataSourceDefFile = writeDataFactoryWrapper( bundle, contentState, subreport.getDataFactory() );
        final String relativePath =
            IOUtils.getInstance().createRelativePath( dataSourceDefFile, contentState.getFileName() );
        dataSourceAtts.setAttribute( BundleNamespaces.DATADEFINITION, "ref", relativePath ); // NON-NLS
      }
    }
    writer.writeTag( BundleNamespaces.DATADEFINITION, "data-source", dataSourceAtts, XmlWriterSupport.CLOSE ); // NON-NLS

    // write expressions
    ExpressionWriterUtility.writeDataExpressions( bundle, contentState, writer );

    writer.writeCloseTag();
    writer.close();

    return contentState.getFileName();
  }

  private void writeParameterDefinitions( final BundleWriterState state, final XmlWriter writer ) throws IOException,
    BundleWriterException {
    final AbstractReportDefinition report = state.getReport();
    if ( report instanceof SubReport ) {
      writer.writeTag( BundleNamespaces.DATADEFINITION, "parameter-mapping", XmlWriterSupport.OPEN ); // NON-NLS
      final SubReport subReport = (SubReport) report;
      final ParameterMapping[] inputMappings = subReport.getInputMappings();
      for ( int i = 0; i < inputMappings.length; i++ ) {
        final ParameterMapping mapping = inputMappings[i];
        final AttributeList attList = new AttributeList();
        attList.setAttribute( BundleNamespaces.DATADEFINITION, "name", mapping.getName() ); // NON-NLS
        attList.setAttribute( BundleNamespaces.DATADEFINITION, "alias", mapping.getAlias() ); // NON-NLS
        writer.writeTag( BundleNamespaces.DATADEFINITION, "input-parameter", attList, XmlWriterSupport.CLOSE ); // NON-NLS
      }

      final ParameterMapping[] exportMappings = subReport.getExportMappings();
      for ( int i = 0; i < exportMappings.length; i++ ) {
        final ParameterMapping mapping = exportMappings[i];
        final AttributeList attList = new AttributeList();
        attList.setAttribute( BundleNamespaces.DATADEFINITION, "name", mapping.getName() ); // NON-NLS
        attList.setAttribute( BundleNamespaces.DATADEFINITION, "alias", mapping.getAlias() ); // NON-NLS
        writer.writeTag( BundleNamespaces.DATADEFINITION, "export-parameter", attList, XmlWriterSupport.CLOSE ); // NON-NLS
      }
      writer.writeCloseTag();
      return;
    }

    if ( report instanceof MasterReport ) {
      final MasterReport masterReport = (MasterReport) report;
      final ReportParameterDefinition definition = masterReport.getParameterDefinition();
      final ReportParameterValidator reportParameterValidator = definition.getValidator();

      final AttributeList attList = new AttributeList();
      if ( DefaultReportParameterValidator.class.equals( reportParameterValidator.getClass() ) == false ) {
        attList.setAttribute( BundleNamespaces.DATADEFINITION, "validator", // NON-NLS
            reportParameterValidator.getClass().getName() );
      }
      writer.writeTag( BundleNamespaces.DATADEFINITION, "parameter-definition", attList, XmlWriterSupport.OPEN ); // NON-NLS

      writeMasterReportParameters( writer, definition );
      writer.writeCloseTag();
    }
  }

  private static void writeMasterReportParameters( final XmlWriter writer, final ReportParameterDefinition definition )
    throws BundleWriterException, IOException {
    final ParameterDefinitionEntry[] parameterDefinitions = definition.getParameterDefinitions();
    for ( int i = 0; i < parameterDefinitions.length; i++ ) {
      final ParameterDefinitionEntry entry = parameterDefinitions[i];
      if ( entry instanceof PlainParameter ) {
        writePlainParameter( writer, (PlainParameter) entry );
      } else if ( entry instanceof ListParameter ) {
        writeListSelectionParameter( writer, (ListParameter) entry );
      }
    }
  }

  private static void writePlainParameter( final XmlWriter writer, final PlainParameter plainParameter )
    throws BundleWriterException, IOException {
    if ( StringUtils.isEmpty( plainParameter.getName() ) ) {
      throw new BundleWriterException( "Cannot write a unnamed parameter entry." );
    }

    final AttributeList paramAttrs = new AttributeList();
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "name", plainParameter.getName() ); // NON-NLS
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "mandatory", String
        .valueOf( plainParameter.isMandatory() ) ); // NON-NLS
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "type", plainParameter.getValueType().getName() ); // NON-NLS

    final Object defaultValue = plainParameter.getDefaultValue();
    if ( defaultValue != null ) {
      try {
        final String valAsString = ConverterRegistry.toAttributeValue( defaultValue );
        if ( StringUtils.isEmpty( valAsString ) == false ) {
          paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "default-value", valAsString ); // NON-NLS
        }
      } catch ( BeanException e ) {
        throw new BundleWriterException( "Unable to convert parameter " + "default-value to string for parameter '"
            + plainParameter.getName() + '\'' );
      }
    }

    final String[] namespaces = plainParameter.getParameterAttributeNamespaces();
    if ( namespaces.length == 0 ) {
      writer.writeTag( BundleNamespaces.DATADEFINITION, "plain-parameter", paramAttrs, XmlWriterSupport.CLOSE ); // NON-NLS
    } else {
      writer.writeTag( BundleNamespaces.DATADEFINITION, "plain-parameter", paramAttrs, XmlWriterSupport.OPEN ); // NON-NLS
      for ( int j = 0; j < namespaces.length; j++ ) {
        final String namespace = namespaces[j];
        final String[] names = plainParameter.getParameterAttributeNames( namespace );
        for ( int k = 0; k < names.length; k++ ) {
          final String name = names[k];
          final String value = plainParameter.getParameterAttribute( namespace, name );
          if ( StringUtils.isEmpty( value ) ) {
            continue;
          }
          final AttributeList attrsAttr = new AttributeList();
          attrsAttr.setAttribute( BundleNamespaces.DATADEFINITION, "namespace", namespace ); // NON-NLS
          attrsAttr.setAttribute( BundleNamespaces.DATADEFINITION, "name", name ); // NON-NLS
          writer.writeTag( BundleNamespaces.DATADEFINITION, "attribute", attrsAttr, XmlWriterSupport.OPEN ); // NON-NLS
          writer.writeTextNormalized( value, false );
          writer.writeCloseTag();
        }
      }
      writer.writeCloseTag();
    }
  }

  private static void writeListSelectionParameter( final XmlWriter writer, final ListParameter parameter )
    throws BundleWriterException, IOException {
    if ( StringUtils.isEmpty( parameter.getName() ) ) {
      throw new BundleWriterException( "Cannot write a unnamed parameter entry." );
    }

    final AttributeList paramAttrs = new AttributeList();
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "name", parameter.getName() );
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "allow-multi-selection", // NON-NLS
        String.valueOf( parameter.isAllowMultiSelection() ) );
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "strict-values", // NON-NLS
        String.valueOf( parameter.isStrictValueCheck() ) );
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "mandatory", String.valueOf( parameter.isMandatory() ) ); // NON-NLS
    paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "type", parameter.getValueType().getName() ); // NON-NLS
    if ( parameter instanceof DefaultListParameter ) {
      DefaultListParameter defaultListParameter = (DefaultListParameter) parameter;
      if ( StringUtils.isEmpty( defaultListParameter.getQueryName() ) == false ) {
        paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "query", defaultListParameter.getQueryName() ); // NON-NLS
      }
      if ( StringUtils.isEmpty( defaultListParameter.getKeyColumn() ) == false ) {
        paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "key-column", defaultListParameter.getKeyColumn() ); // NON-NLS
      }
      if ( StringUtils.isEmpty( defaultListParameter.getTextColumn() ) == false ) {
        paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "value-column", defaultListParameter.getTextColumn() ); // NON-NLS
      }
    }
    final Object defaultValue = ( (AbstractParameter) parameter ).getDefaultValue();
    if ( defaultValue != null ) {
      try {
        final String valAsString = ConverterRegistry.toAttributeValue( defaultValue );
        if ( StringUtils.isEmpty( valAsString ) == false ) {
          paramAttrs.setAttribute( BundleNamespaces.DATADEFINITION, "default-value", valAsString ); // NON-NLS
        }
      } catch ( BeanException e ) {
        throw new BundleWriterException( "Unable to convert parameter " + "default-value to string for parameter '"
            + parameter.getName() + '\'' );
      }
    }
    final String[] namespaces = parameter.getParameterAttributeNamespaces();
    if ( namespaces.length == 0 ) {
      writer.writeTag( BundleNamespaces.DATADEFINITION, "list-parameter", paramAttrs, XmlWriterSupport.CLOSE ); // NON-NLS
    } else {
      writer.writeTag( BundleNamespaces.DATADEFINITION, "list-parameter", paramAttrs, XmlWriterSupport.OPEN ); // NON-NLS
      for ( int j = 0; j < namespaces.length; j++ ) {
        final String namespace = namespaces[j];
        final String[] names = parameter.getParameterAttributeNames( namespace );
        for ( int k = 0; k < names.length; k++ ) {
          final String name = names[k];
          final String value = ( (AbstractParameter) parameter ).getParameterAttribute( namespace, name );
          if ( StringUtils.isEmpty( value ) ) {
            continue;
          }

          final AttributeList attrsAttr = new AttributeList();
          attrsAttr.setAttribute( BundleNamespaces.DATADEFINITION, "namespace", namespace ); // NON-NLS
          attrsAttr.setAttribute( BundleNamespaces.DATADEFINITION, "name", name ); // NON-NLS
          writer.writeTag( BundleNamespaces.DATADEFINITION, "attribute", attrsAttr, XmlWriterSupport.OPEN ); // NON-NLS
          writer.writeTextNormalized( value, false );
          writer.writeCloseTag();
        }
      }
      writer.writeCloseTag();
    }
  }

  //package-level visibility for testing purposes
  String writeDataFactoryWrapper( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final DataFactory df ) throws IOException, BundleWriterException {
    return writeDataFactory( bundle, state, df );
  }

  public static String writeDataFactory( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final DataFactory df ) throws IOException, BundleWriterException {
    final BundleDataFactoryWriterHandler writerHandler = BundleWriterUtilities.lookupWriteHandler( df );
    if ( writerHandler == null ) {
      throw new BundleWriterException( "Unable to find writer-handler for data-factory " + df.getClass() );
    }

    final String file = writerHandler.writeDataFactory( bundle, df, state );
    if ( file == null ) {
      throw new BundleWriterException( "Data-factory writer did not create a file for " + df.getClass() );
    }
    return file;
  }
}
