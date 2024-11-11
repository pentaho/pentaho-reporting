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
 * Creation-Date: 05.04.2006, 13:03:37
 *
 * @author Thomas Morgner
 */
public class ResourceLoadingException extends ResourceException {
  private static final long serialVersionUID = -940152083042961377L;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public ResourceLoadingException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ResourceLoadingException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public ResourceLoadingException( final String message ) {
    super( message );
  }
}
