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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pixie.wmf.bitmap;

import java.io.IOException;
import java.io.InputStream;

public class GDIPalette {
  private int noColors;
  private int[] colors;

  public GDIPalette() {
  }

  public void setNoOfColors( final int colors ) {
    this.noColors = colors;
  }

  public void setNoOfImportantColors( final int colors ) {
    if ( colors > noColors ) {
      throw new IllegalArgumentException(
        "There may be not more important colors than colors defined in the palette." );
    }
  }

  public void readPalette( final InputStream in )
    throws IOException {
    colors = new int[ noColors ];
    for ( int i = 0; i < noColors; i++ ) {
      colors[ i ] = readNextColor( in );
    }
  }

  private int readNextColor( final InputStream in )
    throws IOException {
    final int b = in.read();
    final int g = in.read();
    final int r = in.read();
    //final int filler =  
    //noinspection ResultOfMethodCallIgnored
    in.read();
    return b + ( g << 8 ) + ( r << 16 );
  }

  public int lookupColor( final int color ) {
    if ( noColors == 0 ) {
      // Convert from BGR (windows) format to RGB (java) format
      final int b = ( color & 0x00ff0000 ) >> 16;
      final int g = ( color & 0x0000ff00 );
      final int r = ( color & 0x000000ff );
      return b + g + ( r << 16 );
    }

    return colors[ color ];
  }
}
