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


package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * This function enables a "PageFooter only on last page" functionality.
 *
 * @author Thomas Morgner
 */
public class TriggerPageFooterFunction extends AbstractFunction implements LayoutProcessorFunction {
  /**
   * Creates a new TriggerPageFooterFunction with no name. You have to define one using "setName" or the function will
   * not work.
   */
  public TriggerPageFooterFunction() {
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    event.getReport().getPageFooter().setVisible( false );
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event
   *          The event.
   */
  public void reportDone( final ReportEvent event ) {
    event.getReport().getPageFooter().setVisible( true );
  }

  /**
   * This method returns nothing.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return null;
  }
}
