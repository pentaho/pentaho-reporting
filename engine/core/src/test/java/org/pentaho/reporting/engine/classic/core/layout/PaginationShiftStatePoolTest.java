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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationShiftStatePool;

public class PaginationShiftStatePoolTest extends TestCase {
  private static class TestPaginationShiftStatePool extends PaginationShiftStatePool {
    public boolean isBlock( final int box ) {
      return super.isBlock( box );
    }
  }

  public void testRecognizeBoxes() {
    TestPaginationShiftStatePool pool = new TestPaginationShiftStatePool();
    assertTrue( pool.isBlock( LayoutNodeTypes.TYPE_BOX_TABLE ) );
    assertTrue( pool.isBlock( LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) );
    assertTrue( pool.isBlock( LayoutNodeTypes.TYPE_BOX_BLOCK ) );
    assertTrue( pool.isBlock( LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) );
    assertTrue( pool.isBlock( LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) );

    assertFalse( pool.isBlock( LayoutNodeTypes.TYPE_BOX_CANVAS ) );
    assertFalse( pool.isBlock( LayoutNodeTypes.TYPE_BOX_ROWBOX ) );
    assertFalse( pool.isBlock( LayoutNodeTypes.TYPE_BOX_INLINE ) );
    assertFalse( pool.isBlock( LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) );
  }
}
