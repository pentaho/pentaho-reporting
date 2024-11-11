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
