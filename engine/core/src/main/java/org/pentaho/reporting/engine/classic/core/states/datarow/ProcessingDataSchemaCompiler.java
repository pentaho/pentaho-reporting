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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaCompiler;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DirectFieldSelectorRule;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.MetaSelectorRule;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;

public class ProcessingDataSchemaCompiler extends DataSchemaCompiler {
  private static final Log logger = LogFactory.getLog( ProcessingDataSchemaCompiler.class );
  private ResourceManager resourceManager;
  private DataSchemaDefinition globalDefaults;

  public ProcessingDataSchemaCompiler( final DataSchemaDefinition reportSchemaDefinition,
      final DataAttributeContext context, final ResourceManager resourceManager,
      final DataSchemaDefinition globalDefaults ) {
    super( reportSchemaDefinition, context, resourceManager );
    this.resourceManager = resourceManager;
    this.globalDefaults = globalDefaults;
  }

  protected DataSchemaDefinition parseGlobalDefaults( final ResourceManager resourceManager ) {
    if ( globalDefaults == null ) {
      globalDefaults = super.parseGlobalDefaults( resourceManager );
    }
    return globalDefaults;
  }

  public DataSchemaDefinition getGlobalDefaults() {
    return globalDefaults;
  }

  public DataSchema compile( final MasterDataRow masterRow, final ReportEnvironment reportEnvironment )
    throws ReportDataFactoryException {
    if ( masterRow == null ) {
      throw new NullPointerException();
    }
    if ( isInitialized() == false ) {
      init();
    }

    final DefaultDataAttributes globalAttributes = getGlobalAttributes();
    final MetaSelectorRule[] indirectRules = getIndirectRules();
    final DirectFieldSelectorRule[] directRules = getDirectRules();
    final DataAttributeContext context = getContext();
    final ParameterDataRow parameters = masterRow.getParameterDataRow();
    final ExpressionDataRow expressionsRow = masterRow.getExpressionDataRow();
    final ReportDataRow massDataRow = masterRow.getReportDataRow();
    // imported data has been compiled in the subreport ...
    final ImportedVariablesDataRow importedDataRow = masterRow.getImportedDataRow();

    final DefaultDataSchema defaultDataSchema = new DefaultDataSchema();

    if ( parameters != null ) {
      final MasterDataRow parentRow = masterRow.getParentDataRow();
      if ( parentRow == null ) {
        processParameters( parameters, null, reportEnvironment, globalAttributes, indirectRules, directRules,
            defaultDataSchema );
      } else {
        // import the parameters that have been computed already ..
        final String[] parameterNames = parameters.getParentNames();
        final String[] innerNames = parameters.getColumnNames();
        for ( int i = 0; i < parameterNames.length; i++ ) {
          final String name = parameterNames[i];
          final DataAttributes attributes = parentRow.getDataSchema().getAttributes( name );
          defaultDataSchema.setAttributes( innerNames[i], attributes );
        }
      }
    }

    // expressions
    final Expression[] expressions = expressionsRow.getExpressions();
    for ( int i = 0; i < expressions.length; i++ ) {
      final Expression expression = expressions[i];
      final String name = expression.getName();
      if ( name == null ) {
        continue;
      }
      final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
      computedParameterDataAttributes.merge( globalAttributes, context );
      computedParameterDataAttributes.merge( new ExpressionsDataAttributes( expression ), context );

      applyRules( indirectRules, directRules, computedParameterDataAttributes );
      defaultDataSchema.setAttributes( name, computedParameterDataAttributes );
    }

    // massdata
    if ( massDataRow != null ) {
      final GenericDataAttributes parameterDataAttributes = getTableDataAttributes();
      final TableModel data = massDataRow.getReportData();
      if ( data instanceof MetaTableModel == false ) {
        final int count = data.getColumnCount();
        for ( int i = 0; i < count; i++ ) {
          final String colName = data.getColumnName( i );
          parameterDataAttributes.setup( colName, data.getColumnClass( i ), MetaAttributeNames.Core.SOURCE_VALUE_TABLE,
              colName, globalAttributes );

          final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
          computedParameterDataAttributes.merge( parameterDataAttributes, context );
          applyRules( indirectRules, directRules, computedParameterDataAttributes );
          defaultDataSchema.setAttributes( colName, computedParameterDataAttributes );
        }
      } else {
        final MetaTableModel mt = (MetaTableModel) data;

        final DefaultDataAttributes tableGlobalAttributes = new DefaultDataAttributes();
        tableGlobalAttributes.merge( globalAttributes, context );
        tableGlobalAttributes.merge( mt.getTableAttributes(), context );
        try {
          defaultDataSchema.setTableAttributes( tableGlobalAttributes );
        } catch ( CloneNotSupportedException e ) {
          logger.warn( "Unable to copy global data-attributes", e );
        }

        final int count = data.getColumnCount();
        for ( int i = 0; i < count; i++ ) {
          final String colName = data.getColumnName( i );
          final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
          computedParameterDataAttributes.merge( tableGlobalAttributes, context );
          computedParameterDataAttributes.merge( mt.getColumnAttributes( i ), context );

          parameterDataAttributes.setup( colName, data.getColumnClass( i ), MetaAttributeNames.Core.SOURCE_VALUE_TABLE,
              null, EmptyDataAttributes.INSTANCE );
          computedParameterDataAttributes.merge( parameterDataAttributes, context );

          applyRules( indirectRules, directRules, computedParameterDataAttributes );
          defaultDataSchema.setAttributes( colName, computedParameterDataAttributes );
        }
      }
    }

    // imported values ...
    if ( importedDataRow != null ) {
      final String[] columnNames = importedDataRow.getColumnNames();
      for ( int i = 0; i < columnNames.length; i++ ) {
        final String columnName = columnNames[i];
        defaultDataSchema.setAttributes( columnName, importedDataRow.getAttributes( columnName ) );
      }
    }
    return defaultDataSchema;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }
}
