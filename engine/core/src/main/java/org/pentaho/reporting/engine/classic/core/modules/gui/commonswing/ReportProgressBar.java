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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;

public class ReportProgressBar extends JProgressBar implements ReportProgressListener {
  private class ScreenUpdateRunnable implements Runnable {
    /**
     * This is the event upon which we will update the report progress information
     */
    private ReportProgressEvent reportProgressEvent;

    /**
     * Constructor for the screen updatable thread
     */
    protected ScreenUpdateRunnable() {
    }

    public ReportProgressEvent getReportProgressEvent() {
      return reportProgressEvent;
    }

    public void setReportProgressEvent( final ReportProgressEvent reportProgressEvent ) {
      this.reportProgressEvent = reportProgressEvent;
    }

    /**
     * Performs the process of actually updaing the UI
     */
    public synchronized void run() {
      if ( reportProgressEvent == null ) {
        return;
      }

      setValue( (int) ReportProgressEvent.computePercentageComplete( reportProgressEvent, isOnlyPagination() ) );
      reportProgressEvent = null;
    }

    public boolean update( final ReportProgressEvent event ) {
      final boolean retval = ( reportProgressEvent == null );
      this.reportProgressEvent = event;
      return retval;
    }
  }

  /**
   * Indicates if this process is only for pagination
   */
  private boolean onlyPagination;
  private final ScreenUpdateRunnable runnable;

  /**
   * Creates a horizontal progress bar that displays a border but no progress string. The initial and minimum values are
   * 0, and the maximum is 100.
   *
   * @see #setOrientation
   * @see #setBorderPainted
   * @see #setStringPainted
   * @see #setString
   * @see #setIndeterminate
   */
  public ReportProgressBar() {
    super( SwingConstants.HORIZONTAL, 0, 100 );
    this.runnable = new ScreenUpdateRunnable();
  }

  public boolean isOnlyPagination() {
    return onlyPagination;
  }

  public void setOnlyPagination( final boolean onlyPagination ) {
    this.onlyPagination = onlyPagination;
  }

  public void reportProcessingStarted( final ReportProgressEvent event ) {
    synchronized ( runnable ) {
      if ( runnable.update( event ) ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
          runnable.run();
        } else {
          SwingUtilities.invokeLater( runnable );
        }
      }
    }
  }

  public void reportProcessingUpdate( final ReportProgressEvent event ) {
    synchronized ( runnable ) {
      if ( runnable.update( event ) ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
          runnable.run();
        } else {
          SwingUtilities.invokeLater( runnable );
        }
      }
    }
  }

  public void reportProcessingFinished( final ReportProgressEvent event ) {
    synchronized ( runnable ) {
      if ( runnable.update( event ) ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
          runnable.run();
        } else {
          SwingUtilities.invokeLater( runnable );
        }
      }
    }
  }
}
