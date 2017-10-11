/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.java14print;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;

public class Java14RepaginateAndPrintExportTask implements Runnable {
  private static class OnDemandShowProgressListener implements ReportProgressListener {
    private ReportProgressDialog dialog;

    private OnDemandShowProgressListener( final ReportProgressDialog dialog ) {
      this.dialog = dialog;
    }

    /**
     * Receives a notification that the report processing has started.
     *
     * @param event
     *          the start event.
     */
    public void reportProcessingStarted( final ReportProgressEvent event ) {
      if ( dialog != null ) {
        dialog.setVisibleInEDT( true );
        dialog.reportProcessingStarted( event );
      }
    }

    /**
     * Receives a notification that the report processing made some progress.
     *
     * @param event
     *          the update event.
     */
    public void reportProcessingUpdate( final ReportProgressEvent event ) {
      if ( dialog != null ) {
        dialog.reportProcessingUpdate( event );
      }
    }

    /**
     * Receives a notification that the report processing was finished.
     *
     * @param event
     *          the finish event.
     */
    public void reportProcessingFinished( final ReportProgressEvent event ) {
      if ( dialog != null ) {
        dialog.reportProcessingFinished( event );
        dialog.setVisibleInEDT( false );
      }
    }
  }

  private static final Log logger = LogFactory.getLog( Java14RepaginateAndPrintExportTask.class );

  private MasterReport job;
  private ReportProgressDialog progressListener;
  private StatusListener statusListener;

  public Java14RepaginateAndPrintExportTask( final MasterReport job, final ReportProgressDialog progressListener,
      final StatusListener statusListener ) {
    this.job = job;
    this.progressListener = progressListener;
    this.statusListener = statusListener;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    try {
      Java14PrintUtil.print( job, new OnDemandShowProgressListener( progressListener ) );
    } catch ( Exception e ) {
      logger.warn( "Failed to print the report", e );
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, "Export failed", e );
      }
    } finally {
      if ( progressListener != null ) {
        progressListener.setVisible( false );
      }
    }
  }

}
