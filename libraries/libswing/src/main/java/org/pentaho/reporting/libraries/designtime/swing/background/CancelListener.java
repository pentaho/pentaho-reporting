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


package org.pentaho.reporting.libraries.designtime.swing.background;

import java.util.EventListener;

/**
 * Interface which allows a controlling thread to cancel the processing of another thread in a controlled manner.
 */
public interface CancelListener extends EventListener {
  /**
   * Requests that the thread stop processing as soon as possible.
   */
  public void cancelProcessing( final CancelEvent event );
}
