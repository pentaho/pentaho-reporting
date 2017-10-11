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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.datarow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryDesignTimeSupport;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.engine.classic.core.states.LengthLimitingTableModel;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.FormattedMessage;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

public class DefaultFlowController {
  private static class ReportDataContext {
    private boolean advanceRequested;

    protected ReportDataContext( final boolean advanceRequested ) {
      this.advanceRequested = advanceRequested;
    }

    public boolean isAdvanceRequested() {
      return advanceRequested;
    }
  }

  private MasterDataRow dataRow;
  private boolean advanceRequested;
  private FastStack<Integer> expressionsStack;
  private FastStack<ReportDataContext> dataContextStack;
  private String exportDescriptor;
  private ProcessingContext reportContext;
  private ReportParameterValues parameters;
  private boolean storedAdvanceRequested;
  private PerformanceMonitorContext performanceMonitorContext;
  private boolean isQueryLimitReached;

  public DefaultFlowController( final ProcessingContext reportContext, final DataSchemaDefinition schemaDefinition,
      final ReportParameterValues parameters, final PerformanceMonitorContext performanceMonitorContext )
    throws ReportDataFactoryException {
    ArgumentNullException.validate( "performanceMonitorContext", performanceMonitorContext );

    if ( reportContext == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }
    if ( schemaDefinition == null ) {
      throw new NullPointerException();
    }

    this.performanceMonitorContext = performanceMonitorContext;
    this.reportContext = reportContext;
    this.exportDescriptor = reportContext.getExportDescriptor();
    this.expressionsStack = new FastStack<Integer>( 10 );
    this.dataContextStack = new FastStack<ReportDataContext>( 10 );
    this.advanceRequested = false;
    this.parameters = parameters;

    this.dataRow = createDataRow( reportContext, schemaDefinition, parameters );

  }

  protected MasterDataRow createDataRow( ProcessingContext reportContext, DataSchemaDefinition schemaDefinition, ReportParameterValues parameters ) {
    return GlobalMasterRow.createReportRow( reportContext, schemaDefinition, new ParameterDataRow( parameters ) );
  }

  private DefaultFlowController( final DefaultFlowController fc, final MasterDataRow dataRow ) {
    ArgumentNullException.validate( "fc", fc );
    ArgumentNullException.validate( "dataRow", dataRow );

    this.performanceMonitorContext = fc.performanceMonitorContext;
    this.reportContext = fc.reportContext;
    this.exportDescriptor = fc.exportDescriptor;
    this.dataContextStack = fc.dataContextStack.clone();
    this.expressionsStack = fc.expressionsStack.clone();
    this.advanceRequested = fc.advanceRequested;
    this.storedAdvanceRequested = fc.storedAdvanceRequested;
    this.dataRow = dataRow;
    this.parameters = fc.parameters;
  }

  public void requireStructuralProcessing() {
    this.dataRow.requireStructuralProcessing();
  }

  public DefaultFlowController derive() {
    return new DefaultFlowController( this, dataRow.derive() );
  }

  public DefaultFlowController performAdvance() {
    if ( dataRow.isAdvanceable() && advanceRequested == false ) {
      final DefaultFlowController fc = new DefaultFlowController( this, dataRow );
      fc.advanceRequested = true;
      return fc;
    }
    return this;
  }

  public DefaultFlowController performCommit() {
    if ( isAdvanceRequested() ) {
      final DefaultFlowController fc = new DefaultFlowController( this, dataRow );
      fc.dataRow = dataRow.advance();
      fc.advanceRequested = false;
      return fc;
    }
    return this;
  }

  public MasterDataRow getMasterRow() {
    return dataRow;
  }

  public boolean isAdvanceRequested() {
    return advanceRequested;
  }

  /**
   * This should be called only once per report processing. A JFreeReport object defines the global master report - all
   * other reports are subreport instances.
   * <p/>
   * The global master report receives its parameter set from the Job-Definition, while subreports will read their
   * parameters from the current datarow state.
   *
   * @param query
   * @param queryLimit
   * @param queryTimeout
   * @return
   * @throws ReportDataFactoryException
   * @deprecated
   */
  @Deprecated
  public DefaultFlowController performQuery( final DataFactory dataFactory, final String query, final int queryLimit,
      final int queryTimeout, final ResourceBundleFactory resourceBundleFactory ) throws ReportDataFactoryException {
    List<SortConstraint> objects = Collections.emptyList();
    return performQuery( dataFactory, query, queryLimit, queryTimeout, resourceBundleFactory, objects );
  }

  public DefaultFlowController performQuery( final DataFactory dataFactory, final String query, final int queryLimit,
      final int queryTimeout, final ResourceBundleFactory resourceBundleFactory, final List<SortConstraint> sortOrder )
    throws ReportDataFactoryException {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( resourceBundleFactory == null ) {
      throw new NullPointerException();
    }

    final MasterDataRow masterRowWithoutData =
        dataRow
            .deriveSubDataRow( reportContext, dataFactory, new ParameterDataRow( parameters ), resourceBundleFactory );
    final TableModel tableData =
        performQueryData( masterRowWithoutData.getDataFactory(), query, queryLimit, queryTimeout, masterRowWithoutData
            .getGlobalView(), false, sortOrder );
    final MasterDataRow masterRow = masterRowWithoutData.deriveWithQueryData( tableData );

    final DefaultFlowController fc = new DefaultFlowController( this, masterRow );
    fc.dataContextStack.push( new ReportDataContext( advanceRequested ) );
    fc.dataRow = masterRow;
    fc.dataRow.resetDataSchema();
    return fc;
  }

  public DefaultFlowController performDesignTimeQuery( final DataFactory dataFactory, final String query,
      final int queryLimit, final int queryTimeout, final ResourceBundleFactory resourceBundleFactory )
    throws ReportDataFactoryException {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( resourceBundleFactory == null ) {
      throw new NullPointerException();
    }

    final MasterDataRow masterRowWithoutData =
        dataRow
            .deriveSubDataRow( reportContext, dataFactory, new ParameterDataRow( parameters ), resourceBundleFactory );
    List<SortConstraint> objects = Collections.emptyList();
    final TableModel tableData =
        performQueryData( masterRowWithoutData.getDataFactory(), query, queryLimit, queryTimeout, masterRowWithoutData
            .getGlobalView(), true, objects );
    final MasterDataRow masterRow = masterRowWithoutData.deriveWithQueryData( tableData );

    final DefaultFlowController fc = new DefaultFlowController( this, masterRow );
    fc.dataContextStack.push( new ReportDataContext( advanceRequested ) );
    fc.dataRow = masterRow;
    fc.dataRow.resetDataSchema();
    return fc;
  }

  protected TableModel performQueryData( final DataFactory dataFactory, final String query, final int queryLimit,
      final int queryTimeout, final DataRow parameters, final boolean designTime,
      final List<SortConstraint> sortConstraints ) throws ReportDataFactoryException {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    if ( query == null ) {
      return new EmptyTableModel();
    }

    PerformanceLoggingStopWatch sw =
        performanceMonitorContext.createStopWatch( PerformanceTags.REPORT_QUERY, new FormattedMessage( "query={%s}",
            query ) );
    try {
      sw.start();
      // increasedQueryLimit for reports where queryLimit set. To handle the situation when reportData.getRowCount() == queryLimit
      int increasedQueryLimit = queryLimit > 0 ? queryLimit + 1 : queryLimit;
      DataRow params = new QueryDataRowWrapper( parameters, queryTimeout, increasedQueryLimit, sortConstraints );
      TableModel reportData;
      if ( designTime && dataFactory instanceof DataFactoryDesignTimeSupport ) {
        DataFactoryDesignTimeSupport designTimeSupport = (DataFactoryDesignTimeSupport) dataFactory;
        reportData = designTimeSupport.queryDesignTimeStructure( query, params );
      } else {
        reportData = dataFactory.queryData( query, params );
      }

      if ( queryLimit > 0 && reportData.getRowCount() >= queryLimit + 1 ) {
        setQueryLimitReached( true );
        return new LengthLimitingTableModel( reportData, queryLimit );
      }
      return reportData;
    } finally {
      sw.close();
    }
  }

  public DefaultFlowController performInitSubreport( final DataFactory dataFactory,
      final ParameterMapping[] inputParameters, final ResourceBundleFactory resourceBundleFactory ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( inputParameters == null ) {
      throw new NullPointerException();
    }
    if ( resourceBundleFactory == null ) {
      throw new NullPointerException();
    }

    // create a view for the parameters of the report ...
    final MasterDataRow subReportDataRow;
    if ( isGlobalImportOrExport( inputParameters ) ) {
      final ParameterDataRow parameterRow = new ParameterDataRow( dataRow.getGlobalView() );
      subReportDataRow = dataRow.deriveSubDataRow( reportContext, dataFactory, parameterRow, resourceBundleFactory );
    } else {
      final ParameterDataRow parameterRow = new ParameterDataRow( inputParameters, dataRow.getGlobalView() );
      subReportDataRow = dataRow.deriveSubDataRow( reportContext, dataFactory, parameterRow, resourceBundleFactory );
    }

    final DefaultFlowController fc = new DefaultFlowController( this, subReportDataRow );
    fc.dataContextStack.push( new ReportDataContext( advanceRequested ) );
    fc.dataRow = subReportDataRow;
    fc.dataRow.resetDataSchema();
    return fc;
  }

  @Deprecated
  public DefaultFlowController performSubReportQuery( final String query, final int queryLimit, final int queryTimeout,
      final ParameterMapping[] outputParameters ) throws ReportDataFactoryException {
    List<SortConstraint> con = Collections.emptyList();
    return performSubReportQuery( query, queryLimit, queryTimeout, outputParameters, con );
  }

  public DefaultFlowController performSubReportQuery( final String query, final int queryLimit, final int queryTimeout,
      final ParameterMapping[] outputParameters, final List<SortConstraint> sortConstraints )
    throws ReportDataFactoryException {
    if ( outputParameters == null ) {
      throw new NullPointerException();
    }

    final MasterDataRow subReportDataRow = this.dataRow;
    // perform the query ...
    // add the resultset ...
    final TableModel tableData =
        performQueryData( subReportDataRow.getDataFactory(), query, queryLimit, queryTimeout, subReportDataRow
            .getGlobalView(), false, sortConstraints );
    final MasterDataRow masterRow = subReportDataRow.deriveWithQueryData( tableData );

    if ( isGlobalImportOrExport( outputParameters ) ) {
      if ( "true".equals( reportContext.getConfiguration().getConfigProperty(
          "org.pentaho.reporting.engine.classic.core.EnableGlobalSubReportImports" ) ) ) {
        masterRow.getParentDataRow().setImportedDataRow( new ImportedVariablesDataRow( masterRow ) );
      } else {
        masterRow.getParentDataRow().setImportedDataRow(
            new ImportedVariablesDataRow( masterRow, filterGlobalImport( outputParameters ) ) );
      }
    } else {
      // check and rebuild the parameter mapping from the inner to the outer
      // context. Only deep-traversal expressions will be able to see these
      // values (unless they have been defined as local variables).
      masterRow.getParentDataRow().setImportedDataRow( new ImportedVariablesDataRow( masterRow, outputParameters ) );
    }

    final DefaultFlowController fc = new DefaultFlowController( this, masterRow );
    fc.dataContextStack.push( new ReportDataContext( advanceRequested ) );
    fc.dataRow = masterRow;
    fc.dataRow.resetDataSchema();
    return fc;
  }

  private ParameterMapping[] filterGlobalImport( final ParameterMapping[] parameterMapping ) {
    final ArrayList<ParameterMapping> filteredList = new ArrayList<ParameterMapping>( parameterMapping.length );
    for ( int i = 0; i < parameterMapping.length; i++ ) {
      final ParameterMapping mapping = parameterMapping[i];
      if ( "*".equals( mapping.getName() ) && "*".equals( mapping.getAlias() ) ) {
        continue;
      }
      filteredList.add( mapping );
    }
    return filteredList.toArray( new ParameterMapping[filteredList.size()] );
  }

  /**
   * Checks whether a global import is defined. A global import effectly overrides all other imports.
   *
   * @return true, if there is a global import defined, false otherwise.
   */
  private boolean isGlobalImportOrExport( final ParameterMapping[] inputParameters ) {
    for ( int i = 0; i < inputParameters.length; i++ ) {
      final ParameterMapping inputParameter = inputParameters[i];
      if ( "*".equals( inputParameter.getName() ) && "*".equals( inputParameter.getAlias() ) ) {
        return true;
      }
    }
    return false;
  }

  public DefaultFlowController activateExpressions( final Expression[] expressions, final boolean preserveState )
    throws ReportProcessingException {
    if ( expressions == null ) {
      throw new NullPointerException();
    }

    final MasterDataRow dataRow = this.dataRow.derive();
    final ExpressionDataRow edr = dataRow.getExpressionDataRow();
    edr.pushExpressions( expressions, preserveState );
    dataRow.resetDataSchema();

    final DefaultFlowController fc = new DefaultFlowController( this, dataRow );
    final Integer exCount = IntegerCache.getInteger( expressions.length );
    fc.expressionsStack.push( exCount );
    return fc;
  }

  public DefaultFlowController deactivateExpressions() {
    final Integer counter = this.expressionsStack.peek();
    final int counterRaw = counter.intValue();
    if ( counterRaw == 0 ) {
      final DefaultFlowController fc = new DefaultFlowController( this, dataRow );
      fc.expressionsStack.pop();
      return fc;
    }

    final MasterDataRow dataRow = this.dataRow.derive();
    final ExpressionDataRow edr = dataRow.getExpressionDataRow();

    final DefaultFlowController fc = new DefaultFlowController( this, dataRow );
    fc.expressionsStack.pop();
    edr.popExpressions( counterRaw );
    dataRow.resetDataSchema();
    return fc;
  }

  public DefaultFlowController performReturnFromQuery() {
    final ReportDataRow reportDataRow = dataRow.getReportDataRow();
    if ( reportDataRow == null ) {
      return this;
    }
    // We dont close the report data, as some previously saved states may
    // still reference it. (The caching report data factory takes care of
    // that later.)

    final MasterDataRow innerDr = dataRow.deriveWithReturnFromQuery();
    final DefaultFlowController fc = new DefaultFlowController( this, innerDr );
    final ReportDataContext context = fc.dataContextStack.pop();
    fc.dataRow = dataRow.getParentDataRow();
    fc.dataRow = fc.dataRow.derive();
    fc.advanceRequested = context.isAdvanceRequested();
    innerDr.resetDataSchema();
    return fc;
  }

  public DefaultFlowController performReturnFromSubreport() {
    // first, we undo the call "performSubreportQuery" and unwrap the report-data (and its corresponding stack entry)
    final MasterDataRow innerDrFromQuery = dataRow.deriveWithReturnFromQuery();
    final DefaultFlowController fc = new DefaultFlowController( this, innerDrFromQuery );
    fc.advanceRequested = fc.dataContextStack.pop().isAdvanceRequested();

    // second, we undo the call "performInitSubreport" and unwrap the parameter-data (and its corresponding stack entry)
    final DefaultFlowController fc2 = new DefaultFlowController( fc, dataRow.getParentDataRow().derive() );
    fc2.advanceRequested = fc2.dataContextStack.pop().isAdvanceRequested();
    return fc2;
  }

  public DefaultFlowController performClearExportedParameters() {
    final MasterDataRow masterDataRow = dataRow.clearExportedParameters();
    if ( masterDataRow == dataRow ) {
      return this;
    }

    return new DefaultFlowController( this, dataRow.clearExportedParameters() );
  }

  public String getExportDescriptor() {
    return exportDescriptor;
  }

  public ProcessingContext getReportContext() {
    return reportContext;
  }

  public DefaultFlowController fireReportEvent( final ReportEvent event ) {
    dataRow.fireReportEvent( event );
    return this;
  }

  public DataSchema getDataSchema() {
    return dataRow.getDataSchema();
  }

  public DataFactory getDataFactory() {
    return dataRow.getDataFactory();
  }

  public DefaultFlowController startCrosstabMode( final CrosstabSpecification crosstabSpecification ) {
    final MasterDataRow dataRow = this.dataRow.startCrosstabMode( crosstabSpecification );
    // begin crosstab mode ...
    return new DefaultFlowController( this, dataRow );
  }

  public DefaultFlowController endCrosstabMode() {
    final MasterDataRow dataRow = this.dataRow.endCrosstabMode();

    // end crosstab mode ...
    return new DefaultFlowController( this, dataRow );
  }

  public DefaultFlowController resetRowCursor() {
    final MasterDataRow dataRow = this.dataRow.resetRowCursor();
    return new DefaultFlowController( this, dataRow );
  }

  public DefaultFlowController restart() {
    final GlobalMasterRow innerDr = dataRow.rebuild();
    return new DefaultFlowController( this, innerDr );
  }

  public DefaultFlowController updateDataSchema( final DataSchemaDefinition dataSchemaDefinition ) {
    return new DefaultFlowController( this, dataRow.updateDataSchema( dataSchemaDefinition ) );
  }

  public DefaultFlowController refreshDataRow() {
    dataRow.refresh();
    return this;
  }

  public boolean isCrosstabActive() {
    return dataRow.isCrosstabActive();
  }

  public DefaultFlowController recordCrosstabRowState() {
    final MasterDataRow dataRow = this.dataRow.recordCrosstabRowState();
    final DefaultFlowController flowController = new DefaultFlowController( this, dataRow );
    flowController.storedAdvanceRequested = this.advanceRequested;
    return flowController;
  }

  public DefaultFlowController clearRecordedCrosstabRowState() {
    final MasterDataRow dataRow = this.dataRow.clearRecordedCrosstabRowState();
    return new DefaultFlowController( this, dataRow );
  }

  public DefaultFlowController replayStoredCrosstabRowState() {
    final MasterDataRow dataRow = this.dataRow.replayStoredCrosstabRowState();
    final DefaultFlowController flowController = new DefaultFlowController( this, dataRow );
    flowController.advanceRequested = storedAdvanceRequested;
    return flowController;
  }

  public boolean isQueryLimitReached() {
    return isQueryLimitReached;
  }

  public void setQueryLimitReached( boolean queryLimitReached ) {
    isQueryLimitReached = queryLimitReached;
  }
}
