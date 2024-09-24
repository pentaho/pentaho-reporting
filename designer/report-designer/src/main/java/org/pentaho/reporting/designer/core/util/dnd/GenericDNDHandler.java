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

package org.pentaho.reporting.designer.core.util.dnd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public class GenericDNDHandler implements DropTargetListener {
  private static final Log logger = LogFactory.getLog( GenericDNDHandler.class );

  private Point position;
  private Object transferData;
  private DataFlavor flavor;
  private DataFlavor[] acceptedFlavors;

  public GenericDNDHandler( final DataFlavor[] acceptedFlavors ) {
    if ( acceptedFlavors == null ) {
      throw new NullPointerException();
    }
    this.acceptedFlavors = acceptedFlavors.clone();
  }

  /**
   * Called while a drag operation is ongoing, when the mouse pointer enters the operable part of the drop site for the
   * <code>DropTarget</code> registered with this listener.
   *
   * @param dtde the <code>DropTargetDragEvent</code>
   */

  public void dragEnter( final DropTargetDragEvent dtde ) {
    dragOver( dtde );
  }

  /**
   * Called when a drag operation is ongoing, while the mouse pointer is still over the operable part of the drop site
   * for the <code>DropTarget</code> registered with this listener.
   *
   * @param dtde the <code>DropTargetDragEvent</code>
   */

  public void dragOver( final DropTargetDragEvent dtde ) {
    final Transferable transferable = dtde.getTransferable();

    for ( int i = 0; i < acceptedFlavors.length; i++ ) {
      final DataFlavor acceptedFlavor = acceptedFlavors[ i ];
      if ( transferable.isDataFlavorSupported( acceptedFlavor ) ) {
        // a transfer from the palette.
        try {
          transferData = transferable.getTransferData( acceptedFlavor );
          position = dtde.getLocation();
          flavor = acceptedFlavor;
          final int result = updateDragOver( dtde );
          if ( result > 0 ) {
            dtde.acceptDrag( DnDConstants.ACTION_COPY );
          } else {
            transferData = null;
            position = null;
            flavor = null;
            dtde.rejectDrag();
          }
          break;
        } catch ( Exception e ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "ReportPanel.dragOver ", e ); // NON-NLS
          }
          transferData = null;
          position = null;
          flavor = null;
          dtde.rejectDrag();
        }
      }
    }
  }

  public Point getPosition() {
    return position;
  }

  public Object getTransferData() {
    return transferData;
  }

  public DataFlavor getFlavor() {
    return flavor;
  }

  protected int updateDragOver( final DropTargetDragEvent event ) {
    return DnDConstants.ACTION_COPY;
  }

  /**
   * Called if the user has modified the current drop gesture.
   * <p/>
   *
   * @param dtde the <code>DropTargetDragEvent</code>
   */

  public void dropActionChanged( final DropTargetDragEvent dtde ) {

  }

  /**
   * Called while a drag operation is ongoing, when the mouse pointer has exited the operable part of the drop site for
   * the <code>DropTarget</code> registered with this listener.
   *
   * @param dte the <code>DropTargetEvent</code>
   */

  public void dragExit( final DropTargetEvent dte ) {
    transferData = null;
    position = null;
    flavor = null;
  }

  /**
   * Called when the drag operation has terminated with a drop on the operable part of the drop site for the
   * <code>DropTarget</code> registered with this listener.
   * <p/>
   * This method is responsible for undertaking the transfer of the data associated with the gesture. The
   * <code>DropTargetDropEvent</code> provides a means to obtain a <code>Transferable</code> object that represents the
   * data object(s) to be transfered.<P> From this method, the <code>DropTargetListener</code> shall accept or reject
   * the drop via the acceptDrop(int dropAction) or rejectDrop() methods of the <code>DropTargetDropEvent</code>
   * parameter.
   * <p/>
   * Subsequent to acceptDrop(), but not before, <code>DropTargetDropEvent</code>'s getTransferable() method may be
   * invoked, and data transfer may be performed via the returned <code>Transferable</code>'s getTransferData() method.
   * <p/>
   * At the completion of a drop, an implementation of this method is required to signal the success/failure of the drop
   * by passing an appropriate <code>boolean</code> to the <code>DropTargetDropEvent</code>'s dropComplete(boolean
   * success) method.
   * <p/>
   * Note: The data transfer should be completed before the call  to the <code>DropTargetDropEvent</code>'s
   * dropComplete(boolean success) method. After that, a call to the getTransferData() method of the
   * <code>Transferable</code> returned by <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to succeed
   * only if the data transfer is local; that is, only if <code>DropTargetDropEvent.isLocalTransfer()</code> returns
   * <code>true</code>. Otherwise, the behavior of the call is implementation-dependent.
   * <p/>
   *
   * @param dtde the <code>DropTargetDropEvent</code>
   */

  public void drop( final DropTargetDropEvent dtde ) {
    dtde.rejectDrop();
    transferData = null;
    position = null;
    flavor = null;
  }

  public void cleanup() {
    transferData = null;
    position = null;
    flavor = null;
  }
}
