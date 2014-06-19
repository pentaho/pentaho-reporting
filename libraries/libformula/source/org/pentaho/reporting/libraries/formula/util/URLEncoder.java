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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.util;

import java.io.UnsupportedEncodingException;

/**
 * Creation-Date: Jan 22, 2007, 4:36:38 PM
 *
 * @author Thomas Morgner
 */
public class URLEncoder
{
  private URLEncoder()
  {
  }

  private static final String[] hex = {
    "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
    "%08", "%09", "%0A", "%0B", "%0C", "%0D", "%0E", "%0F",
    "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
    "%18", "%19", "%1A", "%1B", "%1C", "%1D", "%1E", "%1F",
    "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
    "%28", "%29", "%2A", "%2B", "%2C", "%2D", "%2E", "%2F",
    "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
    "%38", "%39", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F",
    "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
    "%48", "%49", "%4A", "%4B", "%4C", "%4D", "%4E", "%4F",
    "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
    "%58", "%59", "%5A", "%5B", "%5C", "%5D", "%5E", "%5F",
    "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
    "%68", "%69", "%6A", "%6B", "%6C", "%6D", "%6E", "%6F",
    "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
    "%78", "%79", "%7A", "%7B", "%7C", "%7D", "%7E", "%7F",
    "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
    "%88", "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F",
    "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
    "%98", "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F",
    "%A0", "%A1", "%A2", "%A3", "%A4", "%A5", "%A6", "%A7",
    "%A8", "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF",
    "%B0", "%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7",
    "%B8", "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF",
    "%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7",
    "%C8", "%C9", "%CA", "%CB", "%CC", "%CD", "%CE", "%CF",
    "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7",
    "%D8", "%D9", "%DA", "%DB", "%DC", "%DD", "%DE", "%DF",
    "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7",
    "%E8", "%E9", "%EA", "%EB", "%EC", "%ED", "%EE", "%EF",
    "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7",
    "%F8", "%F9", "%FA", "%FB", "%FC", "%FD", "%FE", "%FF"
  };

  /**
   * Encode a string according to RFC 1738.
   * <p/>
   * <quote> "...Only alphanumerics [0-9a-zA-Z], the special characters "$-_.+!*'()," [not
   * including the quotes - ed], and reserved characters used for their reserved purposes
   * may be used unencoded within a URL."</quote>
   * <p/>
   * <ul> <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through
   * '9' remain the same.
   * <p/>
   * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
   * <p/>
   * <li><p>All other ASCII characters are converted into the 3-character string "%xy",
   * where xy is the two-digit hexadecimal representation of the character code
   * <p/>
   * <li><p>All non-ASCII characters are encoded in two steps: first to a sequence of 2 or
   * 3 bytes, using the UTF-8 algorithm; secondly each of these bytes is encoded as "%xx".
   * </ul>
   *
   * This method was adapted from http://www.w3.org/International/URLUTF8Encoder.java
   * Licensed under http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
   *
   * @param s The string to be encoded
   * @return The encoded string
   */
  public static String encodeUTF8 (final String s)
  {
    final StringBuffer sbuf = new StringBuffer(s.length());
    final char[] sChars = s.toCharArray();
    final int len = sChars.length;
    for (int i = 0; i < len; i++)
    {
      final int ch = sChars[i];
      if ('A' <= ch && ch <= 'Z')
      {		// 'A'..'Z'
        sbuf.append((char) ch);
      }
      else if ('a' <= ch && ch <= 'z')
      {	// 'a'..'z'
        sbuf.append((char) ch);
      }
      else if ('0' <= ch && ch <= '9')
      {	// '0'..'9'
        sbuf.append((char) ch);
      }
      else if (ch == '-' || ch == '_'		// unreserved
              || ch == '.' || ch == '!'
              || ch == '~' || ch == '*'
              || ch == '\'' || ch == '('
              || ch == ')')
      {
        sbuf.append((char) ch);
      }
      else if (ch <= 0x007f)
      {		// other ASCII
        sbuf.append(hex[ch]);
      }
      else if (ch <= 0x07FF)
      {		// non-ASCII <= 0x7FF
        sbuf.append(hex[0xc0 | (ch >> 6)]);
        sbuf.append(hex[0x80 | (ch & 0x3F)]);
      }
      else
      {					// 0x7FF < ch <= 0xFFFF
        sbuf.append(hex[0xe0 | (ch >> 12)]);
        sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
        sbuf.append(hex[0x80 | (ch & 0x3F)]);
      }
    }
    return sbuf.toString();
  }

  private static String encodeBytes (final byte[] s)
  {
    final StringBuffer sbuf = new StringBuffer(s.length);
    final int len = s.length;
    for (int i = 0; i < len; i++)
    {
      final int ch = (s[i] & 0xff);
      if ('A' <= ch && ch <= 'Z')
      {		// 'A'..'Z'
        sbuf.append((char) ch);
      }
      else if ('a' <= ch && ch <= 'z')
      {	// 'a'..'z'
        sbuf.append((char) ch);
      }
      else if ('0' <= ch && ch <= '9')
      {	// '0'..'9'
        sbuf.append((char) ch);
      }
      else if (ch == '-' || ch == '_'		// unreserved
              || ch == '.' || ch == '!'
              || ch == '~' || ch == '*'
              || ch == '\'' || ch == '('
              || ch == ')')
      {
        sbuf.append((char) ch);
      }
      else
      {		// other ASCII
        sbuf.append(hex[ch]);
      }
    }
    return sbuf.toString();
  }

  public static String encode (final String s, final String encoding)
          throws UnsupportedEncodingException
  {
    if ("utf-8".equalsIgnoreCase(encoding))
    {
      return encodeUTF8(s);
    }

    return encodeBytes(s.getBytes(encoding));
  }

}
