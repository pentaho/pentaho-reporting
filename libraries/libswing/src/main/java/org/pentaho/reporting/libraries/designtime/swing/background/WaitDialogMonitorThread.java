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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class which will close the wait dialog once the worker thread has completed processing (or exited early due to a
 * cancel being issued).
 */
public class WaitDialogMonitorThread extends Thread {
  private static final Log log = LogFactory.getLog( WaitDialogMonitorThread.class );
  private final WaitDialog waitDialog;
  private ProgressFeed progressFeed;
  private final Thread workerThread;
  private double progress;

  /**
   * Initializes this monitor threead
   *
   * @param workerThread the worker thread being executed
   * @param waitDialog   the dialog that should be closed when the thread exits
   */
  public WaitDialogMonitorThread( final Thread workerThread,
                                  final WaitDialog waitDialog,
                                  final ProgressFeed progressFeed ) {
    this.workerThread = workerThread;
    this.waitDialog = waitDialog;
    this.progressFeed = progressFeed;
    this.progress = -1;
  }

  /**
   * Starts the worker thread and waits for it to complete. Then it closes the Wait dialog
   */
  public void run() {
    try {
      log.debug( "Waiting for worker thread to complete" );
      while ( workerThread.isAlive() ) {
        if ( progressFeed != null ) {
          final double p = progressFeed.queryProgress();
          if ( p != progress ) {
            waitDialog.updateProgress( p );
            progress = p;
          }
        }
        workerThread.join( 500 );
      }
    } catch ( Throwable t ) {
      log.warn( "The worker thread threw an exception: [" + t.getMessage() + "]" );
    } finally {
      log.debug( "The worker thread has finished - telling the dialog to close" );
      waitDialog.exit();
    }
  }
}
