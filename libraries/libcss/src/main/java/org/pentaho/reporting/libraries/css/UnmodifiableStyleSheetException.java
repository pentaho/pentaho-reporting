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

package org.pentaho.reporting.libraries.css;

/**
 * Creation-Date: 01.12.2005, 20:59:34
 *
 * @author Thomas Morgner
 */
public class UnmodifiableStyleSheetException extends RuntimeException {
  /**
   * Constructs a new runtime exception with <code>null</code> as its detail message.  The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   */
  public UnmodifiableStyleSheetException() {
  }

  /**
   * Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may
   * subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
   *                method.
   */
  public UnmodifiableStyleSheetException( String message ) {
    super( message );
  }
}
