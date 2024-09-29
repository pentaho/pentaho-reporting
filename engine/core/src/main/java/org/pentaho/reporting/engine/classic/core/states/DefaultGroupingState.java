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

import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DefaultGroupingState implements GroupingState {
  public static final GroupingState EMPTY = new DefaultGroupingState();

  private int currentGroup;
  private FastStack<GroupStartRecord> groupStarts;

  protected DefaultGroupingState() {
    this.currentGroup = -1;
    this.groupStarts = new FastStack<GroupStartRecord>();
  }

  public DefaultGroupingState( final int currentGroup, final FastStack<GroupStartRecord> groupStarts ) {
    this.currentGroup = currentGroup;
    this.groupStarts = groupStarts;
  }

  public int getCurrentGroup() {
    return currentGroup;
  }

  public int getGroupStartRow( final String group ) {
    final int size = groupStarts.size();
    for ( int i = 0; i < size; i++ ) {
      final GroupStartRecord o = groupStarts.get( i );
      if ( ObjectUtilities.equal( o.getGroupName(), group ) ) {
        return o.getRow();
      }
      if ( ObjectUtilities.equal( o.getGeneratedGroupName(), group ) ) {
        return o.getRow();
      }
    }
    return 0;
  }

  public int getGroupStartRow( final int group ) {
    if ( group < 0 || group >= groupStarts.size() ) {
      if ( groupStarts.isEmpty() ) {
        return 0;
      }
      final GroupStartRecord o = groupStarts.peek();
      return o.getRow();
    }

    final GroupStartRecord o = groupStarts.get( group );
    return o.getRow();
  }
}
