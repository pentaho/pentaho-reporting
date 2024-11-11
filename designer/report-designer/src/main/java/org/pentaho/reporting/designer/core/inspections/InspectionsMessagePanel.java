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


package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InspectionsMessagePanel extends JPanel {
  private class InspectionSelectionHandler extends MouseAdapter implements KeyListener {
    private InspectionSelectionHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() != 2 || e.getButton() != MouseEvent.BUTTON1 ) {
        return;
      }
      final int row = table.rowAtPoint( e.getPoint() );
      if ( row != -1 ) {
        performSelection( dataModel.getInspectionResult( row ) );
      }
    }

    public void keyTyped( final KeyEvent e ) {
      if ( e.getKeyCode() != KeyEvent.VK_ENTER ) {
        return;
      }

      final int selectedRow = table.getSelectedRow();

      if ( selectedRow == -1 ) {
        return;
      }

      final InspectionResult inspectionResult = dataModel.getInspectionResult( selectedRow );
      performSelection( inspectionResult );
    }

    public void keyPressed( final KeyEvent e ) {

    }

    public void keyReleased( final KeyEvent e ) {

    }
  }

  private InspectionResultTableModel dataModel;
  private InspectionResultTable table;
  private ReportDocumentContext reportRenderContext;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public InspectionsMessagePanel() {
    setLayout( new BorderLayout() );

    dataModel = new InspectionResultTableModel();

    table = new InspectionResultTable();
    table.setModel( dataModel );
    table.addKeyListener( new InspectionSelectionHandler() );
    table.addMouseListener( new InspectionSelectionHandler() );

    add( new JScrollPane( table ) );
  }

  private void performSelection( final InspectionResult inspectionResult ) {
    if ( inspectionResult == null ) {
      return;
    }
    if ( reportRenderContext == null ) {
      return;
    }

    final LocationInfo[] locationInfos = inspectionResult.getLocationInfos();
    final ArrayList<Object> selections = new ArrayList<Object>( locationInfos.length );
    for ( int i = 0; i < locationInfos.length; i++ ) {
      final LocationInfo locationInfo = locationInfos[ i ];
      selections.add( locationInfo.getReportElement() );
    }
    reportRenderContext.getSelectionModel().setSelectedElements( selections.toArray() );
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    table.setEnabled( enabled );
  }

  public void clear() {
    dataModel.clear();
  }

  public InspectionResultListener getResultHandler() {
    return dataModel;
  }

  public ReportDocumentContext getReportRenderContext() {
    return reportRenderContext;
  }

  public void setReportRenderContext( final ReportDocumentContext reportRenderContext ) {
    this.reportRenderContext = reportRenderContext;
  }
}
