/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.gui.print;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * An export task implementation that prints a report using the AWT printing API.
 *
 * @author Thomas Morgner
 */
public class PrintExportTask implements Runnable {
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

  private static final Log logger = LogFactory.getLog( PrintExportTask.class );
  private Messages messages;
  private MasterReport job;
  private ReportProgressDialog progressListener;
  private StatusListener statusListener;

  public PrintExportTask( final MasterReport job, final ReportProgressDialog progressListener,
      final SwingGuiContext swingGuiContext ) {
    this.job = job;
    this.progressListener = progressListener;
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), PrintingPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( PrintingPlugin.class ) );
    }
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
      PrintUtil.print( job, new OnDemandShowProgressListener( progressListener ) );
    } catch ( Exception e ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "PrintExportTask.USER_EXPORT_FAILED" ), e ); //$NON-NLS-1$
      }
      PrintExportTask.logger.error( "Printing Failed", e ); //$NON-NLS-1$
    } finally {
      if ( progressListener != null ) {
        progressListener.setVisible( false );
      }
    }
  }

}
