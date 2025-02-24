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
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.elements.InsertCrosstabGroupAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CrosstabConfigureHandler extends AbstractSubreportHandler<CrosstabElement> {
  public CrosstabConfigureHandler( final CrosstabElement subReport,
                                   final Band parent,
                                   final ReportElementEditorContext dragContext, final boolean rootband ) {
    super( subReport, parent, dragContext, rootband );
  }

  public CrosstabConfigureHandler( final CrosstabElement subReport,
                                   final Band parent,
                                   final ReportDesignerContext designerContext,
                                   final ReportDocumentContext renderContext ) {
    super( subReport, parent, designerContext, renderContext );
  }

  boolean showConfirmationDialog() {
    final UndoManager undo = renderContext.getUndo();
    if ( rootband ) {
      final int result = JOptionPane.showOptionDialog( component,
        Messages.getString( "CrosstabReportElementDragHandler.BandedOrInlineSubreportQuestion" ),
        Messages.getString( "CrosstabReportElementDragHandler.InsertSubreport" ),
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
        new String[] { Messages.getString( "CrosstabReportElementDragHandler.Inline" ),
          Messages.getString( "CrosstabReportElementDragHandler.Banded" ),
          Messages.getString( "CrosstabReportElementDragHandler.Cancel" ) },
        Messages.getString( "CrosstabReportElementDragHandler.Inline" ) );
      if ( result == JOptionPane.CLOSED_OPTION || result == 2 ) {
        return false;
      }

      if ( result == 0 ) {
        undo.addChange( Messages.getString( "CrosstabReportElementDragHandler.UndoEntry" ),
          new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
        parent.addElement( subReport );
      } else {
        final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;
        undo.addChange( Messages.getString( "CrosstabReportElementDragHandler.UndoEntry" ),
          new BandedSubreportEditUndoEntry( parent.getObjectID(), arb.getSubReportCount(), null, subReport ) );
        arb.addSubReport( subReport );
      }
    } else {
      undo.addChange( Messages.getString( "CrosstabReportElementDragHandler.UndoEntry" ),
        new ElementEditUndoEntry( parent.getObjectID(), parent.getElementCount(), null, subReport ) );
      parent.addElement( subReport );
    }
    return true;
  }

  void createSubReportTab() {
    final AbstractReportDefinition reportDefinition = designerContext.getActiveContext().getReportDefinition();
    try {
      // Create the new subreport tab - this is where the contents of the Crosstab
      // dialog will go.  Zoom the crosstab canvas to 150% as crosstab has a lot of elements to display
      subReport.setDataFactory( reportDefinition.getDataFactory() );
      subReport.getReportDefinition()
        .setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.ZOOM, 1.5f );

      final int idx = designerContext.addSubReport( designerContext.getActiveContext(), subReport );
      designerContext.setActiveDocument( designerContext.getReportRenderContext( idx ) );
    } catch ( ReportDataFactoryException e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }

  void doSetQuery( final String queryName ) {
    // Invoke Crosstab dialog
    final InsertCrosstabGroupAction crosstabAction = new InsertCrosstabGroupAction();
    crosstabAction.setReportDesignerContext( designerContext );
    crosstabAction.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ) );
  }


  public static void configureDefaults( final CrosstabElement visualElement ) {
    visualElement.setAutoSort( Boolean.TRUE );

    // Hide all bands except for Details
    visualElement.getPageHeader()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getReportHeader()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getReportFooter()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getPageFooter()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
    visualElement.getWatermark()
      .setAttribute( ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,
        Boolean.TRUE );
  }
}
