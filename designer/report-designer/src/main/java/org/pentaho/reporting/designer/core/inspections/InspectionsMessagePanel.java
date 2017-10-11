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
