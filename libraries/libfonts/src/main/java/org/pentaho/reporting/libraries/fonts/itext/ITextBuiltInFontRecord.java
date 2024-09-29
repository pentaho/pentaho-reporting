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


package org.pentaho.reporting.libraries.fonts.itext;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 22.07.2007, 18:00:32
 *
 * @author Thomas Morgner
 */
public class ITextBuiltInFontRecord implements FontRecord, FontIdentifier {
  private FontFamily family;
  private String fullName;
  private boolean bold;
  private boolean italics;
  private boolean oblique;

  public ITextBuiltInFontRecord( final FontFamily family, final String fullName,
                                 final boolean bold, final boolean italics, final boolean oblique ) {
    if ( family == null ) {
      throw new NullPointerException();
    }
    if ( fullName == null ) {
      throw new NullPointerException();
    }
    this.family = family;
    this.fullName = fullName;
    this.bold = bold;
    this.italics = italics;
    this.oblique = oblique;
  }

  public FontFamily getFamily() {
    return family;
  }

  public String getFullName() {
    return fullName;
  }

  public boolean isBold() {
    return bold;
  }

  public boolean isItalic() {
    return italics;
  }

  public boolean isOblique() {
    return oblique;
  }

  public FontIdentifier getIdentifier() {
    return this;
  }

  public boolean isScalable() {
    return true;
  }

  public FontType getFontType() {
    return FontType.OTHER;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ITextBuiltInFontRecord that = (ITextBuiltInFontRecord) o;

    if ( bold != that.bold ) {
      return false;
    }
    if ( italics != that.italics ) {
      return false;
    }
    if ( oblique != that.oblique ) {
      return false;
    }
    if ( !family.equals( that.family ) ) {
      return false;
    }
    if ( !fullName.equals( that.fullName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = family.hashCode();
    result = 29 * result + fullName.hashCode();
    result = 29 * result + ( bold ? 1 : 0 );
    result = 29 * result + ( italics ? 1 : 0 );
    result = 29 * result + ( oblique ? 1 : 0 );
    return result;
  }
}
