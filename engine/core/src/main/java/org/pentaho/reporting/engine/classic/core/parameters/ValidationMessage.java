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


package org.pentaho.reporting.engine.classic.core.parameters;

import java.io.Serializable;

public class ValidationMessage implements Serializable {
  private static final Object[] EMPTY_OBJECT = new Object[0];

  private String message;
  private Object[] parameters;

  public ValidationMessage( final String message ) {
    this( message, ValidationMessage.EMPTY_OBJECT );
  }

  public ValidationMessage( final String message, final Object arg ) {
    this( message, new Object[] { arg } );
  }

  public ValidationMessage( final String message, final Object arg, final Object arg1 ) {
    this( message, new Object[] { arg, arg1 } );
  }

  public ValidationMessage( final String message, final Object arg, final Object arg1, final Object arg2 ) {
    this( message, new Object[] { arg, arg1, arg2 } );
  }

  public ValidationMessage( final String message, final Object[] parameters ) {
    if ( message == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    this.message = message;
    this.parameters = (Object[]) parameters.clone();
  }

  public String getMessage() {
    return message;
  }

  public Object[] getParameters() {
    return (Object[]) parameters.clone();
  }
}
