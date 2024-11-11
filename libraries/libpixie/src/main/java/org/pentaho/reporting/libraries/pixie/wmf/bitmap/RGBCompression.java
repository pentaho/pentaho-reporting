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

public class RGBCompression extends BitmapCompression {
  public RGBCompression() {
  }

  public int[] decompress( final InputStream in, final GDIPalette palette )
    throws IOException {
    final int[] target = new int[ getWidth() * getHeight() ];

    switch( getBpp() ) {
      case 1:
        fillMono( target, in, palette );
        break;
      case 4:
        fill4Bit( target, in, palette );
        break;
      case 8:
        fill8Bit( target, in, palette );
        break;
      case 16:
        fill16Bit( target, in, palette );
        break;
      case 24:
        fill24Bit( target, in, palette );
        break;
      case 32:
        fill32Bit( target, in, palette );
        break;
    }
    return target;
  }

  /**
   * Cut or padd the string to the given size
   *
   * @param size    the wanted length
   * @param padChar char to use for padding (must be of length()==1!)
   * @return the string with correct lenght, padded with pad if necessary
   */
  public static String forceToSizeLeft( final String str, final int size,
                                        final char padChar ) {
    if ( str != null && str.length() == size ) {
      return str;
    }

    final StringBuffer tmp;
    if ( str == null ) {
      tmp = new StringBuffer( size );
    } else {
      tmp = new StringBuffer( str );
    }

    if ( tmp.length() > size ) {
      tmp.setLength( size );
      return tmp.toString();  // do cutting
    } else {
      final StringBuffer t2 = new StringBuffer( size );

      final int arsize = size - tmp.length();
      final char[] ar = new char[ arsize ];
      for ( int i = 0; i < arsize; i++ ) {
        ar[ i ] = padChar;
      }
      t2.append( ar );
      t2.append( tmp );
      return t2.toString();
    }
  }

  public void fillMono( final int[] target, final InputStream in, final GDIPalette pal )
    throws IOException {
    final int noOfBytes = (int) Math.ceil( target.length / 8 );
    if ( isTopDown() == false ) {
      for ( int i = noOfBytes - 1; i >= 0; i-- ) {
        final int iByte = readInt( in );
        if ( iByte == -1 ) {
          return;
        }


        final int[] data = expandMonocrome( iByte, pal );
        final int left = ( target.length - i * 8 );
        final int size = Math.min( 8, left );

        for ( int ij = size - 1; ij >= 0; ij-- ) {
          target[ 7 - ij + i * 8 ] = data[ ij ];
        }
      }
    } else {
      for ( int i = 0; i < noOfBytes; i++ ) {
        final int iByte = readInt( in );
        if ( iByte == -1 ) {
          return;
        }


        final int[] data = expandMonocrome( iByte, pal );
        System.arraycopy( data, 0, target, i * 8, 8 );
      }
    }
  }

  public void fill4Bit( final int[] target, final InputStream in, final GDIPalette pal )
    throws IOException {
    final int noOfBytes = (int) Math.ceil( target.length / 2 );

    if ( isTopDown() == false ) {
      for ( int i = noOfBytes - 1; i >= 0; i-- ) {
        final int iByte = in.read();
        if ( iByte == -1 ) {
          return;
        }

        final int[] data = expand4BitTuple( iByte, pal );
        target[ i * 2 ] = data[ 1 ];
        target[ i * 2 + 1 ] = data[ 0 ];
      }
    } else {
      for ( int i = 0; i < noOfBytes; i++ ) {
        final int iByte = in.read();
        if ( iByte == -1 ) {
          return;
        }

        final int[] data = expand4BitTuple( iByte, pal );
        target[ i * 2 ] = data[ 0 ];
        target[ i * 2 + 1 ] = data[ 1 ];
      }
    }
  }

  public void fill8Bit( final int[] target, final InputStream in, final GDIPalette pal )
    throws IOException {
    final int noOfBytes = target.length;
    if ( isTopDown() == false ) {
      for ( int i = noOfBytes - 1; i >= 0; i-- ) {
        final int iByte = in.read();
        if ( iByte == -1 ) {
          return;
        }

        target[ i ] = pal.lookupColor( iByte );
      }
    } else {
      for ( int i = 0; i < noOfBytes; i++ ) {
        final int iByte = in.read();
        if ( iByte == -1 ) {
          return;
        }

        target[ i ] = pal.lookupColor( iByte );
      }
    }
  }

  public void fill16Bit( final int[] target, final InputStream in, final GDIPalette pal )
    throws IOException {
    final int noOfBytes = target.length * 2;
    if ( isTopDown() == false ) {
      for ( int i = noOfBytes - 1; i >= 0; i-- ) {
        final int iByte = in.read();
        if ( iByte == -1 ) {
          return;
        }
        final int iByte2 = in.read();
        if ( iByte2 == -1 ) {
          return;
        }

        target[ i ] = pal.lookupColor( ( iByte2 << 8 ) + iByte );
      }
    } else {
      for ( int i = 0; i < noOfBytes; i++ ) {
        final int iByte = in.read();
        if ( iByte == -1 ) {
          return;
        }
        final int iByte2 = in.read();
        if ( iByte2 == -1 ) {
          return;
        }

        target[ i ] = pal.lookupColor( ( iByte2 << 8 ) + iByte );
      }
    }
  }

  public void fill24Bit( final int[] target, final InputStream in, final GDIPalette pal )
    throws IOException {
    final int noOfBytes = target.length * 4;
    if ( isTopDown() == false ) {
      for ( int i = noOfBytes - 1; i >= 0; i-- ) {
        target[ i ] = pal.lookupColor( readInt( in ) );
      }
    } else {
      for ( int i = 0; i < noOfBytes; i++ ) {
        target[ i ] = pal.lookupColor( readInt( in ) );
      }
    }
  }

  public void fill32Bit( final int[] target, final InputStream in, final GDIPalette pal )
    throws IOException {
    final int noOfBytes = target.length * 4;
    if ( isTopDown() == false ) {
      for ( int i = noOfBytes - 1; i >= 0; i-- ) {
        target[ i ] = pal.lookupColor( readInt( in ) );
      }
    } else {
      for ( int i = 0; i < noOfBytes; i++ ) {
        target[ i ] = pal.lookupColor( readInt( in ) );
      }
    }
  }

  protected int readInt( final InputStream in )
    throws IOException {
    final int iByte = in.read();
    if ( iByte == -1 ) {
      return -1;
    }
    final int iByte2 = in.read();
    if ( iByte2 == -1 ) {
      return -1;
    }
    final int iByte3 = in.read();
    if ( iByte3 == -1 ) {
      return -1;
    }
    final int iByte4 = in.read();
    if ( iByte4 == -1 ) {
      return -1;
    }

    return ( ( iByte4 << 24 ) + ( iByte3 << 16 ) + ( iByte2 << 8 ) + ( iByte ) );
  }
}
