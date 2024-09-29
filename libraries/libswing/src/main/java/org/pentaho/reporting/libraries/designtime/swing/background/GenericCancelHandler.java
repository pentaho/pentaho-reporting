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

public class GenericCancelHandler implements CancelListener {
  private Thread thread;
  private boolean cancelled;

  public GenericCancelHandler( final Thread thread ) {
    this.thread = thread;
  }

  /**
   * Requests that the thread stop processing as soon as possible.
   */
  public void cancelProcessing( final CancelEvent event ) {
    thread.interrupt();
    this.cancelled = true;
  }

  public boolean isCancelled() {
    return cancelled;
  }
}
