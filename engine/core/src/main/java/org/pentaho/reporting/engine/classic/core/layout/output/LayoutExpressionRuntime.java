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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.GroupingState;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.swing.table.TableModel;

public class LayoutExpressionRuntime implements ExpressionRuntime {
  private DataRow dataRow;
  private TableModel data;
  private DataSchema dataSchema;
  private ProcessingContext processingContext;
  private ReportState state;
  private GroupingState groupingState;

  public LayoutExpressionRuntime( final DataRow dataRow, final DataSchema dataSchema, final ReportState state,
      final TableModel data, final ProcessingContext processingContext ) {
    if ( dataSchema == null ) {
      throw new NullPointerException();
    }
    if ( processingContext == null ) {
      throw new NullPointerException();
    }
    if ( dataRow == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    this.state = state;
    this.groupingState = state.createGroupingState();
    this.dataSchema = dataSchema;
    this.processingContext = processingContext;
    this.dataRow = dataRow;
    this.data = data;
  }

  public DataFactory getDataFactory() {
    return state.getFlowController().getDataFactory();
  }

  public DataSchema getDataSchema() {
    return dataSchema;
  }

  public ProcessingContext getProcessingContext() {
    return processingContext;
  }

  public Configuration getConfiguration() {
    return getProcessingContext().getConfiguration();
  }

  public DataRow getDataRow() {
    return dataRow;
  }

  /**
   * Access to the tablemodel was granted using report properties, now direct.
   */
  public TableModel getData() {
    return data;
  }

  /**
   * Where are we in the current processing.
   */
  public int getCurrentRow() {
    return state.getCurrentRow();
  }

  public int getCurrentDataItem() {
    return state.getCurrentDataItem();
  }

  public int getCurrentGroup() {
    return groupingState.getCurrentGroup();
  }

  public int getGroupStartRow( final String groupName ) {
    return groupingState.getGroupStartRow( groupName );
  }

  public int getGroupStartRow( final int groupIndex ) {
    return groupingState.getGroupStartRow( groupIndex );
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return getProcessingContext().getResourceBundleFactory();
  }

  /**
   * The output descriptor is a simple string collections consisting of the following components:
   * exportclass/type/subtype
   * <p/>
   * For example, the PDF export would be: pageable/pdf The StreamHTML export would return table/html/stream
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return getProcessingContext().getExportDescriptor();
  }

  public boolean isStructuralComplexReport() {
    return state.isStructuralPreprocessingNeeded();
  }

  public boolean isCrosstabActive() {
    return state.isCrosstabActive();
  }
}
