/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
