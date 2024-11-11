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


package org.pentaho.reporting.libraries.base.boot;

/**
 * This exception is thrown when the module initialization encountered an unrecoverable error which prevents the module
 * from being used.
 *
 * @author Thomas Morgner
 */
public class ModuleInitializeException extends Exception {
  /**
   * A serialization related constant.
   */
  private static final long serialVersionUID = -8742325619631583144L;

  /**
   * Creates a ModuleInitializeException with no message and no base exception.
   */
  public ModuleInitializeException() {
    // nothing required
  }

  /**
   * Creates a ModuleInitializeException with the given message and base exception.
   *
   * @param s the message
   * @param e the root exception
   */
  public ModuleInitializeException( final String s, final Throwable e ) {
    super( s, e );
  }

  /**
   * Creates a ModuleInitializeException with the given message and no base exception.
   *
   * @param s the exception message
   */
  public ModuleInitializeException( final String s ) {
    super( s );
  }

}
