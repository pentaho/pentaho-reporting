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
 * Creation-Date: 05.04.2006, 13:02:23
 *
 * @author Thomas Morgner
 */
public class ResourceKeyCreationException extends ResourceException {
  private static final long serialVersionUID = -8411385657974722306L;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public ResourceKeyCreationException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ResourceKeyCreationException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public ResourceKeyCreationException( final String message ) {
    super( message );
  }
}
