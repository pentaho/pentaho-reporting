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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.FastStack;

import java.io.Serializable;

/**
 * The property lookup parser is used to resolve embedded references to properties within strings.
 * <p/>
 * The default format of the property specification is: <code>${property-name}</code> where 'property-name is the name
 * of the property. If this construct is found within the text, it is replaced with the value returned from a call to
 * "lookupVariable".
 *
 * @author Thomas Morgner
 */
public abstract class PropertyLookupParser implements Serializable {

  public static final int ESCAPE_MODE_NONE = 0;
  public static final int ESCAPE_MODE_ALL = 2;
  public static final int ESCAPE_MODE_STRICT = 1;
  /**
   * A parse state indicator signaling that the parser is outside a property.
   */
  private static final int EXPECT_DOLLAR = 0;
  /**
   * A parse state indicator signaling that an open brace is expected.
   */
  private static final int EXPECT_OPEN_BRACE = 1;

  private static final int EXPECT_NESTED_OPEN_BRACE = 2;

  /**
   * A parse state indicator signaling that a closed brace is expected. All chars received, which are not equal to the
   * closed brace, count as property name.
   */
  private static final int EXPECT_CLOSE_BRACE = 3;
  /**
   * The initial marker char, a $ by default.
   */
  private char markerChar;
  /**
   * The closing brace char.
   */
  private char closingBraceChar;
  /**
   * The opening brace char.
   */
  private char openingBraceChar;
  /**
   * The escape char.
   */
  private char escapeChar;

  private int escapeMode;

  /**
   * Initializes the parser to the default format of "${..}". The escape char will be a backslash.
   */
  protected PropertyLookupParser() {
    markerChar = '$';
    closingBraceChar = '}';
    openingBraceChar = '{';
    escapeChar = '\\';

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String escapeModeText =
        configuration.getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.util.PropertyLookupParserEscapeMode", "strict" );
    if ( "all".equals( escapeModeText ) ) {
      escapeMode = ESCAPE_MODE_ALL;
    } else if ( "none".equals( escapeModeText ) ) {
      escapeMode = ESCAPE_MODE_NONE;
    } else {
      escapeMode = ESCAPE_MODE_STRICT;
    }
  }

  public int getEscapeMode() {
    return escapeMode;
  }

  public void setEscapeMode( final int escapeMode ) {
    this.escapeMode = escapeMode;
  }

  /**
   * Returns the currently defined closed-brace char.
   *
   * @return the closed-brace char.
   */
  public char getClosingBraceChar() {
    return closingBraceChar;
  }

  /**
   * Defines the closing brace character.
   *
   * @param closingBraceChar
   *          the closed-brace character.
   */
  public void setClosingBraceChar( final char closingBraceChar ) {
    this.closingBraceChar = closingBraceChar;
  }

  /**
   * Returns the escape char.
   *
   * @return the escape char.
   */
  public char getEscapeChar() {
    return escapeChar;
  }

  /**
   * Defines the escape char.
   *
   * @param escapeChar
   *          the escape char
   */
  public void setEscapeChar( final char escapeChar ) {
    this.escapeChar = escapeChar;
  }

  /**
   * Returns the currently defined opening-brace char.
   *
   * @return the opening-brace char.
   */
  public char getOpeningBraceChar() {
    return openingBraceChar;
  }

  /**
   * Defines the opening brace character.
   *
   * @param openingBraceChar
   *          the opening-brace character.
   */
  public void setOpeningBraceChar( final char openingBraceChar ) {
    this.openingBraceChar = openingBraceChar;
  }

  /**
   * Returns initial property marker char.
   *
   * @return the initial property marker character.
   */
  public char getMarkerChar() {
    return markerChar;
  }

  /**
   * Defines initial property marker char.
   *
   * @param markerChar
   *          the initial property marker character.
   */
  public void setMarkerChar( final char markerChar ) {
    this.markerChar = markerChar;
  }

  /**
   * Translates the given string and resolves the embedded property references.
   *
   * @param value
   *          the raw value,
   * @return the fully translated string.
   */
  public String translateAndLookup( final String value ) {
    return translateAndLookup( value, new StaticDataRow() );
  }

  /**
   * Translates the given string and resolves the embedded property references.
   *
   * @param value
   *          the raw value,
   * @return the fully translated string.
   */
  public String translateAndLookup( final String value, final DataRow parameters ) {
    if ( value == null ) {
      return null;
    }

    final char[] chars = value.toCharArray();
    StringBuilder result = new StringBuilder( chars.length );

    boolean haveEscape = false;
    int state = PropertyLookupParser.EXPECT_DOLLAR;
    final FastStack<StringBuilder> stack = new FastStack<StringBuilder>();

    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[i];

      if ( haveEscape ) {
        haveEscape = false;
        if ( state == PropertyLookupParser.EXPECT_CLOSE_BRACE || escapeMode == ESCAPE_MODE_ALL ) {
          result.append( c );
        } else {
          if ( c == openingBraceChar || c == closingBraceChar || c == escapeChar || c == markerChar ) {
            result.append( c );
          } else {
            result.append( escapeChar );
            result.append( c );
          }
        }
        continue;
      }

      if ( ( state == PropertyLookupParser.EXPECT_DOLLAR || state == PropertyLookupParser.EXPECT_CLOSE_BRACE )
          && c == markerChar ) {
        state = PropertyLookupParser.EXPECT_OPEN_BRACE;
        continue;
      }

      if ( state == PropertyLookupParser.EXPECT_CLOSE_BRACE && c == closingBraceChar ) {
        final String columnName = result.toString();
        result = stack.pop();
        handleVariableLookup( result, parameters, columnName );

        if ( stack.isEmpty() ) {
          state = PropertyLookupParser.EXPECT_DOLLAR;
        } else {
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
        }
        continue;
      }

      if ( state == PropertyLookupParser.EXPECT_OPEN_BRACE ) {
        if ( c == openingBraceChar ) {
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
          stack.push( result );
          result = new StringBuilder( 100 );
          continue;
        }

        result.append( markerChar );
        if ( stack.isEmpty() ) {
          state = PropertyLookupParser.EXPECT_DOLLAR;
        } else {
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
        }

        // continue with adding the current char ..
      }

      if ( c == escapeChar && escapeMode != ESCAPE_MODE_NONE ) {
        haveEscape = true;
        continue;
      }

      if ( state != PropertyLookupParser.EXPECT_DOLLAR ) {
        result.append( postProcessCharacter( c ) );
      } else {
        result.append( c );
      }
    }

    if ( state != PropertyLookupParser.EXPECT_DOLLAR ) {
      while ( stack.isEmpty() == false ) {
        final String columnName = result.toString();
        result = stack.pop();
        result.append( markerChar );
        if ( state != PropertyLookupParser.EXPECT_OPEN_BRACE ) {
          result.append( openingBraceChar );
          result.append( columnName );
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
        }
      }
    }
    return result.toString();
  }

  protected char postProcessCharacter( final char c ) {
    return c;
  }

  protected void handleVariableLookup( final StringBuilder result, final DataRow parameters, final String columnName ) {
    final String s = lookupVariable( columnName );
    if ( s == null ) {
      result.append( markerChar );
      result.append( openingBraceChar );
      result.append( columnName );
      result.append( closingBraceChar );
    } else {
      result.append( s );
    }
  }

  /**
   * Looks up the property with the given name.
   *
   * @param property
   *          the name of the property to look up.
   * @return the translated value.
   */
  protected abstract String lookupVariable( String property );
}
