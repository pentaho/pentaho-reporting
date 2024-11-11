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


package org.pentaho.reporting.engine.classic.core.states.datarow;

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

/**
 * This is a internal class that provides a expression-runtime for internal purposes. This class can change without
 * notice and may even be deleted or refactored at any time. Depend on it outside of the report-core project and be
 * doomed.
 *
 * @author Thomas Morgner
 */
public class InlineDataRowRuntime implements ExpressionRuntime {
  private ReportState state;
  private GroupingState groupingState;

  public InlineDataRowRuntime() {
  }

  public ReportState getState() {
    return state;
  }

  public void setState( final ReportState state ) {
    this.state = state;
    this.groupingState = null;
  }

  public DataSchema getDataSchema() {
    return state.getFlowController().getDataSchema();
  }

  public DataRow getDataRow() {
    return state.getDataRow();
  }

  public Configuration getConfiguration() {
    return getProcessingContext().getConfiguration();
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return state.getResourceBundleFactory();
  }

  public DataFactory getDataFactory() {
    return state.getFlowController().getDataFactory();
  }

  /**
   * Access to the tablemodel was granted using report properties, now direct.
   */
  public TableModel getData() {
    final DefaultFlowController flowController = state.getFlowController();
    final MasterDataRow masterRow = flowController.getMasterRow();
    return masterRow.getReportData();
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

  public ProcessingContext getProcessingContext() {
    return state.getFlowController().getReportContext();
  }

  public int getCurrentGroup() {
    if ( groupingState == null ) {
      groupingState = state.createGroupingState();
    }
    return groupingState.getCurrentGroup();
  }

  public int getGroupStartRow( final String groupName ) {
    if ( groupingState == null ) {
      groupingState = state.createGroupingState();
    }
    return groupingState.getGroupStartRow( groupName );
  }

  public int getGroupStartRow( final int groupIndex ) {
    if ( groupingState == null ) {
      groupingState = state.createGroupingState();
    }
    return groupingState.getGroupStartRow( groupIndex );
  }

  public boolean isStructuralComplexReport() {
    return state.isStructuralPreprocessingNeeded();
  }

  public boolean isCrosstabActive() {
    return state.isCrosstabActive();
  }
}
