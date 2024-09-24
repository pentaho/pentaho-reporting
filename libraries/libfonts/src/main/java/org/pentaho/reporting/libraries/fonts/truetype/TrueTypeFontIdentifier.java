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

package org.pentaho.reporting.libraries.fonts.truetype;

import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

import java.io.Serializable;

/**
 * Creation-Date: 16.12.2005, 19:35:31
 *
 * @author Thomas Morgner
 */
public class TrueTypeFontIdentifier implements FontIdentifier, Serializable, FontNativeContext {
  private String fontSource;
  private String fontName;
  private String fontVariant;
  private int collectionIndex;
  private long offset;
  private boolean italics;
  private boolean bold;

  public TrueTypeFontIdentifier( final String fontSource,
                                 final String fontName,
                                 final String fontVariant,
                                 final int collectionIndex,
                                 final long offset,
                                 final boolean italics,
                                 final boolean bold ) {
    this.italics = italics;
    this.bold = bold;
    if ( fontSource == null ) {
      throw new NullPointerException();
    }
    if ( fontName == null ) {
      throw new NullPointerException();
    }
    if ( fontVariant == null ) {
      throw new NullPointerException();
    }
    this.fontVariant = fontVariant;
    this.fontSource = fontSource;
    this.fontName = fontName;
    this.collectionIndex = collectionIndex;
    this.offset = offset;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final TrueTypeFontIdentifier that = (TrueTypeFontIdentifier) o;

    if ( collectionIndex != that.collectionIndex ) {
      return false;
    }
    if ( offset != that.offset ) {
      return false;
    }
    if ( !fontSource.equals( that.fontSource ) ) {
      return false;
    }
    if ( !fontName.equals( that.fontName ) ) {
      return false;
    }
    return fontVariant.equals( that.fontVariant );

  }

  public int hashCode() {
    int result = fontName.hashCode();
    result = 29 * result + fontSource.hashCode();
    result = 29 * result + fontVariant.hashCode();
    result = 29 * result + collectionIndex;
    result = 29 * result + (int) ( offset ^ ( offset >>> 32 ) );
    return result;
  }

  public String getFontSource() {
    return fontSource;
  }

  public String getFontVariant() {
    return fontVariant;
  }

  public String getFontName() {
    return fontName;
  }

  public int getCollectionIndex() {
    return collectionIndex;
  }

  public long getOffset() {
    return offset;
  }

  /**
   * Defines, whether the font identifier represents a scalable font type. Such fonts usually create one font metric
   * object for each physical font, and apply the font size afterwards.
   *
   * @return true, if the font is scalable, false otherwise
   */
  public boolean isScalable() {
    return true;
  }

  public FontType getFontType() {
    return FontType.OPENTYPE;
  }

  public boolean isNativeBold() {
    return bold;
  }

  public boolean isNativeItalics() {
    return italics;
  }
}
