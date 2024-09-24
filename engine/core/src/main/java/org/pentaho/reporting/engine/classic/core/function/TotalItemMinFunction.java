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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A report function that pre-computes the smallest item in a group. The Items must be mutually comparable among each
 * other or the function will fail. Comparing dates with strings will not work.
 * <p/>
 * Like all Total-Functions, this function produces a precomputed grand total. The function's result is precomputed once
 * and will not change later. Printing the result of this function in a group header returns the same value as printed
 * in the group-footer.
 * <p/>
 * The ItemMinFunction can be used to produce a running minimum for a group or report.
 * <p/>
 * A group can be defined using the property "group". If the group property is not set, the function will process the
 * whole report.
 *
 * @author Thomas Morgner
 */
public class TotalItemMinFunction extends AbstractFunction implements FieldAggregationFunction {
  private static final Log logger = LogFactory.getLog( TotalItemMinFunction.class );
  private static final int ZERO_I = 0;


  private transient int lastGroupSequenceNumber;

  /**
   * The name of the group on which to reset.
   */
  private String group;
  /**
   * The field that should be evaluated.
   */
  private String field;
  /**
   * The currently computed minimum value.
   */
  private transient Sequence<Comparable> result;

  private StateSequence<Comparable> stateSequence;

  /**
   * The global state key is used to store the result for the whole report.
   */
  private transient ReportStateKey globalStateKey;
  private String crosstabFilterGroup;

  /**
   * Default constructor.
   */
  public TotalItemMinFunction() {
    stateSequence = new StateSequence<>();
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
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    globalStateKey = event.getState().getProcessKey();
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
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

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      final ReportStateKey groupStateKey = event.getState().getProcessKey();
      if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
        result = new Sequence<>();
        lastGroupSequenceNumber = ZERO_I;

        stateSequence.add( globalStateKey, result );
        stateSequence.add( groupStateKey, result );

      } else {
        // Activate the current group, which was filled in the prepare run.
        int found = this.stateSequence.getKeyIndex( groupStateKey );
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


  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( field == null ) {
      return;
    }

    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) == false ) {
      return;
    }

    final Object fieldValue = event.getDataRow().get( getField() );
    if ( fieldValue instanceof Comparable == false ) {
      return;
    }

    try {
      final Comparable compare = (Comparable) fieldValue;

      final Comparable oldValue = result.get( lastGroupSequenceNumber );
      if ( oldValue == null || oldValue.compareTo( compare ) > ZERO_I ) {
        result.set( lastGroupSequenceNumber, compare );
      }
    } catch ( Exception e ) {
      logger.error( "TotalItemMinFunction.advanceItems(): problem comparing values." );
    }
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Returns the name of the group for which the minimum should be computed.
   *
   * @return the group name.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Defines the name of the group to be totalled. If the name is null, the minimum for the whole report is computed.
   *
   * @param group the group name.
   */
  public void setGroup( final String group ) {
    this.group = group;
  }

  /**
   * Returns the computed minimum value.
   *
   * @return The computed minimum value.
   */
  public Object getValue() {
    if ( result == null ) {
      return null;
    }

    return result.get( lastGroupSequenceNumber );
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TotalItemMinFunction function = (TotalItemMinFunction) super.getInstance();
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
    stateSequence = new StateSequence<>();
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

}
