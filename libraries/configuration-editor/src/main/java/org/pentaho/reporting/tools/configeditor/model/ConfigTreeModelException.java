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


package org.pentaho.reporting.tools.configeditor.model;

/**
 * The ConfigTreeModelException is thrown whenever an error occurred during an tree model operation.
 *
 * @author Thomas Morgner
 */
public class ConfigTreeModelException extends Exception {
  /**
   * Creates a ConfigTreeModelException with no message and no parent.
   */
  public ConfigTreeModelException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ConfigTreeModelException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public ConfigTreeModelException( final String message ) {
    super( message );
  }
}
