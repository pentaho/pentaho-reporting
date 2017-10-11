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
