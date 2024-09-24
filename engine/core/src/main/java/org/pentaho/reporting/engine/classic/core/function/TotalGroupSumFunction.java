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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;

/**
 * A report function that calculates the sum of one field (column) from the Data-Row. This function produces a global
 * total. The total sum of the group is known when the group processing starts and the report is not performing a
 * prepare-run. The sum is calculated in the prepare run and recalled in the printing run.
 * <p/>
 * The function can be used in two ways: <ul> <li>to calculate a sum for the entire report;</li> <li>to calculate a sum
 * within a particular group;</li> </ul> This function expects its input values to be either java.lang.Number instances
 * or Strings that can be parsed to java.lang.Number instances using a java.text.DecimalFormat.
 * <p/>
 * The function understands two parameters, the <code>field</code> parameter is required and denotes the name of an
 * ItemBand-field which gets summed up.
 * <p/>
 * The parameter <code>group</code> denotes the name of a group. When this group is started, the counter gets reset to
 * null. This parameter is optional.
 *
 * @author Thomas Morgner
 */
public class TotalGroupSumFunction extends AbstractFunction implements FieldAggregationFunction {
  /**
   * A useful constant representing zero.
   */
  protected static final BigDecimal ZERO = new BigDecimal( 0.0 );

  private static final int ZERO_I = 0;

  private StateSequence<BigDecimal> stateSequence;

  private transient int lastGroupSequenceNumber;

  /**
   * The field that should be evaluated.
   */
  private String field;
  /**
   * The name of the group on which to reset.
   */
  private String group;

  /**
   * The currently computed result.
   */
  private transient Sequence<BigDecimal> result;
  /**
   * The global state key is used to store the result for the whole report.
   */
  private transient ReportStateKey globalStateKey;

  /**
   * The current group key is used to store the result for the current group.
   */
  protected transient ReportStateKey currentGroupKey;

  private String crosstabFilterGroup;

  /**
   * Constructs a new function.
   * <p>
   * Initially the function has no name...be sure to assign one before using the function.
   */
  public TotalGroupSumFunction() {
    stateSequence = new StateSequence<>();
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    globalStateKey = event.getState().getProcessKey();
    if ( isPrepareRunLevel( event ) ) {
      result = new Sequence<>();

      stateSequence.clear();

      stateSequence.add( globalStateKey, result );

      lastGroupSequenceNumber = ZERO_I;
    } else {

      if ( stateSequence.resultExists() ) {
        result = stateSequence.getResult( ZERO_I );
      } else {
        result = null;
      }

      lastGroupSequenceNumber = ZERO_I;
    }
  }

  protected boolean isPrepareRunLevel( final ReportEvent event ) {
    return FunctionUtilities.isDefinedPrepareRunLevel( this, event );
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      currentGroupKey = event.getState().getProcessKey();


      if ( isPrepareRunLevel( event ) ) {
        clear();


        int pos = this.stateSequence.getKeyIndex( currentGroupKey );
        if ( pos == -1 ) {
          this.stateSequence.add( currentGroupKey, result );
        } else {
          stateSequence.updateResult( pos, result );
        }

      } else {
        // Activate the current group, which was filled in the prepare run.

        int found = this.stateSequence.getKeyIndex( currentGroupKey );
        if ( found < ZERO_I ) {
          result = null;
        } else {
          result = stateSequence.getResult( found );
        }
      }
    }

    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  protected void clear() {
    result = new Sequence<>();
    lastGroupSequenceNumber = ZERO_I;
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( field == null ) {
      return;
    }

    if ( isPrepareRunLevel( event ) == false ) {
      return;
    }

    final BigDecimal value = ExpressionUtilities.convertToBigDecimal( event.getDataRow().get( getField() ) );
    if ( value == null ) {
      return;
    }

    final BigDecimal oldValue = result.get( lastGroupSequenceNumber );
    if ( oldValue == null ) {
      result.set( lastGroupSequenceNumber, value );
    } else {
      result.set( lastGroupSequenceNumber, oldValue.add( value ) );
    }
  }

  public Object clone() throws CloneNotSupportedException {
    final TotalGroupSumFunction o = (TotalGroupSumFunction) super.clone();
    o.stateSequence = new StateSequence<>( stateSequence.getKeys().size() );

    if ( result != null ) {
      o.result = result.clone();
    }

    for ( int i = 0; i < stateSequence.getKeys().size(); i++ ) {
      final ReportStateKey reportStateKey = stateSequence.getKeys().get( i );
      if ( reportStateKey.equals( globalStateKey ) || reportStateKey.equals( currentGroupKey ) ) {
        o.stateSequence.add( reportStateKey, o.result );
      } else {
        o.stateSequence.add( reportStateKey, stateSequence.getResult( i ) );
      }

    }

    return o;
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Returns the name of the group to be totalled.
   *
   * @return the group name.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Defines the name of the group to be totalled. If the name is null, all groups are totalled.
   *
   * @param group the group name.
   */
  public void setGroup( final String group ) {
    this.group = group;
  }

  /**
   * Return the current function value.
   * <p>
   * The value depends (obviously) on the function implementation. For example, a page counting function will return the
   * current page number.
   *
   * @return The value of the function.
   */
  public Object getValue() {
    if ( result == null ) {
      return ZERO;
    }

    final BigDecimal value = result.get( lastGroupSequenceNumber );
    if ( value == null ) {
      return ZERO;
    }
    return value;
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TotalGroupSumFunction function = (TotalGroupSumFunction) super.getInstance();
    function.result = null;
    function.stateSequence = new StateSequence<>();

    return function;
  }

  /**
   * Helper function for the serialization.
   *
   * @param in the input stream.
   * @throws IOException            if an IO error occured.
   * @throws ClassNotFoundException if a required class could not be found.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.result = null;

    this.stateSequence = new StateSequence<>();
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }
}
