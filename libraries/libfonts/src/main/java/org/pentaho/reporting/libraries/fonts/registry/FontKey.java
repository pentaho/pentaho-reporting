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
