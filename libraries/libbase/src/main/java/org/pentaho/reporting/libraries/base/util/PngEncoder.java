/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoder;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * PngEncoder takes a Java Image object and creates a byte string which can be saved as a PNG file.  The Image is
 * presumed to use the DirectColorModel. <p/> <p>Thanks to Jay Denny at KeyPoint Software http://www.keypoint.com/ who
 * let me develop this code on company time.</p> <p/> <p>You may contact me with (probably very-much-needed)
 * improvements, comments, and bug fixes at:</p> <p/> <p><code>david@catcode.com</code></p> <p/> <p>This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later
 * version.</p> <p/> <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.</p> <p/> <p>You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA. A copy of the GNU LGPL may be found at <code>http://www.gnu.org/copyleft/lesser
 * .html</code></p>
 *
 * @author J. David Eisenberg
 * @version 1.5, 19 Oct 2003
 *          <p/>
 *          CHANGES: -------- 19-Nov-2002 : CODING STYLE CHANGES ONLY (by David Gilbert for Object Refinery Limited);
 *          19-Sep-2003 : Fix for platforms using EBCDIC (contributed by Paulo Soares); 19-Oct-2003 : Change private
 *          fields to protected fields so that PngEncoderB can inherit them (JDE) Fixed bug with calculation of nRows
 */

public class PngEncoder implements ImageEncoder {
  /**
   * A logger for debug-messages.
   */
  private static final Log logger = LogFactory.getLog( PngEncoder.class );

  /**
   * Constant specifying that alpha channel should be encoded.
   */
  public static final boolean ENCODE_ALPHA = true;

  /**
   * Constant specifying that alpha channel should not be encoded.
   */
  public static final boolean NO_ALPHA = false;

  /**
   * Constants for filter (NONE).
   */
  public static final int FILTER_NONE = 0;

  /**
   * Constants for filter (SUB).
   */
  public static final int FILTER_SUB = 1;

  /**
   * Constants for filter (UP).
   */
  public static final int FILTER_UP = 2;

  /**
   * Constants for filter (LAST).
   */
  public static final int FILTER_LAST = 2;

  /**
   * IHDR tag.
   */
  private static final byte[] IHDR = { 73, 72, 68, 82 };

  /**
   * IDAT tag.
   */
  private static final byte[] IDAT = { 73, 68, 65, 84 };

  /**
   * IEND tag.
   */
  private static final byte[] IEND = { 73, 69, 78, 68 };

  /**
   * PHYS tag.
   */
  private static final byte[] PHYS = { (byte) 'p', (byte) 'H', (byte) 'Y', (byte) 's' };

  /**
   * The png bytes.
   */
  private byte[] pngBytes;

  /**
   * The prior row.
   */
  private byte[] priorRow;

  /**
   * The left bytes.
   */
  private byte[] leftBytes;

  /**
   * The image.
   */
  private Image image;

  /**
   * The width.
   */
  private int width;

  /**
   * The height.
   */
  private int height;

  /**
   * The byte position.
   */
  private int bytePos;

  /**
   * The maximum position.
   */
  private int maxPos;

  /**
   * CRC.
   */
  private CRC32 crc = new CRC32();

  /**
   * The CRC value.
   */
  private long crcValue;

  /**
   * A flag indicating whether the alpha channel should also be encoded.
   */
  private boolean encodeAlpha;

  /**
   * The filter type.
   */
  private int filter;

  /**
   * The bytes-per-pixel.
   */
  private int bytesPerPixel;

  /**
   * The physical pixel dimension : number of pixels per inch on the X axis.
   */
  private int xDpi;

  /**
   * The physical pixel dimension : number of pixels per inch on the Y axis.
   */
  private int yDpi;

  /**
   * Used for conversion of DPI to Pixels per Meter.
   */
  private static final float INCH_IN_METER_UNIT = 0.0254f;

  /**
   * The compression level (1 = best speed, 9 = best compression, 0 = no compression).
   */
  private int compressionLevel;

  /**
   * Class constructor.
   */
  public PngEncoder() {
    this( null, false, PngEncoder.FILTER_NONE, 0 );
  }

  /**
   * Class constructor specifying Image to encode, with no alpha channel encoding.
   *
   * @param image A Java Image object which uses the DirectColorModel
   * @see java.awt.Image
   */
  public PngEncoder( final Image image ) {
    this( image, false, PngEncoder.FILTER_NONE, 0 );
  }

  /**
   * Class constructor specifying Image to encode, and whether to encode alpha.
   *
   * @param image       A Java Image object which uses the DirectColorModel
   * @param encodeAlpha Encode the alpha channel? false=no; true=yes
   * @see java.awt.Image
   */
  public PngEncoder( final Image image, final boolean encodeAlpha ) {
    this( image, encodeAlpha, PngEncoder.FILTER_NONE, 0 );
  }

  /**
   * Class constructor specifying Image to encode, whether to encode alpha, and filter to use.
   *
   * @param image       A Java Image object which uses the DirectColorModel
   * @param encodeAlpha Encode the alpha channel? false=no; true=yes
   * @param whichFilter 0=none, 1=sub, 2=up
   * @see java.awt.Image
   */
  public PngEncoder( final Image image, final boolean encodeAlpha, final int whichFilter ) {
    this( image, encodeAlpha, whichFilter, 0 );
  }


  /**
   * Class constructor specifying Image source to encode, whether to encode alpha, filter to use, and compression
   * level.
   *
   * @param image       A Java Image object
   * @param encodeAlpha Encode the alpha channel? false=no; true=yes
   * @param whichFilter 0=none, 1=sub, 2=up
   * @param compLevel   0..9 (1 = best speed, 9 = best compression, 0 = no compression)
   * @see java.awt.Image
   */
  public PngEncoder( final Image image,
                     final boolean encodeAlpha,
                     final int whichFilter,
                     final int compLevel ) {
    this.image = image;
    this.encodeAlpha = encodeAlpha;
    setFilter( whichFilter );
    setCompressionLevel( compLevel );
    if ( getCompressionLevel() == 0 ) {
      setCompressionLevel( 5 );
    }
  }

  /**
   * Set the image to be encoded.
   *
   * @param image A Java Image object which uses the DirectColorModel
   * @see java.awt.Image
   * @see java.awt.image.DirectColorModel
   */
  public void setImage( final Image image ) {
    this.image = image;
    this.pngBytes = null;
  }

  /**
   * Returns the image to be encoded.
   *
   * @return the image to be encoded.
   */
  public Image getImage() {
    return image;
  }

  /**
   * Creates an array of bytes that is the PNG equivalent of the current image, specifying whether to encode alpha or
   * not.
   *
   * @param encodeAlpha boolean false=no alpha, true=encode alpha
   * @return an array of bytes, or null if there was a problem
   * @deprecated Use the other pngEncode method and select the alpha-encoding via the constructor or setter.
   */
  public byte[] pngEncode( final boolean encodeAlpha ) {
    setEncodeAlpha( encodeAlpha );
    return pngEncode();
  }

  /**
   * Creates an array of bytes that is the PNG equivalent of the current image, specifying whether to encode alpha or
   * not.
   *
   * @return an array of bytes, or null if there was a problem
   */
  public byte[] pngEncode() {
    final byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

    if ( this.image == null ) {
      return null;
    }
    this.width = this.image.getWidth( null );
    this.height = this.image.getHeight( null );

    /*
    * start with an array that is big enough to hold all the pixels
    * (plus filter bytes), and an extra 200 bytes for header info
    */
    this.pngBytes = new byte[ ( ( this.width + 1 ) * this.height * 3 ) + 200 ];

    /*
    * keep track of largest byte written to the array
    */
    this.maxPos = 0;

    this.bytePos = writeBytes( pngIdBytes, 0 );
    //hdrPos = bytePos;
    writeHeader();
    writeResolution();
    //dataPos = bytePos;
    if ( writeImageData() ) {
      writeEnd();
      final byte[] pngBytes = resizeByteArray( this.pngBytes, this.maxPos );
      this.pngBytes = null;
      return pngBytes;
    } else {
      this.pngBytes = null;
      return null;
    }
  }

  /**
   * Set the alpha encoding on or off.
   *
   * @param encodeAlpha false=no, true=yes
   */
  public void setEncodeAlpha( final boolean encodeAlpha ) {
    this.encodeAlpha = encodeAlpha;
  }

  /**
   * Retrieve alpha encoding status.
   *
   * @return boolean false=no, true=yes
   */
  public boolean getEncodeAlpha() {
    return this.encodeAlpha;
  }

  /**
   * Set the filter to use.
   *
   * @param whichFilter from constant list
   */
  public void setFilter( final int whichFilter ) {
    this.filter = PngEncoder.FILTER_NONE;
    if ( whichFilter <= PngEncoder.FILTER_LAST ) {
      this.filter = whichFilter;
    }
  }

  /**
   * Retrieve filtering scheme.
   *
   * @return int (see constant list)
   */
  public int getFilter() {
    return this.filter;
  }

  /**
   * Set the compression level to use.
   *
   * @param level the compression level (1 = best speed, 9 = best compression, 0 = no compression)
   */
  public void setCompressionLevel( final int level ) {
    if ( level >= 0 && level <= 9 ) {
      this.compressionLevel = level;
    }
  }

  /**
   * Retrieve compression level.
   *
   * @return int (1 = best speed, 9 = best compression, 0 = no compression)
   */
  public int getCompressionLevel() {
    return this.compressionLevel;
  }

  /**
   * Increase or decrease the length of a byte array.
   *
   * @param array     The original array.
   * @param newLength The length you wish the new array to have.
   * @return Array of newly desired length. If shorter than the original, the trailing elements are truncated.
   */
  protected byte[] resizeByteArray( final byte[] array, final int newLength ) {
    final byte[] newArray = new byte[ newLength ];
    final int oldLength = array.length;

    System.arraycopy( array, 0, newArray, 0, Math.min( oldLength, newLength ) );
    return newArray;
  }

  /**
   * Write an array of bytes into the pngBytes array. Note: This routine has the side effect of updating maxPos, the
   * largest element written in the array. The array is resized by 1000 bytes or the length of the data to be written,
   * whichever is larger.
   *
   * @param data   The data to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  protected int writeBytes( final byte[] data, final int offset ) {
    this.maxPos = Math.max( this.maxPos, offset + data.length );
    if ( data.length + offset > this.pngBytes.length ) {
      this.pngBytes = resizeByteArray( this.pngBytes, this.pngBytes.length
        + Math.max( 1000, data.length ) );
    }
    System.arraycopy( data, 0, this.pngBytes, offset, data.length );
    return offset + data.length;
  }

  /**
   * Write an array of bytes into the pngBytes array, specifying number of bytes to write. Note: This routine has the
   * side effect of updating maxPos, the largest element written in the array. The array is resized by 1000 bytes or the
   * length of the data to be written, whichever is larger.
   *
   * @param data   The data to be written into pngBytes.
   * @param nBytes The number of bytes to be written.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  protected int writeBytes( final byte[] data, final int nBytes, final int offset ) {
    this.maxPos = Math.max( this.maxPos, offset + nBytes );
    if ( nBytes + offset > this.pngBytes.length ) {
      this.pngBytes = resizeByteArray( this.pngBytes, this.pngBytes.length
        + Math.max( 1000, nBytes ) );
    }
    System.arraycopy( data, 0, this.pngBytes, offset, nBytes );
    return offset + nBytes;
  }

  /**
   * Write a two-byte integer into the pngBytes array at a given position.
   *
   * @param n      The integer to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  protected int writeInt2( final int n, final int offset ) {
    final byte[] temp = { (byte) ( ( n >> 8 ) & 0xff ), (byte) ( n & 0xff ) };
    return writeBytes( temp, offset );
  }

  /**
   * Write a four-byte integer into the pngBytes array at a given position.
   *
   * @param n      The integer to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  protected int writeInt4( final int n, final int offset ) {
    final byte[] temp = { (byte) ( ( n >> 24 ) & 0xff ),
      (byte) ( ( n >> 16 ) & 0xff ),
      (byte) ( ( n >> 8 ) & 0xff ),
      (byte) ( n & 0xff ) };
    return writeBytes( temp, offset );
  }

  /**
   * Write a single byte into the pngBytes array at a given position.
   *
   * @param b      The integer to be written into pngBytes.
   * @param offset The starting point to write to.
   * @return The next place to be written to in the pngBytes array.
   */
  protected int writeByte( final int b, final int offset ) {
    final byte[] temp = { (byte) b };
    return writeBytes( temp, offset );
  }

  /**
   * Write a PNG "IHDR" chunk into the pngBytes array.
   */
  protected void writeHeader() {

    this.bytePos = writeInt4( 13, this.bytePos );
    final int startPos = bytePos;
    this.bytePos = writeBytes( PngEncoder.IHDR, this.bytePos );
    this.width = this.image.getWidth( null );
    this.height = this.image.getHeight( null );
    this.bytePos = writeInt4( this.width, this.bytePos );
    this.bytePos = writeInt4( this.height, this.bytePos );
    this.bytePos = writeByte( 8, this.bytePos ); // bit depth
    this.bytePos = writeByte( ( this.encodeAlpha ) ? 6 : 2, this.bytePos );
    // direct model
    this.bytePos = writeByte( 0, this.bytePos ); // compression method
    this.bytePos = writeByte( 0, this.bytePos ); // filter method
    this.bytePos = writeByte( 0, this.bytePos ); // no interlace
    this.crc.reset();
    this.crc.update( this.pngBytes, startPos, this.bytePos - startPos );
    this.crcValue = this.crc.getValue();
    this.bytePos = writeInt4( (int) this.crcValue, this.bytePos );
  }

  /**
   * Perform "sub" filtering on the given row. Uses temporary array leftBytes to store the original values of the
   * previous pixels.  The array is 16 bytes long, which will easily hold two-byte samples plus two-byte alpha.
   *
   * @param pixels   The array holding the scan lines being built
   * @param startPos Starting position within pixels of bytes to be filtered.
   * @param width    Width of a scanline in pixels.
   */
  protected void filterSub( final byte[] pixels, final int startPos, final int width ) {
    final int offset = this.bytesPerPixel;
    final int actualStart = startPos + offset;
    final int nBytes = width * this.bytesPerPixel;
    int leftInsert = offset;
    int leftExtract = 0;

    for ( int i = actualStart; i < startPos + nBytes; i++ ) {
      this.leftBytes[ leftInsert ] = pixels[ i ];
      pixels[ i ] = (byte) ( ( pixels[ i ] - this.leftBytes[ leftExtract ] ) % 256 );
      leftInsert = ( leftInsert + 1 ) % 0x0f;
      leftExtract = ( leftExtract + 1 ) % 0x0f;
    }
  }

  /**
   * Perform "up" filtering on the given row. Side effect: refills the prior row with current row
   *
   * @param pixels   The array holding the scan lines being built
   * @param startPos Starting position within pixels of bytes to be filtered.
   * @param width    Width of a scanline in pixels.
   */
  protected void filterUp( final byte[] pixels, final int startPos, final int width ) {
    final int nBytes = width * this.bytesPerPixel;
    for ( int i = 0; i < nBytes; i++ ) {
      final byte currentByte = pixels[ startPos + i ];
      pixels[ startPos + i ] = (byte) ( ( pixels[ startPos + i ] - this.priorRow[ i ] ) % 256 );
      this.priorRow[ i ] = currentByte;
    }
  }

  /**
   * Write the image data into the pngBytes array. This will write one or more PNG "IDAT" chunks. In order to conserve
   * memory, this method grabs as many rows as will fit into 32K bytes, or the whole image; whichever is less.
   *
   * @return true if no errors; false if error grabbing pixels
   */
  protected boolean writeImageData() {

    this.bytesPerPixel = ( this.encodeAlpha ) ? 4 : 3;

    final Deflater scrunch = new Deflater( this.compressionLevel );
    final ByteArrayOutputStream outBytes = new ByteArrayOutputStream( 1024 );
    final DeflaterOutputStream compBytes = new DeflaterOutputStream( outBytes, scrunch );
    try {
      int startRow = 0;       // starting row to process this time through
      //noinspection SuspiciousNameCombination
      int rowsLeft = this.height;  // number of rows remaining to write
      while ( rowsLeft > 0 ) {
        final int nRows = Math.max( Math.min( 32767 / ( this.width * ( this.bytesPerPixel + 1 ) ), rowsLeft ), 1 );

        final int[] pixels = new int[ this.width * nRows ];

        final PixelGrabber pg = new PixelGrabber( this.image, 0, startRow,
          this.width, nRows, pixels, 0, this.width );
        try {
          pg.grabPixels();
        } catch ( Exception e ) {
          logger.error( "interrupted waiting for pixels!", e );
          return false;
        }
        if ( ( pg.getStatus() & ImageObserver.ABORT ) != 0 ) {
          logger.error( "image fetch aborted or errored" );
          return false;
        }

        /*
        * Create a data chunk. scanLines adds "nRows" for
        * the filter bytes.
        */
        final byte[] scanLines = new byte[ this.width * nRows * this.bytesPerPixel + nRows ];

        if ( this.filter == PngEncoder.FILTER_SUB ) {
          this.leftBytes = new byte[ 16 ];
        }
        if ( this.filter == PngEncoder.FILTER_UP ) {
          this.priorRow = new byte[ this.width * this.bytesPerPixel ];
        }

        int scanPos = 0;
        int startPos = 1;
        for ( int i = 0; i < this.width * nRows; i++ ) {
          if ( i % this.width == 0 ) {
            scanLines[ scanPos++ ] = (byte) this.filter;
            startPos = scanPos;
          }
          scanLines[ scanPos++ ] = (byte) ( ( pixels[ i ] >> 16 ) & 0xff );
          scanLines[ scanPos++ ] = (byte) ( ( pixels[ i ] >> 8 ) & 0xff );
          scanLines[ scanPos++ ] = (byte) ( ( pixels[ i ] ) & 0xff );
          if ( this.encodeAlpha ) {
            scanLines[ scanPos++ ] = (byte) ( ( pixels[ i ] >> 24 )
              & 0xff );
          }
          if ( ( i % this.width == this.width - 1 )
            && ( this.filter != PngEncoder.FILTER_NONE ) ) {
            if ( this.filter == PngEncoder.FILTER_SUB ) {
              filterSub( scanLines, startPos, this.width );
            }
            if ( this.filter == PngEncoder.FILTER_UP ) {
              filterUp( scanLines, startPos, this.width );
            }
          }
        }

        /*
        * Write these lines to the output area
        */
        compBytes.write( scanLines, 0, scanPos );

        startRow += nRows;
        rowsLeft -= nRows;
      }
      compBytes.close();

      /*
      * Write the compressed bytes
      */
      final byte[] compressedLines = outBytes.toByteArray();
      final int nCompressed = compressedLines.length;

      this.crc.reset();
      this.bytePos = writeInt4( nCompressed, this.bytePos );
      this.bytePos = writeBytes( PngEncoder.IDAT, this.bytePos );
      this.crc.update( PngEncoder.IDAT );
      this.bytePos = writeBytes( compressedLines, nCompressed,
        this.bytePos );
      this.crc.update( compressedLines, 0, nCompressed );

      this.crcValue = this.crc.getValue();
      this.bytePos = writeInt4( (int) this.crcValue, this.bytePos );
      return true;
    } catch ( IOException e ) {
      logger.error( "Failed to write PNG Data", e );
      return false;
    } finally {
      scrunch.finish();
      scrunch.end();
    }
  }

  /**
   * Write a PNG "IEND" chunk into the pngBytes array.
   */
  protected void writeEnd() {
    this.bytePos = writeInt4( 0, this.bytePos );
    this.bytePos = writeBytes( PngEncoder.IEND, this.bytePos );
    this.crc.reset();
    this.crc.update( PngEncoder.IEND );
    this.crcValue = this.crc.getValue();
    this.bytePos = writeInt4( (int) this.crcValue, this.bytePos );
  }


  /**
   * Set the DPI for the X axis.
   *
   * @param xDpi The number of dots per inch
   */
  public void setXDpi( final int xDpi ) {
    this.xDpi = Math.round( xDpi / PngEncoder.INCH_IN_METER_UNIT );

  }

  /**
   * Get the DPI for the X axis.
   *
   * @return The number of dots per inch
   */
  public int getXDpi() {
    return Math.round( xDpi * PngEncoder.INCH_IN_METER_UNIT );
  }

  /**
   * Set the DPI for the Y axis.
   *
   * @param yDpi The number of dots per inch
   */
  public void setYDpi( final int yDpi ) {
    this.yDpi = Math.round( yDpi / PngEncoder.INCH_IN_METER_UNIT );
  }

  /**
   * Get the DPI for the Y axis.
   *
   * @return The number of dots per inch
   */
  public int getYDpi() {
    return Math.round( yDpi * PngEncoder.INCH_IN_METER_UNIT );
  }

  /**
   * Set the DPI resolution.
   *
   * @param xDpi The number of dots per inch for the X axis.
   * @param yDpi The number of dots per inch for the Y axis.
   */
  public void setDpi( final int xDpi, final int yDpi ) {
    this.xDpi = Math.round( xDpi / PngEncoder.INCH_IN_METER_UNIT );
    this.yDpi = Math.round( yDpi / PngEncoder.INCH_IN_METER_UNIT );
  }

  /**
   * Write a PNG "pHYs" chunk into the pngBytes array.
   */
  protected void writeResolution() {
    if ( xDpi > 0 && yDpi > 0 ) {
      bytePos = writeInt4( 9, bytePos );
      final int startPos = bytePos;
      bytePos = writeBytes( PngEncoder.PHYS, bytePos );
      bytePos = writeInt4( xDpi, bytePos );
      bytePos = writeInt4( yDpi, bytePos );
      bytePos = writeByte( 1, bytePos ); // unit is the meter.

      crc.reset();
      crc.update( pngBytes, startPos, bytePos - startPos );
      crcValue = crc.getValue();
      bytePos = writeInt4( (int) crcValue, bytePos );
    }
  }

  public void encodeImage( final Image image,
                           final OutputStream outputStream,
                           final float quality,
                           final boolean encodeAlpha ) throws IOException, UnsupportedEncoderException {
    setCompressionLevel( Math.min( 9, Math.max( 0, (int) ( quality * 10 ) ) ) );
    setImage( image );
    setEncodeAlpha( encodeAlpha );
    final byte[] bytes = this.pngEncode();
    outputStream.write( bytes );
  }

  public String getMimeType() {
    return "image/png";
  }
}
