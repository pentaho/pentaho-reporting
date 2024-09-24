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

import org.pentaho.reporting.libraries.fonts.ByteAccessUtilities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reads a TrueTypeCollection file and instantiates the fonts contained in that file.
 *
 * @author Thomas Morgner
 */
public class TrueTypeCollection {
  public static final long MAGIC_NUMBER =
    ( 't' << 24 | 't' << 16 | 'c' << 8 | 'f' );

  private File filename;
  private long numFonts;
  private long[] offsets;
  private TrueTypeFont[] fonts;

  public TrueTypeCollection( final File filename ) throws IOException {
    this.filename = filename;

    final RandomAccessFile raf = new RandomAccessFile( filename, "r" );
    final byte[] headerBuffer = new byte[ 12 ];
    raf.readFully( headerBuffer );
    if ( ByteAccessUtilities.readULong( headerBuffer, 0 ) != MAGIC_NUMBER ) {
      raf.close();
      throw new IOException();
    }
    numFonts = ByteAccessUtilities.readLong( headerBuffer, 8 );

    final byte[] offsetBuffer = new byte[ (int) ( 4 * numFonts ) ];
    raf.readFully( offsetBuffer );

    final int size = (int) numFonts;
    offsets = new long[ size ];
    fonts = new TrueTypeFont[ size ];
    for ( int i = 0; i < size; i++ ) {
      offsets[ i ] = ByteAccessUtilities.readULong( offsetBuffer, i * 4 );
    }
  }

  public File getFilename() {
    return filename;
  }

  public long getNumFonts() {
    return numFonts;
  }

  public TrueTypeFont getFont( final int index ) throws IOException {
    final TrueTypeFont cachedFont = fonts[ index ];
    if ( cachedFont != null ) {
      return cachedFont;
    }
    final TrueTypeFont font = new TrueTypeFont( filename, offsets[ index ], index );
    fonts[ index ] = font;
    return font;
  }
}
