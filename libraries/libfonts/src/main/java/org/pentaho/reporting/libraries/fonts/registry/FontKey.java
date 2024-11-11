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


package org.pentaho.reporting.libraries.fonts.registry;

import java.io.Serializable;

/**
 * Creation-Date: 05.08.2007, 19:22:44
 *
 * @author Thomas Morgner
 */
public class FontKey implements Serializable, Cloneable {
  private FontIdentifier identifier;
  private boolean aliased;
  private boolean fractional;
  private double fontSize;
  private volatile int hashKey;
  private volatile boolean hashValid;

  public FontKey( final FontIdentifier identifier,
                  final boolean aliased,
                  final boolean fractional, final double fontSize ) {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    this.identifier = identifier;
    this.aliased = aliased;
    this.fractional = fractional;
    this.fontSize = fontSize;
  }

  public FontKey( final FontKey key ) {
    this.identifier = key.identifier;
    this.aliased = key.aliased;
    this.fractional = key.fractional;
    this.fontSize = key.fontSize;
    this.hashKey = key.hashKey;
  }

  public FontKey() {
  }

  public FontIdentifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier( final FontIdentifier identifier ) {
    this.identifier = identifier;
    this.hashValid = false;
  }

  public boolean isAliased() {
    return aliased;
  }

  public void setAliased( final boolean aliased ) {
    this.aliased = aliased;
    this.hashValid = false;
  }

  public boolean isFractional() {
    return fractional;
  }

  public void setFractional( final boolean fractional ) {
    this.fractional = fractional;
    this.hashValid = false;
  }

  public double getFontSize() {
    return fontSize;
  }

  public void setFontSize( final double fontSize ) {
    this.fontSize = fontSize;
    this.hashValid = false;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FontKey fontKey = (FontKey) o;

    if ( aliased != fontKey.aliased ) {
      return false;
    }
    if ( fontKey.fontSize != fontSize ) {
      return false;
    }
    if ( fractional != fontKey.fractional ) {
      return false;
    }
    if ( !identifier.equals( fontKey.identifier ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    if ( hashValid == false ) {
      int result = identifier.hashCode();
      result = 29 * result + ( aliased ? 1 : 0 );
      result = 29 * result + ( fractional ? 1 : 0 );
      final long temp = fontSize == +0.0d ? 0L : Double.doubleToLongBits( fontSize );
      result = 29 * result + (int) ( temp ^ ( temp >>> 32 ) );
      hashKey = result;
      hashValid = true;
    }
    return hashKey;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
