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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

import java.io.Serializable;

public abstract class IterateSimpleReverseStructureProcessStep implements Serializable {
  protected IterateSimpleReverseStructureProcessStep() {
  }

  protected final void startProcessing( final RenderNode node ) {
    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
      final LogicalPageBox box = (LogicalPageBox) node;
      if ( startBox( box ) ) {
        startProcessing( box.getWatermarkArea() );
        startProcessing( box.getHeaderArea() );
        processBoxChilds( box );
        startProcessing( box.getFooterArea() );
        startProcessing( box.getRepeatFooterArea() );
      }
      finishBox( box );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startBox( box ) ) {
        processBoxChilds( box );
      }
      finishBox( box );
    } else {
      processOtherNode( node );
    }
  }

  protected void processBoxChilds( final RenderBox box ) {
    RenderNode node = box.getLastChild();
    while ( node != null ) {
      startProcessing( node );
      node = node.getPrev();
    }
  }

  protected void processOtherNode( final RenderNode node ) {
  }

  protected void finishBox( final RenderBox box ) {
  }

  protected boolean startBox( final RenderBox box ) {
    return true;
  }

}
