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
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.DefaultGroupingState;
import org.pentaho.reporting.engine.classic.core.states.GroupingState;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.swing.table.TableModel;

public final class ExpressionDataRow extends ExpressionEventHelper {
  private static final Log logger = LogFactory.getLog( ExpressionDataRow.class );

  private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];

  private static class DataRowRuntime implements ExpressionRuntime {
    private ExpressionDataRow expressionDataRow;
    private GroupingState state;
    private boolean structuralComplex;
    private boolean crosstabActive;

    protected DataRowRuntime( final ExpressionDataRow dataRow ) {
      this.expressionDataRow = dataRow;
      this.state = DefaultGroupingState.EMPTY;
    }

    public DataSchema getDataSchema() {
      return expressionDataRow.getMasterRow().getDataSchema();
    }

    public DataRow getDataRow() {
      return expressionDataRow.getMasterRow().getGlobalView();
    }

    public Configuration getConfiguration() {
      return getProcessingContext().getConfiguration();
    }

    public ResourceBundleFactory getResourceBundleFactory() {
      return expressionDataRow.getMasterRow().getResourceBundleFactory();
    }

    public DataFactory getDataFactory() {
      return expressionDataRow.getMasterRow().getDataFactory();
    }

    /**
     * Access to the tablemodel was granted using report properties, now direct.
     */
    public TableModel getData() {
      return expressionDataRow.getMasterRow().getReportData();
    }

    /**
     * Where are we in the current processing.
     */
    public int getCurrentRow() {
      return expressionDataRow.getMasterRow().getCursor();
    }

    public int getCurrentDataItem() {
      return expressionDataRow.getMasterRow().getRawDataCursor();
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
      return expressionDataRow.getProcessingContext();
    }

    public int getCurrentGroup() {
      return state.getCurrentGroup();
    }

    public int getGroupStartRow( final String groupName ) {
      return state.getGroupStartRow( groupName );
    }

    public int getGroupStartRow( final int groupIndex ) {
      return state.getGroupStartRow( groupIndex );
    }

    public boolean isStructuralComplexReport() {
      return structuralComplex;
    }

    public boolean isCrosstabActive() {
      return crosstabActive;
    }

    public GroupingState getState() {
      return state;
    }

    public void setState( final GroupingState state ) {
      if ( state == null ) {
        throw new NullPointerException();
      }
      this.state = state;
    }

    public void setCrosstabInfo( final boolean structuralComplex, final boolean crosstabActive ) {
      this.structuralComplex = structuralComplex;
      this.crosstabActive = crosstabActive;
    }
  }

  private MasterDataRowChangeHandler masterRowChangeHandler;
  private MasterDataRow masterRow;
  private ProcessingContext processingContext;
  private int length;
  private Expression[] expressions;
  private LevelStorageBackend[] levelData;
  private DataRowRuntime runtime;
  private boolean includeStructuralProcessing;

  public ExpressionDataRow( final MasterDataRowChangeHandler masterRowChangeHandler, final MasterDataRow masterRow,
      final ProcessingContext processingContext ) {
    if ( masterRow == null ) {
      throw new NullPointerException();
    }
    if ( processingContext == null ) {
      throw new NullPointerException();
    }

    this.processingContext = processingContext;
    this.masterRow = masterRow;
    this.masterRowChangeHandler = masterRowChangeHandler;
    this.expressions = ExpressionDataRow.EMPTY_EXPRESSIONS;
    this.runtime = new DataRowRuntime( this );
    this.revalidate();
  }

  public boolean isIncludeStructuralProcessing() {
    return includeStructuralProcessing;
  }

  public void setIncludeStructuralProcessing( final boolean includeStructuralProcessing ) {
    this.includeStructuralProcessing = includeStructuralProcessing;
    revalidate();
  }

  private void revalidate() {
    this.levelData = LevelStorageBackend.revalidate( this.expressions, length, includeStructuralProcessing );
  }

  private ExpressionDataRow( final MasterDataRowChangeHandler masterRowChangeHandler, final MasterDataRow masterRow,
      final ExpressionDataRow previousRow, final boolean updateGlobalView ) throws CloneNotSupportedException {
    final MasterDataRowChangeEvent chEvent = masterRowChangeHandler.getReusableEvent();
    chEvent.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, "", "" );
    this.processingContext = previousRow.processingContext;
    this.masterRow = masterRow;
    this.masterRowChangeHandler = masterRowChangeHandler;
    this.expressions = new Expression[previousRow.expressions.length];
    this.length = previousRow.length;
    this.levelData = previousRow.levelData;
    this.runtime = new DataRowRuntime( this );
    this.runtime.setState( previousRow.runtime.getState() );
    this.includeStructuralProcessing = previousRow.includeStructuralProcessing;

    for ( int i = 0; i < length; i++ ) {
      final Expression expression = previousRow.expressions[i];
      if ( expression == null ) {
        ExpressionDataRow.logger.debug( "Error: Expression is null..." );
        throw new IllegalStateException();
      }

      if ( expression instanceof Function ) {
        expressions[i] = (Expression) expression.clone();
      } else {
        expressions[i] = expression;
      }

      if ( updateGlobalView == false ) {
        continue;
      }

      final String name = expression.getName();
      if ( name != null ) {
        chEvent.setColumnName( name );
      }
      Object value;

      final ExpressionRuntime oldRuntime = expression.getRuntime();
      try {
        expression.setRuntime( runtime );
        if ( runtime.getProcessingContext().getProcessingLevel() <= expression.getDependencyLevel() ) {
          value = expression.getValue();
        } else {
          value = null;
        }
      } catch ( Exception e ) {
        if ( ExpressionDataRow.logger.isDebugEnabled() ) {
          ExpressionDataRow.logger.warn( "Failed to evaluate expression '" + name + '\'', e );
        } else {
          ExpressionDataRow.logger.warn( "Failed to evaluate expression '" + name + '\'' );
        }
        value = null;
      } finally {
        expression.setRuntime( oldRuntime );
      }
      if ( name != null ) {
        chEvent.setColumnValue( value );
        masterRowChangeHandler.dataRowChanged( chEvent );
      }
    }
  }

  /**
   * This adds the expression to the data-row and queries the expression for the first time.
   *
   * @param expressionSlot
   *          the expression that should be added.
   * @param preserveState
   *          a flag indicating whether the expression is statefull and should preserve its internal state.
   * @throws ReportProcessingException
   *           if the processing failed due to invalid function implementations.
   */
  private void pushExpression( final Expression expressionSlot, final boolean preserveState )
    throws ReportProcessingException {
    if ( expressionSlot == null ) {
      throw new NullPointerException();
    }

    ensureCapacity( length + 1 );

    if ( preserveState == false ) {
      this.expressions[length] = expressionSlot.getInstance();
    } else {
      try {
        this.expressions[length] = (Expression) expressionSlot.clone();
      } catch ( final CloneNotSupportedException e ) {
        throw new ReportProcessingException( "Failed to clone the expression.", e );
      }
    }

    final String name = expressionSlot.getName();
    length += 1;

    // A manual advance to initialize the function.
    if ( name != null ) {
      final MasterDataRowChangeEvent event = masterRowChangeHandler.getReusableEvent();
      event.reuse( MasterDataRowChangeEvent.COLUMN_ADDED, name, null );
      masterRowChangeHandler.dataRowChanged( event );
    }
  }

  public void pushExpressions( final Expression[] expressionSlots, final boolean preserveState )
    throws ReportProcessingException {
    if ( expressionSlots == null ) {
      throw new NullPointerException();
    }

    ensureCapacity( length + expressionSlots.length );
    for ( int i = 0; i < expressionSlots.length; i++ ) {
      final Expression expression = expressionSlots[i];
      if ( expression == null ) {
        continue;
      }
      pushExpression( expression, preserveState );
    }

    revalidate();
  }

  public void popExpressions( final int counter ) {
    for ( int i = 0; i < counter; i++ ) {
      popExpression();
    }

    revalidate();
  }

  private void popExpression() {
    if ( length == 0 ) {
      return;
    }
    final Expression removedExpression = this.expressions[length - 1];
    final String originalName = removedExpression.getName();
    removedExpression.setRuntime( null );

    this.expressions[length - 1] = null;
    this.length -= 1;
    if ( originalName != null ) {
      if ( removedExpression.isPreserve() == false ) {
        final MasterDataRowChangeEvent event = masterRowChangeHandler.getReusableEvent();
        event.reuse( MasterDataRowChangeEvent.COLUMN_REMOVED, originalName, null );
        masterRowChangeHandler.dataRowChanged( event );
      }
    }
  }

  private void ensureCapacity( final int requestedSize ) {
    final int capacity = this.expressions.length;
    if ( capacity > requestedSize ) {
      return;
    }
    final int newSize = Math.max( capacity * 2, requestedSize + 10 );

    final Expression[] newExpressions = new Expression[newSize];
    System.arraycopy( expressions, 0, newExpressions, 0, length );

    this.expressions = newExpressions;
  }

  /**
   * Returns the number of columns, expressions and functions and marked ReportProperties in the report.
   *
   * @return the item count.
   */
  public int getColumnCount() {
    return length;
  }

  public void fireReportEvent( final ReportEvent event ) {
    final ReportState reportState = event.getState();
    runtime.setState( reportState.createGroupingState() );
    runtime.setCrosstabInfo( reportState.isStructuralPreprocessingNeeded(), reportState.isCrosstabActive() );
    super.fireReportEvent( event );
    super.reactivateExpressions( event.isDeepTraversing() );
  }

  protected void updateMasterDataRow( final String name, final Object value ) {
    final MasterDataRowChangeEvent event = masterRowChangeHandler.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, name, value );
    masterRowChangeHandler.dataRowChanged( event );
  }

  protected ExpressionRuntime getRuntime() {
    return runtime;
  }

  protected int getProcessingLevel() {
    final int activeLevel;
    final int rawLevel = runtime.getProcessingContext().getProcessingLevel();
    if ( rawLevel == LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING ) {
      // we are in the data-pre-processing stage. Include all common expressions, in case they
      // compute a group-break.
      if ( levelData.length > 0 ) {
        activeLevel = levelData[0].getLevelNumber();
      } else {
        activeLevel = rawLevel;
      }
    } else {
      activeLevel = rawLevel;
    }
    return activeLevel;
  }

  public ExpressionDataRow derive( final MasterDataRowChangeHandler masterRowChangeHandler,
      final MasterDataRow masterRow, final boolean update ) {
    try {
      return new ExpressionDataRow( masterRowChangeHandler, masterRow, this, update );
    } catch ( final CloneNotSupportedException e ) {
      logger.error( "Error on derive(..): ", e );
      throw new IllegalStateException( "Cannot clone? Cannot survive!" );
    }
  }

  public boolean isValid() {
    return levelData != null;
  }

  public Expression[] getExpressions() {
    final Expression[] retval = new Expression[length];
    System.arraycopy( expressions, 0, retval, 0, length );
    return retval;
  }

  /**
   * Returns the current master-row instance to inner-classes.
   *
   * @return a reference to the master-row (to be used in inner classes).
   * @noinspection ProtectedMemberInFinalClass
   */
  protected MasterDataRow getMasterRow() {
    return masterRow;
  }

  /**
   * Returns the current processing context to inner-classes.
   *
   * @return a reference to the processing context (to be used in inner classes).
   * @noinspection ProtectedMemberInFinalClass
   */
  protected ProcessingContext getProcessingContext() {
    return processingContext;
  }

  public void refresh() {
    reactivateExpressions( false );
  }

  protected int getRunLevelCount() {
    return levelData.length;
  }

  protected LevelStorage getRunLevel( final int index ) {
    final LevelStorageBackend backend = levelData[index];
    return LevelStorageBackend.getLevelStorage( backend, expressions );
  }
}
