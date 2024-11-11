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


package org.pentaho.reporting.libraries.css.model;

import java.io.Serializable;

/**
 * Creation-Date: 26.10.2005, 14:05:23
 *
 * @author Thomas Morgner
 */
public final class StyleKey implements Serializable, Cloneable {
  public static final int ALWAYS = 0x13FFF;

  public static final int INLINE_ELEMENTS = 0x0001;
  public static final int BLOCK_ELEMENTS = 0x0002;
  public static final int DOM_ELEMENTS = 0x0003;

  public static final int All_ELEMENTS = 0x0FFF;

  public static final int PSEUDO_BEFORE = 0x0010;
  public static final int PSEUDO_AFTER = 0x0020;
  public static final int PSEUDO_ALTERNATE = 0x0040;
  public static final int PSEUDO_MARKER = 0x0080;
  public static final int PSEUDO_LINEMARKER = 0x0100;
  public static final int PSEUDO_FIRST_LETTER = 0x0200;
  public static final int PSEUDO_FIRST_LINE = 0x0400;
  public static final int PSEUDO_OTHER = 0x0800;

  public static final int MARGINS = 0x1000;
  public static final int FOOTNOTE_AREA = 0x02000;

  public static final int PAGE_CONTEXT = 0x10000;
  public static final int COUNTERS = 0x20000;

  /**
   * The index is implicitly defined when the key is registered. Do not rely on that index for long term persitence.
   */
  public final transient int index;

  /**
   * The name of the style key.
   */
  public final String name;

  /**
   * Whether this stylekey is transient. Transient keys denote temporary values stored in the stylesheet. Such keys
   * should never be written into long term persistent states.
   */
  private boolean trans;

  /**
   * Defines, whether the key can be inherited.
   */
  private boolean inherited;

  //  /**
  //   * Defines, whether this key should hold a list of values. The value object
  //   * stored by that key must be an instance of CSSValueList.
  //   */
  //  private boolean listOfValues;

  private int validity;

  /**
   * This constructor is intentionally 'package protected'.
   */
  /**
   * Creates a new style key.
   *
   * @param name the name (never null).
   */
  protected StyleKey( final String name,
                      final boolean trans,
                      final boolean inherited,
                      final int index,
                      final int validity ) {
    if ( name == null ) {
      throw new NullPointerException( "StyleKey.setName(...): null not permitted." );
    }

    this.validity = validity;
    this.name = name;
    this.trans = trans;
    this.inherited = inherited;
    this.index = index;
  }

  /**
   * Returns the name of the key.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  public int getIndex() {
    return index;
  }

  public boolean isValidOn( int mask ) {
    return ( validity & mask ) != 0;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof StyleKey ) ) {
      return false;
    }

    final StyleKey key = (StyleKey) o;

    if ( name.equals( key.name ) == false ) {
      return false;
    }
    return true;
  }

  public boolean isInherited() {
    return inherited;
  }

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those
   * provided by <code>java.util.Hashtable</code>.
   * <p/>
   *
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return index;
  }

  /**
   * Checks, whether this stylekey denotes a temporary computation result.
   *
   * @return true, if the key is transient, false otherwise.
   */
  public boolean isTransient() {
    return trans;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return "StyleKey{" +
      "name='" + name + "'" +
      ", trans=" + trans +
      ", inherited=" + inherited +
      ", validity=" + Integer.toHexString( validity ) +
      "}";
  }

  public Object clone()
    throws CloneNotSupportedException {
    return super.clone();
  }
}
