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

package org.pentaho.reporting.libraries.fonts.truetype;

import org.pentaho.reporting.libraries.fonts.FontException;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontSource;

import java.io.IOException;

/**
 * A true-type font record. The record contains meta-information about the font, which allows the system to lookup the
 * font by one of its names and other style attributes.
 * <p/>
 * A font without a 'name' table is rejected. The Name-Table is a mandatory table in the OpenType standard, and only
 * weird MacOS fonts omit that table.
 * <p/>
 * Missing 'head' or 'OS/2' tables are ignored and default values are assumed instead.
 *
 * @author Thomas Morgner
 */
public class TrueTypeFontRecord implements FontSource {
  private int collectionIndex;
  private long offset;
  private boolean bold;
  private boolean italics;
  private boolean oblique;
  private FontFamily family;
  private boolean embeddable;
  private boolean nonWindows; // the font does not have an OS2-Table
  private TrueTypeFontIdentifier identifier;

  //  private FontDataInputSource fontInputSource;
  private String name;
  private String variant;

  public TrueTypeFontRecord( final TrueTypeFont trueTypeFont,
                             final FontFamily family ) throws IOException,
    FontException {
    if ( trueTypeFont == null ) {
      throw new NullPointerException( "The font must not be null" );
    }
    if ( family == null ) {
      throw new NullPointerException( "The font-family must not be null" );
    }
    this.family = family;
    this.collectionIndex = trueTypeFont.getCollectionIndex();
    this.offset = trueTypeFont.getOffset();

    final OS2Table table = (OS2Table) trueTypeFont.getTable( OS2Table.TABLE_ID );
    if ( table != null ) {
      this.embeddable = ( table.isRestricted() == false );
      this.nonWindows = false;
    } else {
      this.nonWindows = true;
    }

    final NameTable nameTable = (NameTable) trueTypeFont.getTable( NameTable.TABLE_ID );
    if ( nameTable == null ) {
      throw new FontException
        ( "This font does not have a 'name' table. It is not valid." );
    }

    name = nameTable.getPrimaryName( NameTable.NAME_FULLNAME );
    //    this.allNames = nameTable.getAllNames(NameTable.NAME_FULLNAME);
    variant = nameTable.getPrimaryName( NameTable.NAME_SUBFAMILY );
    //    this.allVariants = nameTable.getAllNames(NameTable.NAME_SUBFAMILY);

    final FontHeaderTable headTable = (FontHeaderTable)
      trueTypeFont.getTable( FontHeaderTable.TABLE_ID );
    if ( headTable != null ) {
      this.bold = headTable.isBold();
      this.italics = headTable.isItalic();
    } else {
      final OS2Table os2Table = (OS2Table)
        trueTypeFont.getTable( OS2Table.TABLE_ID );
      if ( os2Table != null ) {
        this.bold = os2Table.isBold();
        this.italics = os2Table.isItalic();
      } else {
        // try to use the english name instead. If there is no english name,
        // then do whatever you like. Buggy non standard fonts are not funny ..
        this.bold = ( variant.toLowerCase().indexOf( "bold" ) >= 0 );
        this.italics = ( variant.toLowerCase().indexOf( "italic" ) >= 0 );
      }
    }

    // A font may declare that it is oblique (which is the poor man's italics
    // mode), but a font that supports italics is automaticly oblique as well.
    if ( this.oblique || variant.toLowerCase().indexOf( "oblique" ) >= 0 ) {
      this.oblique = true;
    } else {
      this.oblique = false;
    }

    this.identifier = new TrueTypeFontIdentifier
      ( trueTypeFont.getFilename(), name, variant, collectionIndex, offset, italics, bold );
  }

  public long getOffset() {
    return offset;
  }

  public FontFamily getFamily() {
    return family;
  }

  public boolean isEmbeddable() {
    return embeddable;
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

  public String getFontSource() {
    return identifier.getFontSource();
  }

  public int getCollectionIndex() {
    return collectionIndex;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final TrueTypeFontRecord that = (TrueTypeFontRecord) o;

    if ( bold != that.bold ) {
      return false;
    }
    if ( embeddable != that.embeddable ) {
      return false;
    }
    if ( italics != that.italics ) {
      return false;
    }
    if ( oblique != that.oblique ) {
      return false;
    }
    if ( !name.equals( that.name ) ) {
      return false;
    }
    return variant.equals( that.variant );

  }

  /**
   * This identifies the font resource assigned to this record.
   *
   * @return
   */
  public FontIdentifier getIdentifier() {
    return identifier;
  }

  public boolean isNonWindows() {
    return nonWindows;
  }

  public int hashCode() {
    int result = ( bold ? 1 : 0 );
    result = 29 * result + ( italics ? 1 : 0 );
    result = 29 * result + ( oblique ? 1 : 0 );
    result = 29 * result + ( embeddable ? 1 : 0 );
    result = 29 * result + name.hashCode();
    result = 29 * result + variant.hashCode();
    return result;
  }
}
