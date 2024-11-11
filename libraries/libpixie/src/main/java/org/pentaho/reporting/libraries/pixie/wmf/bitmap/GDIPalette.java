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
