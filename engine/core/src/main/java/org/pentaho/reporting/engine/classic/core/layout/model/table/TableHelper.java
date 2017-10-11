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

package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class TableHelper {
  public static TableRenderBox lookupTable( TableCellRenderBox box ) {
    final RenderBox layoutParent = box.getLayoutParent();
    if ( layoutParent == null ) {
      throw new IllegalStateException( "Missing table" );
    }

    if ( layoutParent.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
      return lookupTable( (TableRowRenderBox) layoutParent );
    }
    throw new IllegalStateException( "Missing table" );
  }

  public static TableRenderBox lookupTable( TableRowRenderBox box ) {
    final RenderBox layoutParent = box.getLayoutParent();
    if ( layoutParent == null ) {
      throw new IllegalStateException( "Missing table" );
    }

    if ( layoutParent.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      return lookupTable( (TableSectionRenderBox) layoutParent );
    }
    throw new IllegalStateException( "Missing table" );
  }

  public static TableRenderBox lookupTable( TableSectionRenderBox box ) {
    final RenderBox layoutParent = box.getLayoutParent();
    if ( layoutParent == null ) {
      throw new IllegalStateException( "Missing table" );
    }

    if ( layoutParent.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return (TableRenderBox) layoutParent;
    }
    throw new IllegalStateException( "Missing table" );
  }

}
