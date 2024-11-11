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

/**
 * Creation-Date: 23.11.2005, 11:41:24
 *
 * @author Thomas Morgner
 */
public class CSSNumericType extends CSSType {
  public static final CSSNumericType NUMBER = new CSSNumericType( "", false, false );
  public static final CSSNumericType PERCENTAGE = new CSSNumericType( "%", false, false );
  public static final CSSNumericType EM = new CSSNumericType( "em", true, false );
  public static final CSSNumericType EX = new CSSNumericType( "ex", true, false );
  public static final CSSNumericType PX = new CSSNumericType( "px", true, false );

  public static final CSSNumericType CM = new CSSNumericType( "cm", true, true );
  public static final CSSNumericType MM = new CSSNumericType( "mm", true, true );
  public static final CSSNumericType INCH = new CSSNumericType( "inch", true, true );

  public static final CSSNumericType PT = new CSSNumericType( "pt", true, true );
  public static final CSSNumericType PC = new CSSNumericType( "pc", true, true );

  public static final CSSNumericType DEG = new CSSNumericType( "deg", false, false );

  private boolean absolute;
  private boolean length;

  protected CSSNumericType( String name, final boolean length, final boolean absolute ) {
    super( name );
    this.length = length;
    this.absolute = absolute;
  }

  public boolean isLength() {
    return length;
  }

  public boolean isAbsolute() {
    return absolute;
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    return ( obj instanceof CSSNumericType && super.equals( obj ) );
  }

  public int hashCode() {
    return super.hashCode();
  }
}
