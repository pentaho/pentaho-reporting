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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.BulkArrayList;
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

  private static final int CAPACITY = 30;
  private transient BulkArrayList<ReportStateKey> stateKeys;
  private transient BulkArrayList<Sequence<BigDecimal>> sequences;

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
    this.stateKeys = new BulkArrayList<>( CAPACITY );
    this.sequences = new BulkArrayList<>( CAPACITY );
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

      this.stateKeys.clear();
      this.sequences.clear();

      // first element is always global state key.
      this.stateKeys.add( globalStateKey );
      // and it's result
      this.sequences.add( result );

      lastGroupSequenceNumber = 0;
    } else {

      if ( sequences.size() > 0 ) {
        result = sequences.get( 0 );
      } else {
        result = null;
      }

      lastGroupSequenceNumber = 0;
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

      boolean isGlobalKey = currentGroupKey.equals( globalStateKey );

      if ( isPrepareRunLevel( event ) ) {
        clear();

        if ( isGlobalKey ) {
          // global state key is always 0 position
          sequences.set( 0, result );
        } else {
          int pos = this.groupPos( stateKeys, currentGroupKey );
          if ( pos == -1 ) {
            stateKeys.add( currentGroupKey );
            sequences.add( result );
          } else {
            sequences.set( pos, result );
          }
        }
      } else {
        // Activate the current group, which was filled in the prepare run.
        if ( isGlobalKey ) {
          result = sequences.get( 0 );
        } else {
          int found = groupPos( stateKeys, currentGroupKey );
          if ( found < 0 ) {
            result = null;
          } else {
            result = sequences.get( found );
          }
        }
      }
    }

    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  // from head or from tail?
  private int groupPos( final BulkArrayList<ReportStateKey> list, final ReportStateKey key ) {
    if ( list.size() == 0 || key == null ) {
      return -1;
    }
    ReportStateKey current;
    for ( int j = list.size() - 1; j > -1; j-- ) {
      current = list.get( j );

      // equal is more expensive then hashCode
      if ( current.hashCode() == key.hashCode() && current.equals( key ) ) {
        return j;
      }
    }
    return -1;
  }

  protected void clear() {
    result = new Sequence<>();
    lastGroupSequenceNumber = 0;

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

    o.stateKeys = new BulkArrayList<>( stateKeys.size() );
    o.sequences = new BulkArrayList<>( sequences.size() );

    boolean resultNotNull = result != null;

    if ( resultNotNull ) {
      o.result = result.clone();
      o.stateKeys.set( 0, globalStateKey );
      o.sequences.set( 0, o.result );
    }

    int shift = 0;
    int currentGroupKeyPosition = -1;
    for ( int i = 1; i <= stateKeys.size() - 1; i++ ) {
      ReportStateKey key = stateKeys.get( i );
      // do not copy current group key
      // a note: 2 objects can not be (==), but still be equal to each other.
      if ( currentGroupKey != key && globalStateKey != key ) {
        // the only key we are really interested in is currentGroupKey.
        // hash code is faster then equals, but according to contract hashCode does not guarantee equality.
        if ( key != null && key.hashCode() == currentGroupKey.hashCode() && key.equals( currentGroupKey ) ) {
          currentGroupKeyPosition = i;
        }
        // this is not a current group key neither global - just put at the tail.
        o.stateKeys.set( i + shift, key );
        o.sequences.set( i + shift, sequences.get( i ).clone() );
      } else {
        shift--;
      }
    }

    if ( resultNotNull ) {
      if ( currentGroupKeyPosition == -1 ) {
        o.stateKeys.add( currentGroupKey );
        o.sequences.add( o.result );
      } else if ( currentGroupKeyPosition > 0 ) {
        o.sequences.set( currentGroupKeyPosition, o.result );
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
    function.stateKeys = new BulkArrayList<>( CAPACITY );
    function.sequences = new BulkArrayList<>( CAPACITY );

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

    this.stateKeys = new BulkArrayList<>( CAPACITY );
    this.sequences = new BulkArrayList<>( CAPACITY );
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }
}
