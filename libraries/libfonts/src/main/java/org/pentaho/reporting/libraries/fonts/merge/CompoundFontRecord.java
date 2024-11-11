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


package org.pentaho.reporting.libraries.fonts.merge;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;

/**
 * Creation-Date: 20.07.2007, 18:55:08
 *
 * @author Thomas Morgner
 */
public class CompoundFontRecord implements FontRecord {
  /*
   * Specifiying the boldSpecified and italicsSpecified is a dirty hack and should be removed pretty soon.  
   */

  private FontRecord base;
  private CompoundFontFamily family;
  private boolean boldSpecified;
  private boolean italicsSpecified;
  private FontIdentifier identifier;


  public CompoundFontRecord( final FontRecord base,
                             final CompoundFontFamily family,
                             final boolean boldSpecified,
                             final boolean italicsSpecified ) {
    this.base = base;
    this.family = family;
    this.boldSpecified = boldSpecified;
    this.italicsSpecified = italicsSpecified;
  }

  public FontRecord getBase() {
    return base;
  }

  public FontFamily getFamily() {
    return family;
  }

  public boolean isBold() {
    return base.isBold();
  }

  public boolean isItalic() {
    return base.isItalic();
  }

  public boolean isOblique() {
    return base.isOblique();
  }

  public FontIdentifier getIdentifier() {
    if ( identifier == null ) {
      identifier = new CompoundFontIdentifier
        ( base.getIdentifier(), family.getRegistry(), boldSpecified, italicsSpecified );
    }
    return identifier;
  }
}
