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
 * This exception is thrown whenever a resource-manager tries to load data using an invalid key.
 *
 * @author Thomas Morgner
 */
public class UnrecognizedLoaderException extends ResourceLoadingException {
  private static final long serialVersionUID = 6319955849184970434L;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public UnrecognizedLoaderException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public UnrecognizedLoaderException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public UnrecognizedLoaderException( final String message ) {
    super( message );
  }
}
