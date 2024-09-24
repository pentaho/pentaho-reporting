/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
