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

package org.pentaho.reporting.designer.core.model.data;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.designtime.AbstractDesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaCompiler;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class TemporaryDataSchemaModel extends AbstractDesignTimeDataSchemaModel {
  private DataSchema dataSchema;

  public TemporaryDataSchemaModel( final MasterReport masterReportElement,
                                   final AbstractReportDefinition parent ) {
    super( masterReportElement, parent );
  }

  protected DataSchemaDefinition createDataSchemaDefinition( final MasterReport masterReportElement ) {
    DataSchemaDefinition dataSchemaDefinition = masterReportElement.getDataSchemaDefinition();
    if ( dataSchemaDefinition == null ) {
      return DataSchemaUtility.parseDefaults( masterReportElement.getResourceManager() );
    }
    return dataSchemaDefinition;
  }

  public Throwable getDataFactoryException() {
    return null;
  }

  public boolean isValid() {
    return false;
  }

  public DataSchema getDataSchema() {
    if ( dataSchema == null ) {
      final ParameterDataRow parameterRow = computeParameterData();
      final ParameterDefinitionEntry[] parameterDefinitions = computeParameterDefinitionEntries();

      final ResourceManager resourceManager = getMasterReportElement().getResourceManager();
      final DataSchemaCompiler dataSchemaCompiler =
        new DataSchemaCompiler( getDataSchemaDefinition(), getDataAttributeContext(), resourceManager );

      try {
        final TableModel reportData = new DefaultTableModel();
        final Expression[] expressions = getReport().getExpressions().getExpressions();
        final ReportEnvironment reportEnvironment = getMasterReportElement().getReportEnvironment();
        dataSchema = dataSchemaCompiler.compile
          ( reportData, expressions, parameterRow, parameterDefinitions, reportEnvironment );

      } catch ( final ReportDataFactoryException e ) {
        dataSchema = new DefaultDataSchema();
      }
    }

    return dataSchema;
  }
}
