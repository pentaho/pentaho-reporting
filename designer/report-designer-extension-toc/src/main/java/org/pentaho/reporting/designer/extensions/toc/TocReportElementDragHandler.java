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

package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.elements.AbstractSubReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.elements.SubreportConfigureHandler;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.extensions.toc.TocElement;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Locale;

public class TocReportElementDragHandler extends AbstractSubReportElementDragHandler {
  public TocReportElementDragHandler() {
  }

  protected Element createElement( final ElementMetaData elementMetaData,
                                   final String fieldName,
                                   final ReportDocumentContext context ) throws InstantiationException {
    final ElementType type = elementMetaData.create();
    final TocElement visualElement = new TocElement();
    SubreportConfigureHandler.configureDefaults( visualElement );
    type.configureDesignTimeDefaults( visualElement, Locale.getDefault() );

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH );
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT );
    return visualElement;
  }

  protected void postProcessDrop( final Element visualElement,
                                  final Band target,
                                  final ReportElementEditorContext dragContext,
                                  final Point2D point ) {
    final Element rootBand = findRootBand( dragContext, point );
    SwingUtilities.invokeLater( new TocReportConfigureHandler
      ( (TocElement) visualElement, target, dragContext, rootBand == target ) );
  }

  private static class TocReportConfigureHandler implements Runnable {
    private TocElement subReport;
    private Band parent;
    private ReportElementEditorContext dragContext;
    private boolean rootband;

    private TocReportConfigureHandler( final TocElement subReport,
                                       final Band parent,
                                       final ReportElementEditorContext dragContext,
                                       final boolean rootband ) {
      this.subReport = subReport;
      this.parent = parent;
      this.dragContext = dragContext;
      this.rootband = rootband;
    }

    public void run() {
      if ( rootband ) {
        final int result = JOptionPane.showOptionDialog( dragContext.getRepresentationContainer(),
          Messages.getInstance().getString( "TocElementDragHandler.BandedOrInlineSubreportQuestion" ),
          Messages.getInstance().getString( "TocElementDragHandler.InsertSubreport" ),
          JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
          new String[] { Messages.getInstance().getString( "TocElementDragHandler.Inline" ),
            Messages.getInstance().getString( "TocElementDragHandler.Banded" ),
            Messages.getInstance().getString( "TocElementDragHandler.Cancel" ) },
          Messages.getInstance().getString( "TocElementDragHandler.Inline" ) );
        if ( result == JOptionPane.CLOSED_OPTION || result == 2 ) {
          return;
        }

        if ( result == 0 ) {
          final ReportDocumentContext context = dragContext.getRenderContext();
          final UndoManager undo = context.getUndo();
          undo.addChange( Messages.getInstance().getString( "TocElementDragHandler.UndoEntry" ),
            new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
          parent.addElement( subReport );
        } else {
          final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;

          final ReportDocumentContext context = dragContext.getRenderContext();
          final UndoManager undo = context.getUndo();
          undo.addChange( Messages.getInstance().getString( "TocElementDragHandler.UndoEntry" ),
            new BandedSubreportEditUndoEntry( parent.getObjectID(), arb.getSubReportCount(), null, subReport ) );
          arb.addSubReport( subReport );
        }
      } else {
        final ReportDocumentContext context = dragContext.getRenderContext();
        final UndoManager undo = context.getUndo();
        undo.addChange( Messages.getInstance().getString( "TocElementDragHandler.UndoEntry" ),
          new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
        parent.addElement( subReport );
      }

      dragContext.getRenderContext().getSelectionModel().setSelectedElements( new Object[] { subReport } );

    }
  }
}

