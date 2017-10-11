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

package org.pentaho.reporting.libraries.fonts.tools;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.afm.AfmFontRegistry;
import org.pentaho.reporting.libraries.fonts.pfm.PfmFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.FontSource;

public class ListFonts {
  private ListFonts() {
  }

  private static void printRecord( final FontRecord record ) {
    if ( record == null ) {
      System.out.println( "  - (there is no font defined for that style and family.)" );
      return;
    }
    if ( record instanceof FontSource ) {
      final FontSource fs = (FontSource) record;
      System.out.println(
        "  " + record.getFamily().getFamilyName() + " italics:" + record.isItalic() + " oblique:" + record.isOblique()
          + " bold: " + record.isBold() + ' ' + fs.getFontSource() );
    } else {
      System.out.println(
        "  " + record.getFamily().getFamilyName() + " italics:" + record.isItalic() + " oblique:" + record.isOblique()
          + " bold: " + record.isBold() );
    }
  }

  public static void main( final String[] args ) {
    LibFontBoot.getInstance().start();

    //    final TrueTypeFontRegistry registry = new TrueTypeFontRegistry();
    //    registry.initialize();
    //    final String[] fontFamilies = registry.getRegisteredFamilies();
    //    for (int i = 0; i < fontFamilies.length; i++)
    //    {
    //      String fontFamily = fontFamilies[i];
    //      final FontFamily family = registry.getFontFamily(fontFamily);
    //      String[] names = family.getAllNames();
    //      printRecord(family.getFontRecord(true, false));
    //    }
    //

    listFontS( new AfmFontRegistry() );
    listFontS( new PfmFontRegistry() );
  }

  private static void listFontS( final FontRegistry registry ) {
    registry.initialize();
    final String[] fontFamilies = registry.getRegisteredFamilies();
    for ( int i = 0; i < fontFamilies.length; i++ ) {
      final String fontFamily = fontFamilies[ i ];
      final FontFamily family = registry.getFontFamily( fontFamily );
      //final String[] names = family.getAllNames();
      printRecord( family.getFontRecord( false, false ) );
      printRecord( family.getFontRecord( true, false ) );
      printRecord( family.getFontRecord( false, true ) );
      printRecord( family.getFontRecord( true, true ) );
    }
  }
}
