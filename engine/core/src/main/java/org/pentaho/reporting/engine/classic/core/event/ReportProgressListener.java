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

package org.pentaho.reporting.engine.classic.core.event;

import java.util.EventListener;

/**
 * A report progress listener receives status events about the report processing status. This is mainly used to display
 * progress dialogs.
 *
 * @author Thomas Morgner
 */
public interface ReportProgressListener extends EventListener {
  /**
   * Receives a notification that the report processing has started.
   *
   * @param event
   *          the start event.
   */
  public void reportProcessingStarted( ReportProgressEvent event );

  /**
   * Receives a notification that the report processing made some progress.
   *
   * @param event
   *          the update event.
   */
  public void reportProcessingUpdate( ReportProgressEvent event );

  /**
   * Receives a notification that the report processing was finished.
   *
   * @param event
   *          the finish event.
   */
  public void reportProcessingFinished( ReportProgressEvent event );
}
