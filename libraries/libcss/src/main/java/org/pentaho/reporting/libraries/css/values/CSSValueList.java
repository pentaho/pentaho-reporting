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


package org.pentaho.reporting.libraries.css.values;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Collection;


/**
 * Creation-Date: 23.11.2005, 12:37:21
 *
 * @author Thomas Morgner
 */
public final class CSSValueList implements CSSValue, Cloneable {
  private CSSValue[] values;

  public CSSValueList( Collection collection ) {
    CSSValue[] values = new CSSValue[ collection.size() ];
    this.values = (CSSValue[]) collection.toArray( values );
  }

  public CSSValueList( CSSValue[] values ) {
    if ( values == null ) {
      throw new NullPointerException();
    }
    this.values = values;
  }

  public int getLength() {
    return values.length;
  }

  public CSSValue getItem( int index ) {
    return values[ index ];
  }

  public String getCSSText() {
    StringBuffer b = new StringBuffer();
    for ( int i = 0; i < values.length; i++ ) {
      CSSValue value = values[ i ];
      if ( i > 0 ) {
        b.append( " " );
      }
      b.append( value );
    }
    return b.toString();
  }

  public String toString() {
    return getCSSText();
  }

  public boolean contains( CSSValue value ) {
    for ( int i = 0; i < values.length; i++ ) {
      CSSValue cssValue = values[ i ];
      if ( ObjectUtilities.equal( cssValue, value ) ) {
        return true;
      }
    }
    return false;
  }

  public static CSSValueList createList( CSSValue value ) {
    return new CSSValueList( new CSSValue[] { value } );
  }

  public static CSSValueList createDuoList( CSSValue value ) {
    return CSSValueList.createDuoList( value, value );
  }

  public static CSSValueList createDuoList( CSSValue first, CSSValue second ) {
    final CSSValue[] values = new CSSValue[ 2 ];
    values[ 0 ] = first;
    values[ 1 ] = second;
    return new CSSValueList( values );
  }

  public static CSSValueList createQuadList( CSSValue value ) {
    return CSSValueList.createQuadList( value, value );
  }

  public static CSSValueList createQuadList( CSSValue first, CSSValue second ) {
    return CSSValueList.createQuadList( first, second, first, second );
  }

  public static CSSValueList createQuadList( CSSValue first, CSSValue second,
                                             CSSValue third, CSSValue fourth ) {
    final CSSValue[] values = new CSSValue[ 4 ];
    values[ 0 ] = first;
    values[ 1 ] = second;
    values[ 2 ] = third;
    values[ 3 ] = fourth;
    return new CSSValueList( values );
  }

  public static CSSValueList insertFirst( final CSSValueList list,
                                          final CSSValue value ) {
    final int length = list.values.length;
    final CSSValue[] newValues = new CSSValue[ length + 1 ];
    newValues[ 0 ] = value;
    System.arraycopy( list.values, 0, newValues, 1, length );
    return new CSSValueList( newValues );
  }

  public static CSSValueList insertLast( final CSSValueList list,
                                         final CSSValue value ) {
    final int length = list.values.length;
    final CSSValue[] newValues = new CSSValue[ length + 1 ];
    System.arraycopy( list.values, 0, newValues, 0, length );
    newValues[ length ] = value;
    return new CSSValueList( newValues );
  }

  public Object clone() {
    try {
      final CSSValueList o = (CSSValueList) super.clone();
      o.values = (CSSValue[]) values.clone();
      return o;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalAccessError( "Clone cannot be unsupported." );
    }
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSValueList ) {
      CSSValueList that = (CSSValueList) obj;
      return ObjectUtilities.equalArray( this.values, that.values );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
