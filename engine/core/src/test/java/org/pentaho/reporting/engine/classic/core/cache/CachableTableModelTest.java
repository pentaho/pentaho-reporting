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

package org.pentaho.reporting.engine.classic.core.cache;


import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import javax.swing.table.TableModel;
import static org.junit.Assert.assertTrue;
import java.util.Date;

public class CachableTableModelTest {
  @Test
  public void testIsSafeToCache() {
    TypedTableModel model = new TypedTableModel( );
    model.addColumn( "id", java.lang.String.class );
    model.addColumn( "id", java.lang.Integer.class );
    model.addColumn( "bb", java.lang.Boolean.class );
    model.addColumn( "bb", java.util.Date.class );
    model.addColumn( "id", java.lang.Integer.TYPE );
    model.addRow( "R0", "C0", "1" );
    model.addRow( "R0", "C1", 1 );
    model.addRow( "R2", "C2", true );
    model.addRow( "R2", "C3", new Date() );
    model.addRow( "R0", "C1", 1 );

    TableModel tableModel = new CachableTableModel( model );
    assertTrue( CachableTableModel.isSafeToCache( tableModel ) );
  }
}
