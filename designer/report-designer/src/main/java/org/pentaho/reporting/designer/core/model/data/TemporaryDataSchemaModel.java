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
