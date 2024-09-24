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
