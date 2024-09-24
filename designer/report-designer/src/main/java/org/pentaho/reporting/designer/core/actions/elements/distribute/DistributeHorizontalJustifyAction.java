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

package org.pentaho.reporting.designer.core.actions.elements.distribute;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class DistributeHorizontalJustifyAction extends AbstractElementSelectionAction {
  private static class ElementPositionComparator implements Comparator<Element> {
    public int compare( final Element o1, final Element o2 ) {
      final CachedLayoutData data1 = ModelUtility.getCachedLayoutData( o1 );
      final long x1 = data1.getX();
      final CachedLayoutData data2 = ModelUtility.getCachedLayoutData( o2 );
      final long x2 = data2.getX();
      if ( x1 < x2 ) {
        return -1;
      }
      if ( x1 > x2 ) {
        return +1;
      }
      return 0;
    }
  }

  public DistributeHorizontalJustifyAction() {
    putValue( Action.NAME, ActionMessages.getString( "DistributeHorizontalJustifyAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "DistributeHorizontalJustifyAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "DistributeHorizontalJustifyAction.Mnemonic" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getDistributeGapsHorizontalIcon() );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getString( "DistributeHorizontalJustifyAction.Accelerator" ) );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType( Element.class );
    if ( visualElements.size() <= 2 ) {
      return;
    }

    final List<Element> reportElements = ModelUtility.filterParents( visualElements );
    if ( reportElements.size() <= 2 ) {
      return;
    }

    Collections.sort( reportElements, new ElementPositionComparator() );
    final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder( reportElements );
    final Element[] carrier = new Element[ 1 ];

    long totalHeight = 0;
    long topmostY = Long.MAX_VALUE;
    long bottommostY = Long.MIN_VALUE;

    for ( Element element : reportElements ) {
      final CachedLayoutData layoutData = ModelUtility.getCachedLayoutData( element );
      totalHeight += layoutData.getWidth();
      topmostY = Math.min( topmostY, layoutData.getX() );
      bottommostY = Math.max( bottommostY, layoutData.getX() + layoutData.getWidth() );
    }

    final long gap = ( bottommostY - topmostY - totalHeight ) / ( reportElements.size() - 1 );
    long currentY = topmostY;
    for ( Element reportElement : reportElements ) {
      final CachedLayoutData layoutData = ModelUtility.getCachedLayoutData( reportElement );
      final long height = layoutData.getWidth();

      carrier[ 0 ] = reportElement;
      final Point2D.Double originPoint =
        new Point2D.Double( StrictGeomUtility.toExternalValue( layoutData.getX() ), 0 );
      final MoveDragOperation mop = new MoveDragOperation
        ( Arrays.asList( carrier ), originPoint, EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE );
      mop.update( new Point2D.Double( StrictGeomUtility.toExternalValue( currentY ), 0 ), 1 );
      mop.finish();

      currentY += height + gap;
    }
    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    getActiveContext().getUndo()
      .addChange( ActionMessages.getString( "DistributeHorizontalJustifyAction.UndoName" ), massElementStyleUndoEntry );
  }

}
