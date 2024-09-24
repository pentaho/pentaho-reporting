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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
