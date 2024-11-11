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


package org.pentaho.reporting.engine.classic.core.util;

import java.io.ObjectStreamException;

/**
 * The <code>ObjectStreamResolveException</code> this thrown, when the object resolving operation for serialized objects
 * failed.
 *
 * @author Thomas Morgner
 */
public class ObjectStreamResolveException extends ObjectStreamException {
  /**
   * Create an ObjectStreamException with the specified argument.
   *
   * @param classname
   *          the detailed message for the exception
   */
  public ObjectStreamResolveException( final String classname ) {
    super( classname );
  }

  /**
   * Create an ObjectStreamException.
   */
  public ObjectStreamResolveException() {
  }
}
