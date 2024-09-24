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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * An ItemSum function, that is reset to zero on every new page.
 *
 * @author Thomas Morgner
 */
public class PageItemSumFunction extends ItemSumFunction implements PageEventListener {
  /**
   * Default Constructor.
   */
  public PageItemSumFunction() {
  }

  /**
   * Handles the pageStartedEvent.
   *
   * @param event
   *          the report event.
   */
  public void pageStarted( final ReportEvent event ) {
    clear();
  }

  /**
   * Handles the pageFinishedEvent. This method is emtpy and only here as implementation side effect.
   *
   * @param event
   *          the report event.
   */
  public void pageFinished( final ReportEvent event ) {
  }
}
