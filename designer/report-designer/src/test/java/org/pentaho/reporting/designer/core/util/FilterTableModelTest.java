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


package org.pentaho.reporting.designer.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.util.table.filter.CompoundFilter;
import org.pentaho.reporting.designer.core.util.table.filter.DefaultFilterTableModel;
import org.pentaho.reporting.designer.core.util.table.filter.Filter;

import javax.swing.table.DefaultTableModel;

public class FilterTableModelTest extends TestCase {
  private static class BooleanFilter implements Filter {
    public Result isMatch( final Object o ) {
      if ( Boolean.TRUE.equals( o ) ) {
        return Result.REJECT;
      }
      return Result.UNDECIDED;
    }
  }

  public FilterTableModelTest() {
  }

  public FilterTableModelTest( final String name ) {
    super( name );
  }

  public void testBorderCase() {
    final DefaultTableModel model = new DefaultTableModel( 3, 0 );
    model.addColumn( "Boolean" );
    model.setValueAt( Boolean.TRUE, 0, 0 );
    model.setValueAt( Boolean.FALSE, 0, 0 );
    model.setValueAt( Boolean.TRUE, 0, 0 );

    final DefaultFilterTableModel filter = new DefaultFilterTableModel( model, 0 );
    filter.setFilters( CompoundFilter.create( new BooleanFilter() ) );
    filter.mapToModel( 1 );
    filter.mapToModel( 0 );
  }
}
