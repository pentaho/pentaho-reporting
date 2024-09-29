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


package org.pentaho.reporting.engine.classic.core.style;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Creation-Date: 30.10.2005, 19:37:35
 *
 * @author Thomas Morgner
 */
public class TextWrap implements Serializable {
  public static final TextWrap NONE = new TextWrap( "none" );
  public static final TextWrap WRAP = new TextWrap( "wrap" );
  private String type;

  private TextWrap( final String type ) {
    this.type = type;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final TextWrap that = (TextWrap) o;

    if ( type != null ? !type.equals( that.type ) : that.type != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return ( type != null ? type.hashCode() : 0 );
  }

  public String toString() {
    return type;
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws java.io.ObjectStreamException
   *           if the element could not be resolved.
   * @noinspection UNUSED_SYMBOL
   */
  protected Object readResolve() throws ObjectStreamException {
    if ( this.type.equals( TextWrap.NONE.type ) ) {
      return TextWrap.NONE;
    }
    if ( this.type.equals( TextWrap.WRAP.type ) ) {
      return TextWrap.WRAP;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

}
