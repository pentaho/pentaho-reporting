/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.wizard.model;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class GroupType implements Serializable {
  public static final GroupType RELATIONAL = new GroupType( "RELATIONAL" );
  public static final GroupType CT_COLUMN = new GroupType( "CT_COLUMN" );
  public static final GroupType CT_ROW = new GroupType( "CT_ROW" );
  public static final GroupType CT_OTHER = new GroupType( "CT_OTHER" );

  private String type;

  private GroupType( final String type ) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static GroupType[] values() {
    return new GroupType[] { RELATIONAL, CT_COLUMN, CT_ROW, CT_OTHER };
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws ObjectStreamException if the element could not be resolved.
   */
  private Object readResolve()
    throws ObjectStreamException {
    if ( this.type.equals( RELATIONAL.type ) ) {
      return RELATIONAL;
    }
    if ( this.type.equals( CT_COLUMN.type ) ) {
      return CT_COLUMN;
    }
    if ( this.type.equals( CT_ROW.type ) ) {
      return CT_ROW;
    }
    if ( this.type.equals( CT_OTHER.type ) ) {
      return CT_OTHER;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

  public String toString() {
    return "GroupType(" + type + ')';
  }
}
