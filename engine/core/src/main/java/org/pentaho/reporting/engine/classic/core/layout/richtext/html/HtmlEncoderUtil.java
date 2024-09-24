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

package org.pentaho.reporting.engine.classic.core.layout.richtext.html;

/**
 * Utility methods to support HTML style encodings like the UTF and CSS encodings.
 *
 * @author Thomas Morgner
 */
public final class HtmlEncoderUtil {
  /**
   * CSS Escapes: CSS 2.1 / 4.1.3 Characters and case
   * <p/>
   * Third, backslash escapes allow authors to refer to characters they can't easily put in a document. In this case,
   * the backslash is followed by at most six hexadecimal digits (0..9A..F), which stand for the ISO 10646 ([ISO10646])
   * character with that number, which must not be zero. (It is undefined in CSS 2.1 what happens if a style sheet does
   * contain a character with Unicode codepoint zero.) If a character in the range [0-9a-f] follows the hexadecimal
   * number, the end of the number needs to be made clear. There are two ways to do that:
   * <p/>
   * 1. with a space (or other whitespace character): "\26 B" ("&B"). In this case, user agents should treat a "CR/LF"
   * pair (U+000D/U+000A) as a single whitespace character.<br/>
   * 2. by providing exactly 6 hexadecimal digits: "\000026B" ("&B")
   * <p/>
   * In fact, these two methods may be combined. Only one whitespace character is ignored after a hexadecimal escape.
   * Note that this means that a "real" space after the escape sequence must itself either be escaped or doubled.
   */

  /**
   * DefaultConstructor.
   */
  private HtmlEncoderUtil() {
  }

  /**
   * Provides a method to encode any string into a URL-safe form. Non-ASCII characters are first encoded as sequences of
   * two or three bytes, using the UTF-8 algorithm, before being encoded as %HH escapes.
   */
  private static final String[] HEX_CSS_ENCODING = new String[256];

  static {
    // static initializer block for creating the Hex-Encoding array. This is as fast as having a static array
    // but reduces the code size.
    for ( int i = 0; i < 256; i++ ) {
      final String s = Integer.toHexString( i );
      if ( s.length() == 1 ) {
        HEX_CSS_ENCODING[i] = '0' + s;
      } else {
        HEX_CSS_ENCODING[i] = s;
      }
    }
  }

  /**
   * Encode a string to the encoded form as defined in the CSS standard.
   *
   * @param s
   *          The string to be encoded
   * @return The encoded string
   */
  public static String encodeCSS( final String s ) {
    final StringBuffer sbuf = new StringBuffer( s.length() * 15 / 10 );
    return encodeCSS( s, sbuf );
  }

  public static String encodeCSS( final String s, final StringBuffer sbuf ) {
    final int len = s.length();
    for ( int i = 0; i < len; i++ ) {
      final char ch = s.charAt( i );
      if ( ch == '\"' ) {
        sbuf.append( '\\' );
        sbuf.append( ch );
      } else if ( ch >= 0x20 && ch <= 0x7f ) { // 7-Bit ascii
        sbuf.append( ch );
      } else {
        sbuf.append( '\\' );
        sbuf.append( HEX_CSS_ENCODING[( ch >> 16 ) & 0xFF] );
        sbuf.append( HEX_CSS_ENCODING[( ( ch >> 8 ) & 0xFF )] );
        sbuf.append( HEX_CSS_ENCODING[( ch & 0xFF )] );
      }
    }
    return sbuf.toString();
  }

}
