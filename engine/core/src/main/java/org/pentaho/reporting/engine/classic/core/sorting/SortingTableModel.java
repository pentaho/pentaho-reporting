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


package org.pentaho.reporting.engine.classic.core.sorting;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;

import java.util.List;

public class SortingTableModel extends IndexedMetaTableModel {
  private int[] index;

  public SortingTableModel( final MetaTableModel backend, final List<SortConstraint> sortConstraints ) {
    super( backend );
    index = TableSorter.sort( backend, sortConstraints.toArray( new SortConstraint[sortConstraints.size()] ) );
  }

  protected int mapRow( final int row ) {
    return index[row];
  }
}
