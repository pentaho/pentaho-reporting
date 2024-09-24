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

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.io.FontDataInputSource;
import org.pentaho.reporting.libraries.fonts.io.ResourceFontDataInputSource;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontSource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Creation-Date: 22.03.2006, 17:41:58
 *
 * @author Thomas Morgner
 */
public class TrueTypeFontRegistryTest extends TestCase {
  public TrueTypeFontRegistryTest() {
    LibFontBoot.getInstance().start();
  }

  public TrueTypeFontRegistryTest( final String string ) {
    super( string );
    LibFontBoot.getInstance().start();
  }

  public void testFontRegistration() throws IOException, ResourceKeyCreationException {
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final String[] names = ge.getAvailableFontFamilyNames();

    final TrueTypeFontRegistry tfr = new TrueTypeFontRegistry();
    tfr.registerDefaultFontPath();
    final int length = names.length;
    for ( int i = 0; i < length; i++ ) {
      final String name = names[ i ];
      final FontFamily fofam = tfr.getFontFamily( name );
      if ( "AmerType Md BT".equals( name ) ) {
        final FontSource fr = (FontSource) fofam.getFontRecord( false, false );
        final ResourceManager resourceManager = new ResourceManager();
        resourceManager.registerDefaults();

        final ResourceKey fontSource = resourceManager.createKey( new File( fr.getFontSource() ) );
        final FontDataInputSource fs =
          new ResourceFontDataInputSource( resourceManager, fontSource );
        final TrueTypeFont ttf = new TrueTypeFont( fs );
        final NameTable nt = (NameTable) ttf.getTable( NameTable.TABLE_ID );
        //PostscriptInformationTable pst = ttf.getTable(PostscriptInformationTable.TABLE_ID);
        final FontHeaderTable fht = (FontHeaderTable) ttf.getTable( FontHeaderTable.TABLE_ID );
        /*
        TrueTypeFontMetricsFactory tfmf = new TrueTypeFontMetricsFactory();
        FontMetrics fm =
                tfmf.createMetrics(fr, new DefaultFontContext(14, false, false));
        */
        System.out.println( "HERE!" );

      }
      if ( fofam == null ) {
        System.out.println( "Warning: Font not known " + name );
      } else {
        System.out.println( "Font registered " + name );
      }
    }
  }

}
