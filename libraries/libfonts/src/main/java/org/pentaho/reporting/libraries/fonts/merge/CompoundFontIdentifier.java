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

package org.pentaho.reporting.libraries.fonts.merge;

import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 20.07.2007, 19:31:00
 *
 * @author Thomas Morgner
 */
public class CompoundFontIdentifier implements FontIdentifier {
  private FontIdentifier identifier;
  private FontRegistry registry;
  private boolean boldSpecified;
  private boolean italicsSpecified;

  public CompoundFontIdentifier( final FontIdentifier identifier,
                                 final FontRegistry registry,
                                 final boolean boldSpecified,
                                 final boolean italicsSpecified ) {
    this.boldSpecified = boldSpecified;
    this.italicsSpecified = italicsSpecified;
    if ( registry == null ) {
      throw new NullPointerException();
    }
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    this.registry = registry;
    this.identifier = identifier;
  }

  public FontIdentifier getIdentifier() {
    return identifier;
  }

  public boolean isBoldSpecified() {
    return boldSpecified;
  }

  public boolean isItalicsSpecified() {
    return italicsSpecified;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final CompoundFontIdentifier that = (CompoundFontIdentifier) o;

    if ( !identifier.equals( that.identifier ) ) {
      return false;
    }
    if ( !registry.equals( that.registry ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = identifier.hashCode();
    result = 29 * result + registry.hashCode();
    return result;
  }

  public boolean isScalable() {
    return identifier.isScalable();
  }

  public FontType getFontType() {
    return identifier.getFontType();
  }

  public FontRegistry getRegistry() {
    return registry;
  }
}
