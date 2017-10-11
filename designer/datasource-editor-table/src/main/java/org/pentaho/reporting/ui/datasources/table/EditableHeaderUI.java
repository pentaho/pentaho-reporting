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
