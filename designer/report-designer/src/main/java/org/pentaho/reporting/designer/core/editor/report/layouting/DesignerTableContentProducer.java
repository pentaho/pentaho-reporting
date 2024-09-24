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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DesignerTableContentProducer extends TableContentProducer {
  private HashMap<InstanceID, Set<InstanceID>> conflicts;
  private LinkedHashSet<InstanceID> conflictsPerCell;

  public DesignerTableContentProducer( final SheetLayout sheetLayout,
                                       final OutputProcessorMetaData metaData ) {
    super( sheetLayout, metaData );
    conflicts = new HashMap<InstanceID, Set<InstanceID>>();
    conflictsPerCell = new LinkedHashSet<InstanceID>();
    setProcessWatermark( false );
  }

  protected boolean startBox( final RenderBox box ) {
    conflicts.remove( box.getInstanceId() );
    return super.startBox( box );
  }

  protected boolean isProcessed( final RenderBox box ) {
    // we process all boxes, regardless of their previous state.
    return false;
  }

  protected void handleContentConflict( final RenderBox box ) {
    super.handleContentConflict( box );

    // shall we collect more information?
    final TableRectangle lookupRectangle = getLookupRectangle();
    final int rectX2 = lookupRectangle.getX2();
    final int rectY2 = lookupRectangle.getY2();

    conflictsPerCell.clear();

    for ( int r = lookupRectangle.getY1(); r < rectY2; r++ ) {
      for ( int c = lookupRectangle.getX1(); c < rectX2; c++ ) {
        final RenderBox content = getContent( r, c );
        if ( content != null ) {
          conflictsPerCell.add( content.getInstanceId() );
        }
      }
    }

    conflicts.put( box.getInstanceId(), new LinkedHashSet<InstanceID>( conflictsPerCell ) );
  }

  public Map<InstanceID, Set<InstanceID>> computeConflicts( final LogicalPageBox box,
                                                            final Map<InstanceID, Set<InstanceID>> collectedConflicts
  ) {
    if ( collectedConflicts == null ) {
      throw new NullPointerException();
    }

    conflicts.clear();
    this.compute( box, false );
    collectedConflicts.putAll( conflicts );
    return collectedConflicts;
  }

  public Map<InstanceID, Set<InstanceID>> computeWatermarkConflics( final LogicalPageBox box,
                                                                    final Map<InstanceID, Set<InstanceID>>
                                                                      collectedConflicts ) {
    if ( collectedConflicts == null ) {
      throw new NullPointerException();
    }

    conflicts.clear();
    this.computeDesigntimeConflicts( box.getWatermarkArea() );
    collectedConflicts.putAll( conflicts );
    return collectedConflicts;
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }
}
