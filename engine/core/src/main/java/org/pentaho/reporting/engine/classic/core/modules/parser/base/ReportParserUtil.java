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

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import java.awt.Color;
import java.awt.Stroke;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Locator;

/**
 * A helper class to make parsing the xml files a lot easier.
 *
 * @author Thomas Morgner
 */
public final class ReportParserUtil {
  public static final String INCLUDE_PARSING_KEY = "::Include-parser";
  public static final Object INCLUDE_PARSING_VALUE = Boolean.TRUE;
  public static final String HELPER_OBJ_REPORT_NAME = "::Report";
  public static final String HELPER_OBJ_LEGACY_STYLES = "::Legacy-Styles";

  private static final Log logger = LogFactory.getLog( ReportParserUtil.class );
  private static boolean strictParsing;

  static {
    strictParsing =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.parser.base.StrictParseMode" ) );
  }

  /**
   * DefaultConstructor.
   */
  private ReportParserUtil() {
  }

  /**
   * Checks whether this report is a included report and not the main report definition.
   *
   * @param rootXmlReadHandler
   *          the root handler that provides access to the properties.
   * @return true, if the report is included, false otherwise.
   */
  public static boolean isIncluded( final RootXmlReadHandler rootXmlReadHandler ) {
    return INCLUDE_PARSING_VALUE.equals( rootXmlReadHandler.getHelperObject( INCLUDE_PARSING_KEY ) );
  }

  /**
   * Parses a vertical alignment value.
   *
   * @param value
   *          the text to parse.
   * @param locator
   *          the locator provides the current parse position for meaningful error messages.
   * @return the element alignment.
   * @throws ParseException
   *           if the alignment value is not recognised.
   */
  public static ElementAlignment parseVerticalElementAlignment( String value, final Locator locator )
    throws ParseException {
    if ( value == null ) {
      return null;
    }
    // todo: Remove me
    value = value.toLowerCase();

    if ( "top".equals( value ) ) {
      return ElementAlignment.TOP;
    }
    if ( "middle".equals( value ) ) {
      return ElementAlignment.MIDDLE;
    }
    if ( "bottom".equals( value ) ) {
      return ElementAlignment.BOTTOM;
    }
    if ( strictParsing ) {
      throw new ParseException( "Invalid vertical alignment", locator );
    }
    if ( locator == null ) {
      logger.warn( "Invalid value encountered for vertical alignment attribute." );
    } else {
      logger.warn( "Invalid value encountered for vertical alignment attribute. [Line: " + locator.getLineNumber()
          + " Column: " + locator.getColumnNumber() + "]" );
    }
    return ElementAlignment.TOP;
  }

  /**
   * Parses a horizontal alignment value.
   *
   * @param value
   *          the text to parse.
   * @param locator
   *          the locator provides the current parse position for meaningful error messages.
   * @return the element alignment.
   * @throws ParseException
   *           if a parse error occured.
   */
  public static ElementAlignment parseHorizontalElementAlignment( String value, final Locator locator )
    throws ParseException {
    if ( value == null ) {
      return null;
    }
    // todo: remove me
    value = value.toLowerCase();
    if ( "left".equals( value ) ) {
      return ElementAlignment.LEFT;
    }
    if ( "center".equals( value ) ) {
      return ElementAlignment.CENTER;
    }
    if ( "right".equals( value ) ) {
      return ElementAlignment.RIGHT;
    }
    if ( "justify".equals( value ) ) {
      return ElementAlignment.JUSTIFY;
    }
    if ( strictParsing ) {
      throw new ParseException( "Invalid horizontal alignment", locator );
    }

    if ( locator == null ) {
      logger.warn( "Invalid value encountered for horizontal alignment attribute." );
    } else {
      logger.warn( "Invalid value encountered for horizontal alignment attribute. [Line: " + locator.getLineNumber()
          + " Column: " + locator.getColumnNumber() + "]" );
    }
    return ElementAlignment.LEFT;
  }

  /**
   * Reads an attribute as float and returns <code>def</code> if that fails.
   *
   * @param value
   *          the attribute value.
   * @param locator
   *          the locator provides the current parse position for meaningful error messages.
   * @return the float value.
   * @throws ParseException
   *           if an parse error occured.
   */
  public static Float parseFloat( final String value, final Locator locator ) throws ParseException {
    if ( value == null ) {
      return null;
    }
    try {
      return new Float( value );
    } catch ( Exception ex ) {
      throw new ParseException( "Failed to parse value", locator );
    }
  }

  public static Boolean parseBoolean( final String value, final Locator locator ) throws ParseException {
    if ( value == null ) {
      return null;
    }
    if ( "true".equals( value ) ) {
      return Boolean.TRUE;
    } else if ( "false".equals( value ) ) {
      return Boolean.FALSE;
    } else {
      if ( strictParsing ) {
        throw new ParseException( "Failed to parse value", locator );
      }

      if ( locator == null ) {
        logger.warn( "Invalid value encountered for boolean attribute." );
      } else {
        logger.warn( "Invalid value encountered for boolean attribute. [Line: " + locator.getLineNumber() + " Column: "
            + locator.getColumnNumber() + "]" );
      }
      return Boolean.FALSE;
    }
  }

  /**
   * Reads an attribute as float and returns <code>def</code> if that fails.
   *
   * @param value
   *          the attribute value.
   * @param locator
   *          the locator provides the current parse position for meaningful error messages.
   * @return the float value.
   * @throws ParseException
   *           if an parse error occured.
   */
  public static Integer parseInteger( final String value, final Locator locator ) throws ParseException {
    if ( value == null ) {
      return null;
    }
    try {
      return new Integer( value );
    } catch ( Exception ex ) {
      throw new ParseException( "Failed to parse value", locator );
    }
  }

  /**
   * Parses a color entry. If the entry is in hexadecimal or ocal notation, the color is created using Color.decode().
   * If the string denotes a constant name of on of the color constants defined in java.awt.Color, this constant is
   * used.
   * <p/>
   * As fallback the color black is returned if no color can be parsed.
   *
   * @param color
   *          the color (as a string).
   * @return the paint.
   */
  public static Color parseColor( final String color ) {
    return parseColor( color, Color.black );
  }

  /**
   * Parses a color entry. If the entry is in hexadecimal or octal notation, the color is created using Color.decode().
   * If the string denotes a constant name of one of the color constants defined in java.awt.Color, this constant is
   * used.
   * <p/>
   * As fallback the supplied default value is returned if no color can be parsed.
   *
   * @param color
   *          the color (as a string).
   * @param defaultValue
   *          the default value (returned if no color can be parsed).
   * @return the paint.
   */
  public static Color parseColor( final String color, final Color defaultValue ) {
    if ( color == null ) {
      return defaultValue;
    }
    try {
      // get color by hex or octal value
      return (Color) new ColorValueConverter().toPropertyValue( color );
    } catch ( Exception nfe ) {
      return defaultValue;
    }
  }

  /**
   * Parses a position of an element. If a relative postion is given, the returnvalue is a negative number between 0 and
   * -100.
   *
   * @param value
   *          the value.
   * @param exceptionMessage
   *          the exception message.
   * @param locator
   *          the locator provides the current parse position for meaningful error messages.
   * @return the float value.
   * @throws ParseException
   *           if there is a problem parsing the string.
   */
  public static float parseRelativeFloat( final String value, final String exceptionMessage, final Locator locator )
    throws ParseException {
    if ( value == null ) {
      throw new ParseException( exceptionMessage, locator );
    }
    if ( "auto".equalsIgnoreCase( value ) ) {
      return Long.MIN_VALUE;
    }
    final String tvalue = value.trim();
    if ( tvalue.length() > 0 && tvalue.charAt( tvalue.length() - 1 ) == '%' ) {
      final String number = tvalue.substring( 0, tvalue.length() - 1 );
      return ParserUtil.parseFloat( number, exceptionMessage, locator ) * -1.0f;
    } else {
      return ParserUtil.parseFloat( tvalue, exceptionMessage, locator );
    }
  }

  public static Stroke parseStroke( final String strokeStyle, final float weight ) {
    // "dashed | solid | dotted | dot-dot-dash | dot-dash"
    if ( "dashed".equalsIgnoreCase( strokeStyle ) ) {
      return StrokeUtility.createStroke( StrokeUtility.STROKE_DASHED, weight );
    } else if ( "dotted".equalsIgnoreCase( strokeStyle ) ) {
      return StrokeUtility.createStroke( StrokeUtility.STROKE_DOTTED, weight );
    } else if ( "dot-dot-dash".equalsIgnoreCase( strokeStyle ) ) {
      return StrokeUtility.createStroke( StrokeUtility.STROKE_DOT_DOT_DASH, weight );
    } else if ( "dot-dash".equalsIgnoreCase( strokeStyle ) ) {
      return StrokeUtility.createStroke( StrokeUtility.STROKE_DOT_DASH, weight );
    } else {
      return StrokeUtility.createStroke( StrokeUtility.STROKE_SOLID, weight );
    }
  }

  public static VerticalTextAlign parseVerticalTextElementAlignment( final String value, final Locator locator )
    throws ParseException {
    if ( value == null ) {
      return null;
    }

    if ( "top".equals( value ) ) {
      return VerticalTextAlign.TOP;
    }
    if ( "middle".equals( value ) ) {
      return VerticalTextAlign.MIDDLE;
    }
    if ( "bottom".equals( value ) ) {
      return VerticalTextAlign.BOTTOM;
    }
    if ( "baseline".equals( value ) ) {
      return VerticalTextAlign.BASELINE;
    }
    if ( "central".equals( value ) ) {
      return VerticalTextAlign.CENTRAL;
    }
    if ( "sub".equals( value ) ) {
      return VerticalTextAlign.SUB;
    }
    if ( "super".equals( value ) ) {
      return VerticalTextAlign.SUPER;
    }
    if ( "text-bottom".equals( value ) ) {
      return VerticalTextAlign.TEXT_BOTTOM;
    }
    if ( "text-top".equals( value ) ) {
      return VerticalTextAlign.TEXT_TOP;
    }
    if ( "use-script".equals( value ) ) {
      return VerticalTextAlign.USE_SCRIPT;
    }

    throw new ParseException( "Invalid vertical alignment", locator );
  }

  public static int parseVersion( final String s ) {
    if ( StringUtils.isEmpty( s ) ) {
      return -1;
    }

    try {
      final StringTokenizer strtok = new StringTokenizer( s, "." );
      int version = 0;
      while ( strtok.hasMoreElements() ) {
        final String token = strtok.nextToken();
        final int i = Integer.parseInt( token );
        version = version * 1000 + i;
      }
      return version;
    } catch ( Exception e ) {
      return -1;
    }
  }
}
