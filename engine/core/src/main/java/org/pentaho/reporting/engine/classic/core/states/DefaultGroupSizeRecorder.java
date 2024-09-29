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


package org.pentaho.reporting.engine.classic.core.states;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.IntList;
import org.pentaho.reporting.libraries.base.util.FastStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DefaultGroupSizeRecorder implements GroupSizeRecorder {
  private static class GroupKey implements Comparable<GroupKey> {
    private int[] keys;
    private boolean itemKey;

    private GroupKey( final int[] keys, final boolean itemKey ) {
      if ( keys == null ) {
        throw new NullPointerException();
      }
      this.itemKey = itemKey;
      this.keys = keys;
    }

    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "GroupKey" );
      sb.append( "{keys=" );
      for ( int i = 0; i < keys.length; ++i ) {
        sb.append( i == 0 ? "" : ", " );
        sb.append( keys[i] );
      }
      sb.append( '}' );
      return sb.toString();
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final GroupKey groupKey = (GroupKey) o;
      if ( itemKey != groupKey.itemKey ) {
        return false;
      }
      if ( !Arrays.equals( keys, groupKey.keys ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int hashCode = itemKey ? 1 : 0;
      hashCode = hashCode * 31 + Arrays.hashCode( keys );
      return hashCode;
    }

    public int compareTo( final GroupKey o ) {
      return -toString().compareTo( o.toString() );
    }
  }

  private static final Log logger = LogFactory.getLog( DefaultGroupSizeRecorder.class );
  private Map<GroupKey, Integer> counts;
  private FastStack<GroupKey> keys;
  private IntList groupCounts;
  private int currentGroupIndex;

  public DefaultGroupSizeRecorder() {
    counts = new HashMap<GroupKey, Integer>();
    groupCounts = new IntList( 100 );
    groupCounts.add( 0 );
    currentGroupIndex = -1;
    keys = new FastStack<GroupKey>();
  }

  public Object clone() {
    try {
      // logger.debug("g.clone()");
      final DefaultGroupSizeRecorder rec = (DefaultGroupSizeRecorder) super.clone();
      rec.groupCounts = (IntList) groupCounts.clone();
      rec.keys = (FastStack<GroupKey>) keys.clone();
      return rec;
    } catch ( CloneNotSupportedException cse ) {
      throw new IllegalStateException( cse );
    }
  }

  public void enterGroup() {
    currentGroupIndex += 1;
    groupCounts.set( currentGroupIndex, groupCounts.get( currentGroupIndex ) + 1 );

    final GroupKey key = new GroupKey( groupCounts.toArray(), false );
    keys.push( key );
    // logger.debug("g.enterGroup(); // " + key);

    groupCounts.add( 0 );
  }

  public void enterItems() {
    currentGroupIndex += 1;
  }

  public void advanceItems() {
    // logger.debug("g.advanceItems()");
    groupCounts.set( currentGroupIndex, groupCounts.get( currentGroupIndex ) + 1 );
  }

  public void leaveItems() {
    currentGroupIndex -= 1;
  }

  public void leaveGroup() {
    // logger.debug("g.leaveGroup()");
    final Integer i = groupCounts.pop();
    GroupKey k = keys.pop();

    counts.put( k, i );
    currentGroupIndex -= 1;
  }

  public void reset() {
    // logger.debug("g.reset()");
    groupCounts.clear();
    keys.clear();
    groupCounts.add( 0 );
    currentGroupIndex = -1;
  }

  public int getCurrentGroupIndex() {
    return currentGroupIndex;
  }

  public int[] getGroupCounts() {
    return groupCounts.toArray();
  }

  /**
   * Returns the predicted size of the current group. This can return <code>null</code> if there is no prediction.
   *
   * @return
   */
  public Integer getPredictedStateCount() {
    if ( groupCounts.size() == 0 ) {
      logger.debug( "Outside of any group: Unable to predict results" );
      return null;
    }

    final Integer prediction = counts.get( keys.peek() );
    return prediction;
  }
}
