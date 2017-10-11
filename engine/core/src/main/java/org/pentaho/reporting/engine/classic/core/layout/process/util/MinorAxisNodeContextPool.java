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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

public class MinorAxisNodeContextPool extends StackedObjectPool<MinorAxisNodeContext> {
  public MinorAxisNodeContextPool() {
  }

  public MinorAxisNodeContext create() {
    return new MinorAxisNodeContext( this );
  }

  private boolean isEstablishBlockContext( final int nodeType, final boolean definedWidth ) {
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      return definedWidth;
    }

    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_CANVAS ) == LayoutNodeTypes.TYPE_BOX_CANVAS ) {
      return definedWidth;
    }

    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_ROWBOX ) == LayoutNodeTypes.TYPE_BOX_ROWBOX ) {
      return definedWidth;
    }

    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_TABLE ) == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return true;
    }

    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      return true;
    }
    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
      return true;
    }
    return false;
  }

  public MinorAxisNodeContext createContext( final RenderBox box, final MinorAxisNodeContext context,
      final boolean blockLevelNode ) {
    final MinorAxisNodeContext nodeContext;
    final int nodeType = box.getNodeType();
    final boolean horizontal;
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
      nodeContext = new MinorAxisLogicalPageContext( (LogicalPageBox) box );
      horizontal = false;
    } else {
      nodeContext = get();
      nodeContext.reuseParent( context );

      final int layoutNodeType = box.getLayoutNodeType();
      if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW
          || ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) ) {
        horizontal = true;
      } else {
        horizontal = false;
      }
    }

    // auto-boxes do not establish an own block context.
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    nodeContext.reuse( horizontal, blockLevelNode, box.isBoxOverflowX(), isEstablishBlockContext( box.getNodeType(),
        sblp.isDefinedWidth() ) );
    return nodeContext;
  }
}
