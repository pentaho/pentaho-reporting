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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.base;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creation-Date: 09.04.2007, 10:59:35
 *
 * @author Thomas Morgner
 */
public abstract class AbstractPageableOutputProcessor extends AbstractOutputProcessor implements
    PageableOutputProcessor {
  private List physicalPages;

  protected AbstractPageableOutputProcessor() {
    this.physicalPages = new ArrayList();
  }

  protected void processingPagesFinished() {
    super.processingPagesFinished();
    physicalPages = Collections.unmodifiableList( physicalPages );
  }

  public int getPhysicalPageCount() {
    return physicalPages.size();
  }

  public PhysicalPageKey getPhysicalPage( final int page ) {
    if ( isPaginationFinished() == false ) {
      throw new IllegalStateException();
    }

    return (PhysicalPageKey) physicalPages.get( page );
  }

  protected LogicalPageKey createLogicalPage( final int width, final int height ) {
    final LogicalPageKey key = super.createLogicalPage( width, height );
    for ( int h = 0; h < key.getHeight(); h++ ) {
      for ( int w = 0; w < key.getWidth(); w++ ) {
        physicalPages.add( key.getPage( w, h ) );
      }
    }
    return key;
  }

  protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
    final PageGrid pageGrid = logicalPage.getPageGrid();
    final int rowCount = pageGrid.getRowCount();
    final int colCount = pageGrid.getColumnCount();

    final PageFlowSelector selector = getFlowSelector();
    if ( selector != null ) {
      if ( selector.isLogicalPageAccepted( logicalPageKey ) ) {
        processLogicalPage( logicalPageKey, logicalPage );
      }

      for ( int row = 0; row < rowCount; row++ ) {
        for ( int col = 0; col < colCount; col++ ) {
          final PhysicalPageKey pageKey = logicalPageKey.getPage( col, row );
          if ( selector.isPhysicalPageAccepted( pageKey ) ) {
            processPhysicalPage( pageGrid, logicalPage, row, col, pageKey );
          }
        }
      }
    }
  }

  protected abstract PageFlowSelector getFlowSelector();

  protected abstract void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage,
      final int row, final int col, final PhysicalPageKey pageKey ) throws ContentProcessingException;

  protected abstract void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage )
    throws ContentProcessingException;

}
