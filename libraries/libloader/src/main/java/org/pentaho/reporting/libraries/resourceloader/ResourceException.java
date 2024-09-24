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

package org.pentaho.reporting.libraries.resourceloader;

/**
 * Creation-Date: 05.04.2006, 13:03:53
 *
 * @author Thomas Morgner
 */
public class ResourceException extends Exception {
  private static final long serialVersionUID = 9017929290846143507L;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public ResourceException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ResourceException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public ResourceException( final String message ) {
    super( message );
  }
}
