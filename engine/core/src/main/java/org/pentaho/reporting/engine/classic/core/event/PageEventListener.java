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
 * The PageEventListener gets informed of PageEvents.
 * <p/>
 * This is an extracted interface of the original ReportEventListener. As page events are only fired by some (page
 * sensitive) report processors, there is no need to support page events in the ReportEventListener interface.
 * <p/>
 * Functions that should be informed of page events should implement this interface.
 * <p/>
 * Information: The pageCanceled method is called, if a empty page was created and was removed from the report
 * afterwards.
 *
 * @author Thomas Morgner
 */
public interface PageEventListener extends EventListener {

  /**
   * Receives notification that a new page is being started.
   *
   * @param event
   *          The event.
   */
  public void pageStarted( ReportEvent event );

  /**
   * Receives notification that a page is completed.
   *
   * @param event
   *          The event.
   */
  public void pageFinished( ReportEvent event );
}
