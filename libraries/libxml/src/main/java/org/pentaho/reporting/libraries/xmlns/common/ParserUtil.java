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

package org.pentaho.reporting.libraries.xmlns.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.xmlns.LibXmlBoot;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Basic helper functions to ease up the process of parsing.
 *
 * @author Thomas Morgner
 */
public class ParserUtil {
  private static final Log logger = LogFactory.getLog( ParserUtil.class );
  private static boolean strictParsing;

  static {
    strictParsing = "true".equals( LibXmlBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.xmlns.StrictParseMode" ) );
  }


  /**
   * Private constructors prevent initializations of utility classes.
   */
  private ParserUtil() {
  }

  /**
   * Parses the string <code>text</code> into an int. If text is null or does not contain a parsable value, the message
   * given in <code>message</code> is used to throw a SAXException.
   *
   * @param text    the text to parse.
   * @param message the error message if parsing fails.
   * @param locator the SAX locator to print meaningfull error messages.
   * @return the int value.
   * @throws SAXException if there is a problem with the parsing.
   */
  public static int parseInt( final String text,
                              final String message,
                              final Locator locator )
    throws SAXException {
    if ( text == null ) {
      throw new SAXException( message );
    }

    try {
      return Integer.parseInt( text );
    } catch ( NumberFormatException nfe ) {
      throw new ParseException( "NumberFormatError: " + message, locator );
    }
  }

  /**
   * Parses the string <code>text</code> into an int. If text is null or does not contain a parsable value, the message
   * given in <code>message</code> is used to throw a SAXException.
   *
   * @param text    the text to parse.
   * @param message the error message if parsing fails.
   * @return the int value.
   * @throws SAXException if there is a problem with the parsing.
   */
  public static int parseInt( final String text, final String message )
    throws SAXException {
    if ( text == null ) {
      throw new SAXException( message );
    }

    try {
      return Integer.parseInt( text );
    } catch ( NumberFormatException nfe ) {
      throw new SAXException( "NumberFormatError: " + message );
    }
  }

  /**
   * Parses an integer.
   *
   * @param text       the text to parse.
   * @param defaultVal the default value.
   * @return the integer.
   */
  public static int parseInt( final String text, final int defaultVal ) {
    if ( text == null ) {
      return defaultVal;
    }

    try {
      return Integer.parseInt( text );
    } catch ( NumberFormatException nfe ) {
      return defaultVal;
    }
  }

  /**
   * Parses the string <code>text</code> into an float. If text is null or does not contain a parsable value, the
   * message given in <code>message</code> is used to throw a SAXException.
   *
   * @param text    the text to parse.
   * @param message the error message if parsing fails.
   * @param locator the SAX locator to print meaningfull error messages.
   * @return the float value.
   * @throws ParseException if the text is no valid float number.
   */
  public static float parseFloat( final String text,
                                  final String message,
                                  final Locator locator )
    throws ParseException {
    if ( text == null ) {
      throw new ParseException( message, locator );
    }
    try {
      return Float.parseFloat( text );
    } catch ( NumberFormatException nfe ) {
      throw new ParseException( "NumberFormatError: " + message, locator );
    }
  }

  /**
   * Parses the string <code>text</code> into an float. If text is null or does not contain a parsable value, the
   * message given in <code>message</code> is used to throw a SAXException.
   *
   * @param text    the text to parse.
   * @param message the error message if parsing fails.
   * @return the float value.
   * @throws SAXException if there is a problem with the parsing.
   */
  public static float parseFloat( final String text, final String message )
    throws SAXException {
    if ( text == null ) {
      throw new SAXException( message );
    }
    try {
      return Float.parseFloat( text );
    } catch ( NumberFormatException nfe ) {
      throw new SAXException( "NumberFormatError: " + message );
    }
  }

  /**
   * Parses the string <code>text</code> into an float. If text is null or does not contain a parsable value, the
   * message given in <code>message</code> is used to throw a SAXException.
   *
   * @param text       the text to parse.
   * @param defaultVal the defaultValue returned if parsing fails.
   * @return the float value.
   */
  public static float parseFloat( final String text, final float defaultVal ) {
    if ( text == null ) {
      return defaultVal;
    }
    try {
      return Float.parseFloat( text );
    } catch ( NumberFormatException nfe ) {
      return defaultVal;
    }
  }

  /**
   * Parses a boolean. If the string <code>text</code> contains the value of "true", the true value is returned, else
   * false is returned.
   *
   * @param text       the text to parse.
   * @param defaultVal the default value.
   * @return a boolean.
   */
  public static boolean parseBoolean( final String text,
                                      final boolean defaultVal ) {
    if ( text == null ) {
      return defaultVal;
    }
    if ( strictParsing ) {
      return "true".equals( text );
    } else {
      if ( text.equals( "true" ) ) {
        return true;
      } else if ( text.equals( "false" ) ) {
        return false;
      }

      logger.warn( "Invalid value encountered: Expected 'true' or 'false', but got '" + text + "'" );
      return "true".equalsIgnoreCase( text );
    }
  }


  /**
   * Translates an boolean string ("true" or "false") into the corresponding Boolean object.
   *
   * @param value   the string that represents the boolean.
   * @param locator the SAX locator to print meaningfull error messages.
   * @return Boolean.TRUE or Boolean.FALSE
   * @throws ParseException if an parse error occured or the string is not 'true' or 'false'.
   */
  public static Boolean parseBoolean( final String value, final Locator locator )
    throws ParseException {
    if ( value == null ) {
      return null;
    }
    if ( "true".equals( value ) ) {
      return Boolean.TRUE;
    } else if ( "false".equals( value ) ) {
      return Boolean.FALSE;
    }
    if ( strictParsing ) {
      throw new ParseException( "Failed to parse: Expected 'true' or 'false'", locator );
    }

    if ( locator == null ) {
      logger.warn( "Invalid value encountered for boolean attribute." );
    } else {
      logger.warn( "Invalid value encountered for boolean attribute. [Line: " +
        locator.getLineNumber() + " Column: " + locator.getColumnNumber() + "]" );
    }
    return Boolean.FALSE;
  }

  /**
   * Parses a string. If the <code>text</code> is null, defaultval is returned.
   *
   * @param text       the text to parse.
   * @param defaultVal the default value.
   * @return a string.
   */
  public static String parseString( final String text, final String defaultVal ) {
    if ( text == null ) {
      return defaultVal;
    }
    return text;
  }

}
