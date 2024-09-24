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

package org.pentaho.reporting.libraries.formula.lvalues;

import org.pentaho.reporting.libraries.formula.typing.Type;

import java.io.Serializable;

/**
 * Creation-Date: 02.11.2006, 10:02:54
 *
 * @author Thomas Morgner
 */
public class TypeValuePair implements Serializable {
  private Type type;
  private Object value;
  private static final long serialVersionUID = 6531903280852042078L;

  public TypeValuePair( final Type type, final Object value ) {
    if ( type == null ) {
      throw new NullPointerException( "Type must be given." );
    }
    this.type = type;
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }


  public String toString() {
    return "TypeValuePair{" +
      "type=" + type +
      ", value=" + value +
      '}';
  }
}
