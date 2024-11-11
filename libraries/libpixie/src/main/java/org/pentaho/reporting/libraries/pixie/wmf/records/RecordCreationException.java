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


package org.pentaho.reporting.libraries.pixie.wmf.records;

public class RecordCreationException extends Exception {
  private static final long serialVersionUID = -7596557720287315169L;

  /**
   * Constructs an <code>Exception</code> with no specified detail message.
   */
  public RecordCreationException() {
  }

  /**
   * Constructs an <code>Exception</code> with the specified detail message.
   *
   * @param s the detail message.
   */
  public RecordCreationException( final String s ) {
    super( s );
  }
}
