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
