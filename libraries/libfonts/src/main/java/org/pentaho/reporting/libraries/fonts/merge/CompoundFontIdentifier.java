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
