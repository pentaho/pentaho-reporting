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

package org.pentaho.reporting.libraries.designtime.swing.background;

import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import java.awt.*;

/**
 * Helper class used to run database queries in the background and provide the user with a cancellable "wait" dialog.
 */
public class BackgroundCancellableProcessHelper {
  private static class CreateWaitDialogTask implements Runnable {
    private Component parent;
    private boolean allowCancel;
    private boolean showProgress;
    private String message;
    private WaitDialog waitDialog;

    private CreateWaitDialogTask( final Component parent,
                                  final boolean allowCancel,
                                  final boolean showProgress,
                                  final String message ) {
      this.parent = parent;
      this.allowCancel = allowCancel;
      this.showProgress = showProgress;
      this.message = message;
    }

    public WaitDialog getWaitDialog() {
      return waitDialog;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      final Window w = getWindowAncestor( parent );
      if ( w instanceof Frame ) {
        waitDialog = new WaitDialog( (Frame) w );
      } else if ( w instanceof Dialog ) {
        waitDialog = new WaitDialog( (Dialog) w );
      } else {
        waitDialog = new WaitDialog();
      }
      if ( message != null ) {
        waitDialog.setMessage( message );
      }
      waitDialog.setShowCancelButton( allowCancel );
      waitDialog.setShowProgressbar( showProgress );
      waitDialog.reinitComponents();
    }
  }

  private BackgroundCancellableProcessHelper() {
  }

  /**
   * Executes the specified PreparedStatement in a background thread while displaying a "please wait" dialog with a
   * Cancel button. If the JDBC driver running the statement supports cancelling the query, the cancel button will
   * cancel the query.
   *
   * @param workerThread   the thread to run and monitor
   * @param cancelListener a optional listener to cancel the process
   * @param parent         the parent frame to which the dialog will attach
   * @deprecated Always give a message
   */
  public static void executeProcessWithCancelDialog( final Thread workerThread,
                                                     final CancelListener cancelListener,
                                                     final Component parent ) {
    executeProcessWithCancelDialog( workerThread, cancelListener, parent, null, null );
  }


  public static void executeProcessWithCancelDialog( final Thread workerThread,
                                                     final CancelListener cancelListener,
                                                     final Component parent,
                                                     final String message ) {
    executeProcessWithCancelDialog( workerThread, cancelListener, parent, message, null );
  }

  public static void executeProcessWithCancelDialog( final Thread workerThread,
                                                     final CancelListener cancelListener,
                                                     final Component parent,
                                                     String message,
                                                     final ProgressFeed progressFeed ) {
    // Validate parameters
    if ( workerThread == null ) {
      throw new IllegalArgumentException();
    }

    if ( message == null ) {
      message = Messages.getInstance().getString( "BackgroundCancellableProcessHelper.DefaultMessage" );
    }
    // Start the worker thread to get the process moving early
    workerThread.start();

    // Create the cancel dialog
    final CreateWaitDialogTask dialogTask = new CreateWaitDialogTask
      ( parent, cancelListener != null, progressFeed != null, message );
    final WaitDialog waitDialog;
    if ( SwingUtilities.isEventDispatchThread() ) {
      dialogTask.run();
      waitDialog = dialogTask.getWaitDialog();
    } else {
      try {
        SwingUtilities.invokeAndWait( dialogTask );
      } catch ( final Exception e ) {
        // ignore exception
        dialogTask.run();
      }
      waitDialog = dialogTask.getWaitDialog();
    }
    if ( cancelListener != null ) {
      waitDialog.addCancelListener( cancelListener );
    }

    // Create the monitor thread
    if ( workerThread.isAlive() ) {
      final WaitDialogMonitorThread monitorThread =
        new WaitDialogMonitorThread( workerThread, waitDialog, progressFeed );

      // Start up the threads
      monitorThread.start();
      waitDialog.setVisible( true ); // blocks until dialog closes
    }
  }

  protected static Window getWindowAncestor( Component component ) {
    while ( component instanceof Window == false ) {
      if ( component == null ) {
        return null;
      }
      component = component.getParent();
    }
    return (Window) component;
  }

}
