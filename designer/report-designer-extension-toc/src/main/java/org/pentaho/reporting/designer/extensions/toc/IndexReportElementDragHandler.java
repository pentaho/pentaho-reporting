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
import org.pentaho.reporting.engine.classic.extensions.toc.IndexElement;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Locale;

public class IndexReportElementDragHandler extends AbstractSubReportElementDragHandler {
  public IndexReportElementDragHandler() {
  }

  protected Element createElement( final ElementMetaData elementMetaData,
                                   final String fieldName,
                                   final ReportDocumentContext context ) throws InstantiationException {
    final ElementType type = elementMetaData.create();
    final IndexElement visualElement = new IndexElement();
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
    SwingUtilities.invokeLater( new IndexReportConfigureHandler
      ( (IndexElement) visualElement, target, dragContext, rootBand == target ) );
  }

  private static class IndexReportConfigureHandler implements Runnable {
    private IndexElement subReport;
    private Band parent;
    private ReportElementEditorContext dragContext;
    private boolean rootband;

    private IndexReportConfigureHandler( final IndexElement subReport,
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
          Messages.getInstance().getString( "IndexElementDragHandler.BandedOrInlineSubreportQuestion" ),
          Messages.getInstance().getString( "IndexElementDragHandler.InsertSubreport" ),
          JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
          new String[] { Messages.getInstance().getString( "IndexElementDragHandler.Inline" ),
            Messages.getInstance().getString( "IndexElementDragHandler.Banded" ),
            Messages.getInstance().getString( "IndexElementDragHandler.Cancel" ) },
          Messages.getInstance().getString( "IndexElementDragHandler.Inline" ) );
        if ( result == JOptionPane.CLOSED_OPTION || result == 2 ) {
          return;
        }

        if ( result == 0 ) {
          final ReportDocumentContext context = dragContext.getRenderContext();
          final UndoManager undo = context.getUndo();
          undo.addChange( Messages.getInstance().getString( "IndexElementDragHandler.UndoEntry" ),
            new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
          parent.addElement( subReport );
        } else {
          final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;

          final ReportDocumentContext context = dragContext.getRenderContext();
          final UndoManager undo = context.getUndo();
          undo.addChange( Messages.getInstance().getString( "IndexElementDragHandler.UndoEntry" ),
            new BandedSubreportEditUndoEntry( parent.getObjectID(), arb.getSubReportCount(), null, subReport ) );
          arb.addSubReport( subReport );
        }
      } else {
        final ReportDocumentContext context = dragContext.getRenderContext();
        final UndoManager undo = context.getUndo();
        undo.addChange( Messages.getInstance().getString( "IndexElementDragHandler.UndoEntry" ),
          new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
        parent.addElement( subReport );
      }

      dragContext.getRenderContext().getSelectionModel().setSelectedElements( new Object[] { subReport } );

    }
  }
}
