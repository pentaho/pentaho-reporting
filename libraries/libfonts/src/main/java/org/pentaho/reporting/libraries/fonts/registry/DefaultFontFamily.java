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

import java.util.HashSet;

/**
 * Creation-Date: 07.11.2005, 19:45:50
 *
 * @author Thomas Morgner
 */
public class DefaultFontFamily implements FontFamily {
  private static final long serialVersionUID = 6371288535803940635L;

  private HashSet<String> allNames;
  private String familyName;

  private FontRecord[] fontRecords;

  public DefaultFontFamily( final String familyName ) {
    if ( familyName == null ) {
      throw new NullPointerException( "A FamilyName must be given" );
    }

    this.familyName = familyName;
    this.allNames = new HashSet<String>();
    this.allNames.add( familyName );
    this.fontRecords = new FontRecord[ 4 ];
  }

  public String getFamilyName() {
    return familyName;
  }

  public void addName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException( "Name must not be null" );
    }
    allNames.add( name );
  }

  public String[] getAllNames() {
    return allNames.toArray( new String[ allNames.size() ] );
  }

  public FontRecord getFontRecord( final boolean bold, final boolean italics ) {
    if ( bold && italics ) {
      final FontRecord record = fontRecords[ 3 ];
      if ( record != null ) {
        return record;
      }
    }
    if ( italics ) {
      final FontRecord record = fontRecords[ 2 ];
      if ( record != null ) {
        return record;
      }
    }
    if ( bold ) {
      final FontRecord record = fontRecords[ 1 ];
      if ( record != null ) {
        return record;
      }
    }
    final FontRecord record = fontRecords[ 0 ];
    if ( record != null ) {
      return record;
    }

    final int fontRecordCount = fontRecords.length;
    for ( int i = 0; i < fontRecordCount; i++ ) {
      final FontRecord fontRecord = fontRecords[ i ];
      if ( fontRecord != null ) {
        return fontRecord;
      }
    }
    // we tried everything, with no luck ..
    return null;
  }

  public void addFontRecord( final FontRecord record ) {
    final boolean bold = record.isBold();
    final boolean italics = record.isItalic();

    final int index;
    if ( bold && italics ) {
      index = 3;
    } else if ( italics ) {
      index = 2;
    } else if ( bold ) {
      index = 1;
    } else {
      index = 0;
    }

    final FontRecord oldRecord = fontRecords[ index ];
    if ( oldRecord == null ) {
      fontRecords[ index ] = record;
    } else {
      if ( record.isOblique() && oldRecord.isOblique() == false ) {
        // skip, an non-oblique font is more valuable than an oblique font
        return;
      }
      fontRecords[ index ] = record;
    }
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DefaultFontFamily that = (DefaultFontFamily) o;

    if ( !familyName.equals( that.familyName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return familyName.hashCode();
  }
}
