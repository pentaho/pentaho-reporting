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
