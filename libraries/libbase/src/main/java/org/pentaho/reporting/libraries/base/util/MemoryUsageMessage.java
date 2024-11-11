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


package org.pentaho.reporting.libraries.base.util;

/**
 * A helper class to print memory usage message if needed.
 *
 * @author Thomas Morgner
 */
public class MemoryUsageMessage {

  /**
   * The message.
   */
  private final String message;

  /**
   * Creates a new message.
   *
   * @param message the message.
   */
  public MemoryUsageMessage( final String message ) {
    this.message = message;
  }

  /**
   * Returns a string representation of the message (useful for debugging).
   *
   * @return the string.
   */
  public String toString() {
    return this.message + "Free: " + Runtime.getRuntime().freeMemory() + "; "
      + "Total: " + Runtime.getRuntime().totalMemory();
  }

}
