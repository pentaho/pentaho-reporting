/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;

import javax.swing.*;

public class SubreportConfigureHandler extends AbstractSubreportHandler<SubReport> {

  public SubreportConfigureHandler( final SubReport subReport,
                                    final Band parent,
                                    final ReportElementEditorContext dragContext, final boolean rootband ) {
    super( subReport, parent, dragContext, rootband );
  }

  public SubreportConfigureHandler( final SubReport subReport,
                                    final Band parent,
                                    final ReportDesignerContext designerContext,
                                    final ReportDocumentContext renderContext ) {
    super( subReport, parent, designerContext, renderContext );
  }

  boolean showConfirmationDialog() {
    final UndoManager undo = renderContext.getUndo();
    if ( rootband ) {
      final int result = JOptionPane.showOptionDialog( component,
        Messages.getString( "SubreportReportElementDragHandler.BandedOrInlineSubreportQuestion" ),
        Messages.getString( "SubreportReportElementDragHandler.InsertSubreport" ),
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
        new String[] { Messages.getString( "SubreportReportElementDragHandler.Inline" ),
          Messages.getString( "SubreportReportElementDragHandler.Banded" ),
          Messages.getString( "SubreportReportElementDragHandler.Cancel" ) },
        Messages.getString( "SubreportReportElementDragHandler.Inline" ) );
      if ( result == JOptionPane.CLOSED_OPTION || result == 2 ) {
        return false;
      }

      if ( result == 0 ) {
        undo.addChange( Messages.getString( "SubreportReportElementDragHandler.UndoEntry" ),
          new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
        parent.addElement( subReport );
      } else {
        final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;
        undo.addChange( Messages.getString( "SubreportReportElementDragHandler.UndoEntry" ),
          new BandedSubreportEditUndoEntry( parent.getObjectID(), arb.getSubReportCount(), null, subReport ) );
        arb.addSubReport( subReport );
      }
    } else {
      undo.addChange( Messages.getString( "SubreportReportElementDragHandler.UndoEntry" ),
        new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
      parent.addElement( subReport );
    }
    return true;
  }

  void createSubReportTab() {
    final AbstractReportDefinition reportDefinition = designerContext.getActiveContext().getReportDefinition();
    try {
      // Create the new subreport tab and update the active context to point to new subreport.
      subReport.setDataFactory( reportDefinition.getDataFactory() );

      final int idx = designerContext.addSubReport( designerContext.getActiveContext(), subReport );
      designerContext.setActiveDocument( designerContext.getReportRenderContext( idx ) );
    } catch ( ReportDataFactoryException e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }

  public static void configureDefaults( final SubReport visualElement ) {
    visualElement.setAutoSort( Boolean.TRUE );
    visualElement.getRelationalGroup( 0 ).getHeader()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getRelationalGroup( 0 ).getFooter()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getDetailsFooter()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getDetailsHeader()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getNoDataBand()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getWatermark()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
  }
}
