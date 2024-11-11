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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

/**
 * A report function that counts the total of groups in a report. If a null-groupname is given, all groups are counted.
 * <p/>
 * A group can be defined using the property "group". If the group property is not set, all group starts get counted.
 *
 * @author Thomas Morgner
 */
public class TotalGroupCountFunction extends GroupCountFunction {
  /**
   * A map of results, keyed by the process-key.
   */
  private transient HashMap<ReportStateKey, Integer> results;
  /**
   * The currently computed result.
   */
  private transient Integer result;
  /**
   * The global state key is used to store the result for the whole report.
   */
  private transient ReportStateKey globalStateKey;
  /**
   * The current group key is used to store the result for the current group.
   */
  private transient ReportStateKey groupStateKey;

  /**
   * Default constructor.
   */
  public TotalGroupCountFunction() {
    results = new HashMap<ReportStateKey, Integer>();
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event
   *          the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    super.reportInitialized( event );
    globalStateKey = event.getState().getProcessKey();
    if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
      results.clear();
      result = IntegerCache.getInteger( getCount() );
      results.put( globalStateKey, result );
    } else {
      result = results.get( globalStateKey );
    }
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    super.groupStarted( event );

    if ( FunctionUtilities.isDefinedGroup( getParentGroup(), event ) ) {
      groupStateKey = event.getState().getProcessKey();
      if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
        result = IntegerCache.getInteger( getCount() );
        results.put( globalStateKey, result );
        results.put( groupStateKey, result );
        return;
      } else {
        // Activate the current group, which was filled in the prepare run.
        result = results.get( groupStateKey );
      }
    }

    final String definedGroupName = getGroup();
    if ( definedGroupName == null || FunctionUtilities.isDefinedGroup( definedGroupName, event ) ) {
      // count all groups...
      if ( FunctionUtilities.isDefinedPrepareRunLevel( this, event ) ) {
        result = IntegerCache.getInteger( getCount() );
        results.put( globalStateKey, result );
        results.put( groupStateKey, result );
      }
    }
  }

  /**
   * Returns the computed value.
   *
   * @return the computed value.
   */
  public Object getValue() {
    return result;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TotalGroupCountFunction fn = (TotalGroupCountFunction) super.getInstance();
    fn.results = new HashMap<ReportStateKey, Integer>();
    return fn;
  }

  /**
   * Helper function for the serialization.
   *
   * @param in
   *          the input stream.
   * @throws IOException
   *           if an IO error occured.
   * @throws ClassNotFoundException
   *           if a required class could not be found.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.results = new HashMap<ReportStateKey, Integer>();
  }

}
