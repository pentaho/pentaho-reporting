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

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

/**
 * Creation-Date: 04.05.2007, 19:23:43
 *
 * @author Thomas Morgner
 */
public class URLRewriteException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public URLRewriteException() {
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public URLRewriteException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public URLRewriteException( final String message ) {
    super( message );
  }
}
