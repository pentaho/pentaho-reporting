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

package org.pentaho.reporting.ui.datasources.table;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;

public class EditableHeaderUI extends BasicTableHeaderUI {
  public EditableHeaderUI() {
  }

  protected MouseInputListener createMouseInputListener() {
    return new MouseInputHandler( (EditableHeader) header );
  }

  private class MouseInputHandler extends BasicTableHeaderUI.MouseInputHandler {
    private Component dispatchComponent;
    private EditableHeader header;

    public MouseInputHandler( final EditableHeader header ) {
      this.header = header;
    }

    private void setDispatchComponent( final MouseEvent e ) {
      final Component editorComponent = header.getEditorComponent();
      final Point p = e.getPoint();
      final Point p2 = SwingUtilities.convertPoint( header, p, editorComponent );
      dispatchComponent = SwingUtilities.getDeepestComponentAt( editorComponent, p2.x, p2.y );
    }

    private boolean repostEvent( final MouseEvent e ) {
      if ( dispatchComponent == null ) {
        return false;
      }
      final MouseEvent e2 = SwingUtilities.convertMouseEvent( header, e, dispatchComponent );
      dispatchComponent.dispatchEvent( e2 );
      return true;
    }

    public void mousePressed( final MouseEvent e ) {
      super.mousePressed( e );
      if ( !SwingUtilities.isLeftMouseButton( e ) ) {
        return;
      }

      if ( header.getResizingColumn() == null ) {
        final Point p = e.getPoint();
        final TableColumnModel columnModel = header.getColumnModel();
        final int index = columnModel.getColumnIndexAtX( p.x );
        if ( index != -1 ) {
          if ( header.editCellAt( index, e ) ) {
            setDispatchComponent( e );
            repostEvent( e );
          }
        }
      }
    }

    public void mouseReleased( final MouseEvent e ) {
      super.mouseReleased( e );
      if ( !SwingUtilities.isLeftMouseButton( e ) ) {
        return;
      }
      repostEvent( e );
      dispatchComponent = null;
    }
  }
}
