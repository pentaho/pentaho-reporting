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


package org.pentaho.reporting.libraries.fonts.encoding;

import java.io.IOException;

/**
 * Creation-Date: 19.04.2006, 22:03:51
 *
 * @author Thomas Morgner
 */
public class EncodingException extends IOException {
  /**
   * Constructs an <code>IOException</code> with <code>null</code> as its error detail message.
   */
  public EncodingException() {
  }

  /**
   * Constructs an <code>IOException</code> with the specified detail message. The error message string <code>s</code>
   * can later be retrieved by the <code>{@link Throwable#getMessage}</code> method of class
   * <code>java.lang.Throwable</code>.
   *
   * @param s the detail message.
   */
  public EncodingException( final String s ) {
    super( s );
  }
}
