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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InspectionsMessageDialog extends CommonDialog {
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
  private ReportDesignerContext designerContext;

  public InspectionsMessageDialog() {
  }

  public InspectionsMessageDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public InspectionsMessageDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    dataModel = new InspectionResultTableModel();

    table = new InspectionResultTable();
    table.setModel( dataModel );
    table.addKeyListener( new InspectionSelectionHandler() );
    table.addMouseListener( new InspectionSelectionHandler() );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.InspectionsMessage";
  }


  protected boolean hasCancelButton() {
    return false;
  }

  private void performSelection( final InspectionResult inspectionResult ) {
  }

  public void performShowResult( final ReportDesignerContext context, final InspectionResult[] results ) {
    this.designerContext = context;
    try {
      dataModel.clear();
      for ( int i = 0; i < results.length; i++ ) {
        final InspectionResult result = results[ i ];
        dataModel.add( result );
      }
      performEdit();
    } finally {
      designerContext = null;
    }
  }

  protected Component createContentPane() {
    return new JScrollPane( table );
  }
}
