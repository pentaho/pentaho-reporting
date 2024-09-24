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

package org.pentaho.reporting.libraries.xmlns.parser;

/**
 * Provides encoding of raw bytes to base64-encoded characters, and decoding of base64 characters to raw bytes. date: 06
 * August 1998 modified: 14 February 2000 modified: 22 September 2000
 *
 * @author Kevin Kelley (kelley@ruralnet.net)
 * @version 1.3
 */
public class Base64 {
  /**
   * Private constructor prevents object creation.
   */
  private Base64() {
  }

  /**
   * returns an array of base64-encoded characters to represent the passed data array.
   *
   * @param data the array of bytes to encode
   * @return base64-coded character array.
   */
  public static char[] encode( final byte[] data ) {
    final char[] out = new char[ ( ( data.length + 2 ) / 3 ) * 4 ];

    //
    // 3 bytes encode to 4 chars.  Output is always an even
    // multiple of 4 characters.
    //
    for ( int i = 0, index = 0; i < data.length; i += 3, index += 4 ) {

      int val = ( 0xFF & data[ i ] );
      val <<= 8;
      boolean trip = false;
      if ( ( i + 1 ) < data.length ) {
        val |= ( 0xFF & data[ i + 1 ] );
        trip = true;
      }
      val <<= 8;
      boolean quad = false;
      if ( ( i + 2 ) < data.length ) {
        val |= ( 0xFF & data[ i + 2 ] );
        quad = true;
      }
      out[ index + 3 ] = Base64.ALPHABET[ ( quad ? ( val & 0x3F ) : 64 ) ];
      val >>= 6;
      out[ index + 2 ] = Base64.ALPHABET[ ( trip ? ( val & 0x3F ) : 64 ) ];
      val >>= 6;
      out[ index + 1 ] = Base64.ALPHABET[ val & 0x3F ];
      val >>= 6;
      out[ index ] = Base64.ALPHABET[ val & 0x3F ];
    }
    return out;
  }

  /**
   * Decodes a BASE-64 encoded stream to recover the original data. White space before and after will be trimmed away,
   * but no other manipulation of the input will be performed.
   * <p/>
   * As of version 1.2 this method will properly handle input containing junk characters (newlines and the like) rather
   * than throwing an error. It does this by pre-parsing the input and generating from that a count of VALID input
   * characters.
   *
   * @param data the character data.
   * @return The decoded data.
   */
  public static byte[] decode( final char[] data ) {
    // as our input could contain non-BASE64 data (newlines,
    // whitespace of any sort, whatever) we must first adjust
    // our count of USABLE data so that...
    // (a) we don't misallocate the output array, and
    // (b) think that we miscalculated our data length
    //     just because of extraneous throw-away junk

    int tempLen = data.length;
    for ( int ix = 0; ix < data.length; ix++ ) {
      if ( ( data[ ix ] > 255 ) || Base64.CODES[ data[ ix ] ] < 0 ) {
        --tempLen; // ignore non-valid chars and padding
      }
    }
    // calculate required length:
    //  -- 3 bytes for every 4 valid base64 chars
    //  -- plus 2 bytes if there are 3 extra base64 chars,
    //     or plus 1 byte if there are 2 extra.

    int len = ( tempLen / 4 ) * 3;
    if ( ( tempLen % 4 ) == 3 ) {
      len += 2;
    }
    if ( ( tempLen % 4 ) == 2 ) {
      len += 1;
    }

    final byte[] out = new byte[ len ];


    int shift = 0; // # of excess bits stored in accum
    int accum = 0; // excess bits
    int index = 0;

    // we now go through the entire array (NOT using the 'tempLen' value)
    for ( int ix = 0; ix < data.length; ix++ ) {
      final int value = ( data[ ix ] > 255 ) ? -1 : Base64.CODES[ data[ ix ] ];

      if ( value >= 0 ) { // skip over non-code
        accum <<= 6; // bits shift up by 6 each time thru
        shift += 6; // loop, with new bits being put in
        accum |= value; // at the bottom.
        if ( shift >= 8 ) { // whenever there are 8 or more shifted in,
          shift -= 8; // write them out (from the top, leaving any
          out[ index ] = // excess at the bottom for next iteration.
            (byte) ( ( accum >> shift ) & 0xff );
          index += 1;
        }
      }
      // we will also have skipped processing a padding null byte ('=') here;
      // these are used ONLY for padding to an even length and do not legally
      // occur as encoded data. for this reason we can ignore the fact that
      // no index++ operation occurs in that special case: the out[] array is
      // initialized to all-zero bytes to start with and that works to our
      // advantage in this combination.
    }

    // if there is STILL something wrong we just have to throw up now!
    if ( index != out.length ) {
      throw new Error( "Miscalculated data length (wrote "
        + index + " instead of " + out.length + ')' );
    }

    return out;
  }


  //
  // code characters for values 0..63
  //
  private static final char[] ALPHABET =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

  //
  // lookup table for converting base64 characters to value in range 0..63
  //
  private static final byte[] CODES = new byte[ 256 ];

  static {
    for ( int i = 0; i < 256; i++ ) {
      Base64.CODES[ i ] = -1;
    }
    for ( int i = 'A'; i <= 'Z'; i++ ) {
      Base64.CODES[ i ] = (byte) ( i - 'A' );
    }
    for ( int i = 'a'; i <= 'z'; i++ ) {
      Base64.CODES[ i ] = (byte) ( 26 + i - 'a' );
    }
    for ( int i = '0'; i <= '9'; i++ ) {
      Base64.CODES[ i ] = (byte) ( 52 + i - '0' );
    }
    Base64.CODES[ '+' ] = 62;
    Base64.CODES[ '/' ] = 63;
  }

  //
  //
  //    ///////////////////////////////////////////////////
  //    // remainder (main method and helper functions) is
  //    // for testing purposes only, feel free to clip it.
  //    ///////////////////////////////////////////////////
  //
  //    /**
  //     * Entry point.
  //     *
  //     * @param args  the command line arguments.
  //     */
  //    public static void main(final String[] args) {
  //        boolean decode = false;
  //
  //        if (args.length == 0) {
  //            System.out.println("usage:  java Base64 [-d[ecode]] filename");
  //            System.exit(0);
  //        }
  //        for (int i = 0; i < args.length; i++) {
  //            if ("-decode".equalsIgnoreCase(args[i])) {
  //                decode = true;
  //            }
  //            else if ("-d".equalsIgnoreCase(args[i])) {
  //                decode = true;
  //            }
  //        }
  //
  //        final String filename = args[args.length - 1];
  //        final File file = new File(filename);
  //        if (!file.exists()) {
  //            System.out.println("Error:  file '" + filename + "' doesn't exist!");
  //            System.exit(0);
  //        }
  //
  //        if (decode) {
  //            final char[] encoded = org.pentaho.reporting.libraries.xmls.parser.Base64.readChars(file);
  //            final byte[] decoded = org.pentaho.reporting.libraries.xmls.parser.Base64.decode(encoded);
  //            org.pentaho.reporting.libraries.xmls.parser.Base64.writeBytes(file, decoded);
  //        }
  //        else {
  //            final byte[] decoded = org.pentaho.reporting.libraries.xmls.parser.Base64.readBytes(file);
  //            final char[] encoded = org.pentaho.reporting.libraries.xmls.parser.Base64.encode(decoded);
  //            org.pentaho.reporting.libraries.xmls.parser.Base64.writeChars(file, encoded);
  //        }
  //    }
  //
  //    private static byte[] readBytes(final File file) {
  //        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  //        try {
  //            final InputStream fis = new FileInputStream(file);
  //            final InputStream is = new BufferedInputStream(fis);
  //
  //            int count;
  //            final byte[] buf = new byte[16384];
  //            while ((count = is.read(buf)) != -1) {
  //                if (count > 0) {
  //                    baos.write(buf, 0, count);
  //                }
  //            }
  //            is.close();
  //        }
  //        catch (Exception e) {
  //            e.printStackTrace();
  //        }
  //
  //        return baos.toByteArray();
  //    }
  //
  //    private static char[] readChars(final File file) {
  //        final CharArrayWriter caw = new CharArrayWriter();
  //        try {
  //            final Reader fr = new FileReader(file);
  //            final Reader in = new BufferedReader(fr);
  //            int count;
  //            final char[] buf = new char[16384];
  //            while ((count = in.read(buf)) != -1) {
  //                if (count > 0) {
  //                    caw.write(buf, 0, count);
  //                }
  //            }
  //            in.close();
  //        }
  //        catch (Exception e) {
  //            e.printStackTrace();
  //        }
  //
  //        return caw.toCharArray();
  //    }
  //
  //    private static void writeBytes(final File file, final byte[] data) {
  //        try {
  //            final OutputStream fos = new FileOutputStream(file);
  //            final OutputStream os = new BufferedOutputStream(fos);
  //            os.write(data);
  //            os.close();
  //        }
  //        catch (Exception e) {
  //            e.printStackTrace();
  //        }
  //    }
  //
  //    private static void writeChars(final File file, final char[] data) {
  //        try {
  //            final Writer fos = new FileWriter(file);
  //            final Writer os = new BufferedWriter(fos);
  //            os.write(data);
  //            os.close();
  //        }
  //        catch (Exception e) {
  //            e.printStackTrace();
  //        }
  //    }
  //    ///////////////////////////////////////////////////
  //    // end of test code.
  //    ///////////////////////////////////////////////////
  //
}
