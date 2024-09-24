/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * Removed finished block-boxes. The boxes have to be marked as 'finished' by the flow output target or nothing will be
 * removed at all. The boxes marked as finished will be replaced by 'FinishedRenderNodes'. This step preserves nodes
 * that have pagebreaks.
 *
 * @author Thomas Morgner
 */
public final class CleanFlowBoxesStep extends CleanPaginatedBoxesStep {
  public CleanFlowBoxesStep() {
  }

  protected boolean checkFinishedForNode( final RenderNode currentNode ) {
    // todo: We should be able to consolidate the FINISH_TABLE and FINISH_PAGE flags into one.
    return currentNode.isFinishedTable();
  }

  public long compute( final LogicalPageBox pageBox ) {
    return super.compute( pageBox, pageBox.getProcessedTableOffset() );
  }
}
