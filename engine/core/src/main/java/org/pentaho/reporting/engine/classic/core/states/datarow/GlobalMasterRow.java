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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.CascadingDataFactory;
import org.pentaho.reporting.engine.classic.core.states.EmptyDataFactory;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;

import javax.swing.table.TableModel;

public final class GlobalMasterRow implements MasterDataRow {
  private GlobalMasterRow parentRow;
  private FastGlobalView globalView;
  private DataFactory dataFactory;
  private DataSchemaDefinition schemaDefinition;
  private ProcessingDataSchemaCompiler schemaCompiler;
  private DataSchema dataSchema;
  private ResourceBundleFactory resourceBundleFactory;
  private OutputProcessorMetaData outputProcessorMetaData;

  private ReportEnvironmentDataRow environmentDataRow;
  private ParameterDataRow parameterDataRow;
  private ExpressionDataRow expressionDataRow;
  private ImportedVariablesDataRow importedDataRow;
  private DataProcessor paddingData;
  private DataProcessor storedPaddingData;
  private FastGlobalView storedGlobalView;

  private GlobalMasterRow() {
  }

  /**
   * Creates a new master-row. This is called only once when the report processing starts for the very first time.
   *
   * @param reportContext
   * @param schemaDefinition
   * @param parameterDataRow
   * @return
   */
  public static GlobalMasterRow createReportRow( final ProcessingContext reportContext,
      final DataSchemaDefinition schemaDefinition, final ParameterDataRow parameterDataRow ) {
    if ( reportContext == null ) {
      throw new NullPointerException();
    }
    if ( schemaDefinition == null ) {
      throw new NullPointerException();
    }
    if ( parameterDataRow == null ) {
      throw new NullPointerException();
    }

    final GlobalMasterRow gmr = new GlobalMasterRow();
    gmr.globalView = new FastGlobalView();
    gmr.paddingData = new DataProcessor();
    gmr.expressionDataRow = new ExpressionDataRow( gmr.globalView, gmr, reportContext );
    gmr.schemaDefinition = schemaDefinition;
    gmr.dataFactory = new EmptyDataFactory();
    gmr.resourceBundleFactory = reportContext.getResourceBundleFactory();
    gmr.outputProcessorMetaData = reportContext.getOutputProcessorMetaData();
    final DefaultDataAttributeContext dac =
        new DefaultDataAttributeContext( gmr.outputProcessorMetaData, gmr.getResourceBundleFactory().getLocale() );
    gmr.schemaCompiler =
        new ProcessingDataSchemaCompiler( schemaDefinition, dac, reportContext.getResourceManager(), null );
    gmr.dataSchema = null;
    gmr.setEnvironmentDataRow( new ReportEnvironmentDataRow( reportContext.getEnvironment() ) );
    gmr.setParameterDataRow( parameterDataRow );
    return gmr;
  }

  public void requireStructuralProcessing() {
    expressionDataRow.setIncludeStructuralProcessing( true );
  }

  public MasterDataRow deriveSubDataRow( final ProcessingContext reportContext, final DataFactory reportFactory,
      final ParameterDataRow parameterDataRow, final ResourceBundleFactory resourceBundleFactory ) {
    if ( reportContext == null ) {
      throw new NullPointerException();
    }
    if ( reportFactory == null ) {
      throw new NullPointerException();
    }
    if ( resourceBundleFactory == null ) {
      throw new NullPointerException();
    }
    if ( parameterDataRow == null ) {
      throw new NullPointerException();
    }
    final GlobalMasterRow gmr = new GlobalMasterRow();
    gmr.outputProcessorMetaData = outputProcessorMetaData;
    gmr.schemaDefinition = schemaDefinition;
    gmr.schemaCompiler = schemaCompiler;
    gmr.globalView = new FastGlobalView();
    gmr.expressionDataRow = new ExpressionDataRow( gmr.globalView, gmr, reportContext );
    gmr.expressionDataRow.setIncludeStructuralProcessing( expressionDataRow.isIncludeStructuralProcessing() );
    gmr.parentRow = this;
    gmr.dataSchema = null;
    gmr.resourceBundleFactory = resourceBundleFactory;
    gmr.paddingData = new DataProcessor();

    final CascadingDataFactory dataFactory = new CascadingDataFactory();
    dataFactory.add( reportFactory );
    dataFactory.add( this.dataFactory );
    gmr.dataFactory = dataFactory;
    gmr.setEnvironmentDataRow( environmentDataRow );
    gmr.setParameterDataRow( parameterDataRow );
    return gmr;
  }

  public MasterDataRow deriveWithQueryData( final TableModel tableData ) {
    if ( tableData == null ) {
      throw new NullPointerException();
    }

    final GlobalMasterRow derived = derive();
    derived.paddingData.setReportDataRow( new ReportDataRow( tableData ), derived.globalView );
    derived.dataSchema = null;
    return derived;
  }

  public MasterDataRow deriveWithReturnFromQuery() {
    final GlobalMasterRow derived = derive();
    derived.paddingData.clearReportDataRow( derived.globalView );
    derived.setParameterDataRow( null );
    derived.dataSchema = null;
    return derived;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  public DataSchema getDataSchema() {
    if ( dataSchema == null ) {
      try {
        dataSchema = schemaCompiler.compile( this, environmentDataRow.getEnvironment() );
      } catch ( ReportDataFactoryException re ) {
        throw new IllegalStateException( "Failed to compile data-schema - aborting report processing", re );
      }
    }
    return dataSchema;
  }

  public ReportDataRow getReportDataRow() {
    return paddingData.getReportDataRow();
  }

  public ExpressionDataRow getExpressionDataRow() {
    return expressionDataRow;
  }

  private void setEnvironmentDataRow( final ReportEnvironmentDataRow environmentDataRow ) {
    if ( this.environmentDataRow != null ) {
      DataRowEventHelper.removeAllColumns( this.environmentDataRow, this.globalView );
    }

    this.environmentDataRow = environmentDataRow;

    if ( environmentDataRow != null ) {
      DataRowEventHelper.addColumns( environmentDataRow, this.globalView );
    }

    this.dataSchema = null;
  }

  public ParameterDataRow getParameterDataRow() {
    return parameterDataRow;
  }

  private void setParameterDataRow( final ParameterDataRow parameterDataRow ) {
    if ( this.parameterDataRow != null ) {
      DataRowEventHelper.removeAllColumns( this.parameterDataRow, this.globalView );
    }

    this.parameterDataRow = parameterDataRow;

    if ( parameterDataRow != null ) {
      DataRowEventHelper.addColumns( this.parameterDataRow, globalView );
    }

    this.dataSchema = null;
  }

  public DataRow getGlobalView() {
    return globalView;
  }

  public boolean isAdvanceable() {
    return paddingData.isAdvanceable( globalView );
  }

  public GlobalMasterRow derive() {
    final GlobalMasterRow o = new GlobalMasterRow();
    o.storedGlobalView = storedGlobalView;
    o.storedPaddingData = storedPaddingData;
    o.environmentDataRow = environmentDataRow;
    o.outputProcessorMetaData = outputProcessorMetaData;
    o.dataFactory = dataFactory;
    o.dataSchema = dataSchema;
    o.schemaCompiler = schemaCompiler;
    o.schemaDefinition = schemaDefinition;
    o.globalView = globalView.derive();
    o.parameterDataRow = parameterDataRow;
    o.paddingData = paddingData.derive();
    o.resourceBundleFactory = resourceBundleFactory;
    o.expressionDataRow = expressionDataRow.derive( o.globalView, o, false );
    if ( parentRow != null ) {
      o.parentRow = parentRow.derive();
    }
    o.importedDataRow = importedDataRow;
    return o;
  }

  public void setImportedDataRow( final ImportedVariablesDataRow dataRow ) {
    if ( importedDataRow != null ) {
      DataRowEventHelper.removeAllColumns( this.importedDataRow, this.globalView );
    }

    this.importedDataRow = dataRow;
    if ( importedDataRow != null ) {
      DataRowEventHelper.addColumns( importedDataRow, this.globalView );
    }

    this.dataSchema = null;
  }

  public MasterDataRow getParentDataRow() {
    return parentRow;
  }

  /**
   * This advances the cursor by one row and updates the flags.
   *
   * @return
   */
  public MasterDataRow advance() {
    return advanceRecursively( false, null );
  }

  /**
   * This method is public as a implementation side effect. It is only intended to be used internally and no matter what
   * you intend: If you are not calling it from a MasterDataRow implementation, then you are on the wrong track.
   *
   * @param deepTraversingOnly
   * @param subReportRow
   * @return
   */
  public GlobalMasterRow advanceRecursively( final boolean deepTraversingOnly, final MasterDataRow subReportRow ) {
    final GlobalMasterRow dataRow = new GlobalMasterRow();
    dataRow.storedPaddingData = storedPaddingData;
    dataRow.storedGlobalView = storedGlobalView;
    dataRow.environmentDataRow = environmentDataRow;
    dataRow.outputProcessorMetaData = outputProcessorMetaData;
    if ( deepTraversingOnly == false ) {
      dataRow.globalView = globalView.advance();
    } else {
      dataRow.globalView = globalView.derive();
    }
    dataRow.dataSchema = dataSchema;
    dataRow.dataFactory = dataFactory;
    dataRow.schemaCompiler = schemaCompiler;
    dataRow.schemaDefinition = schemaDefinition;
    dataRow.resourceBundleFactory = resourceBundleFactory;

    if ( environmentDataRow != null ) {
      DataRowEventHelper.refreshDataRow( dataRow.environmentDataRow, dataRow.globalView );
    }

    if ( parameterDataRow != null ) {
      dataRow.parameterDataRow = parameterDataRow;
      DataRowEventHelper.refreshDataRow( dataRow.parameterDataRow, dataRow.globalView );
    }

    dataRow.paddingData = paddingData.advance( deepTraversingOnly, dataRow.globalView );

    if ( expressionDataRow != null ) {
      dataRow.expressionDataRow = expressionDataRow.derive( dataRow.globalView, dataRow, true );
    }
    if ( parentRow != null ) {
      // the parent row should get a grip on our data as well - just for the
      // deep traversing fun and so on ..
      dataRow.parentRow = parentRow.advanceRecursively( true, dataRow );
    }

    if ( importedDataRow != null ) {
      if ( subReportRow != null ) {
        dataRow.importedDataRow = importedDataRow.refresh( subReportRow.getGlobalView(), subReportRow.getDataSchema() );
        DataRowEventHelper.refreshDataRow( dataRow.importedDataRow, dataRow.globalView );
      } else {
        dataRow.importedDataRow = importedDataRow;
        DataRowEventHelper.refreshDataRow( dataRow.importedDataRow, dataRow.globalView );
      }
    }

    dataRow.refresh();
    dataRow.globalView.validateChangedFlags();

    return dataRow;
  }

  public void fireReportEvent( final ReportEvent event ) {
    if ( expressionDataRow != null ) {
      expressionDataRow.fireReportEvent( event );
    }
    if ( ( event.getType() & ReportEvent.NO_PARENT_PASSING_EVENT ) == ReportEvent.NO_PARENT_PASSING_EVENT ) {
      return;
    }
    if ( parentRow != null ) {
      final ReportState parentState = event.getState().getParentSubReportState();
      final ReportEvent deepEvent;
      if ( parentState == null ) {
        deepEvent = event;
      } else {
        deepEvent =
            new ReportEvent( parentState, event.getState(), event.getType() | ReportEvent.DEEP_TRAVERSING_EVENT );
      }
      parentRow.fireReportEvent( deepEvent );
      if ( parentRow.importedDataRow != null ) {
        parentRow.importedDataRow = parentRow.importedDataRow.refresh( this.getGlobalView(), this.getDataSchema() );
        DataRowEventHelper.refreshDataRow( parentRow.importedDataRow, parentRow.globalView );
      }
    }
  }

  public MasterDataRow startCrosstabMode( final CrosstabSpecification crosstabSpecification ) {
    final GlobalMasterRow retval = derive();
    retval.paddingData = paddingData.startCrosstabMode( crosstabSpecification, retval.globalView );
    return retval;
  }

  public MasterDataRow endCrosstabMode() {
    final GlobalMasterRow retval = derive();
    retval.paddingData = paddingData.endCrosstabMode();
    return retval;
  }

  public MasterDataRow resetRowCursor() {
    final GlobalMasterRow retval = derive();
    retval.paddingData = paddingData.resetRowCursor();
    return retval;
  }

  public MasterDataRow clearExportedParameters() {
    if ( importedDataRow == null ) {
      return this;
    }

    final GlobalMasterRow derived = derive();
    derived.setImportedDataRow( null );
    derived.resetDataSchema();
    return derived;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  public void resetDataSchema() {
    this.dataSchema = null;
  }

  public GlobalMasterRow rebuild() {
    if ( globalView.getColumnNames().length == 0 ) {
      return this;
    }

    if ( parentRow != null ) {
      throw new IllegalStateException(
          "This should be at the beginning of the master-report processing. No parent allowed." );
    }
    if ( paddingData.getReportDataRow() != null ) {
      throw new IllegalStateException(
          "This should be at the beginning of the master-report processing. No report-data allowed." );
    }

    final GlobalMasterRow gmr = derive();
    gmr.dataSchema = null;
    gmr.globalView = new FastGlobalView();
    gmr.setEnvironmentDataRow( environmentDataRow );
    gmr.setParameterDataRow( parameterDataRow );
    return gmr;
  }

  public MasterDataRow updateDataSchema( final DataSchemaDefinition dataSchemaDefinition ) {
    if ( dataSchemaDefinition == null ) {
      throw new NullPointerException();
    }

    final DefaultDataAttributeContext dac =
        new DefaultDataAttributeContext( outputProcessorMetaData, resourceBundleFactory.getLocale() );
    final GlobalMasterRow gmr = derive();
    gmr.schemaDefinition = dataSchemaDefinition;
    gmr.schemaCompiler =
        new ProcessingDataSchemaCompiler( dataSchemaDefinition, dac, schemaCompiler.getResourceManager(),
            schemaCompiler.getGlobalDefaults() );
    gmr.dataSchema = null;
    return gmr;
  }

  public DataSchemaDefinition getDataSchemaDefinition() {
    return schemaDefinition;
  }

  public void refresh() {
    if ( environmentDataRow != null ) {
      DataRowEventHelper.refreshDataRow( environmentDataRow, this.globalView );
    }
    if ( parameterDataRow != null ) {
      DataRowEventHelper.refreshDataRow( parameterDataRow, this.globalView );
    } else {
      throw new NullPointerException();
    }

    if ( paddingData != null && !paddingData.isCrosstabActive() ) {
      paddingData.refresh( this.globalView );
    }

    if ( expressionDataRow != null ) {
      expressionDataRow.refresh();
    }
    if ( importedDataRow != null ) {
      DataRowEventHelper.refreshDataRow( importedDataRow, this.globalView );
    }
  }

  public ImportedVariablesDataRow getImportedDataRow() {
    return importedDataRow;
  }

  public TableModel getReportData() {
    return paddingData.getRawData();
  }

  public int getCursor() {
    return paddingData.getCursor();
  }

  public int getRawDataCursor() {
    return paddingData.getRawDataCursor();
  }

  public int getRowCount() {
    // todo: Shall we return the padded count (includes all additional crosstab rows) or shall we just
    // return the raw row count?
    final TableModel rawData = paddingData.getRawData();
    if ( rawData != null ) {
      return rawData.getRowCount();
    }
    return 0;
  }

  public CrosstabSpecification getCrosstabSpecification() {
    return paddingData.getCrosstabSpecification();
  }

  public boolean isCrosstabActive() {
    return paddingData.isCrosstabActive();
  }

  public MasterDataRow recordCrosstabRowState() {
    final GlobalMasterRow copy = derive();
    copy.storedPaddingData = paddingData.clone();
    copy.storedGlobalView = globalView.derive();
    return copy;
  }

  public MasterDataRow clearRecordedCrosstabRowState() {
    final GlobalMasterRow copy = derive();
    copy.storedPaddingData = null;
    copy.storedGlobalView = null;
    return copy;
  }

  public MasterDataRow replayStoredCrosstabRowState() {
    final GlobalMasterRow copy = derive();
    copy.globalView = storedGlobalView.derive();
    copy.paddingData = storedPaddingData.clone();
    copy.expressionDataRow = expressionDataRow.derive( copy.globalView, copy, false );
    copy.refresh();

    return copy;
  }

  public void validateReplayFinished() throws ReportProcessingException {
  }
}
