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

package org.pentaho.reporting.libraries.xmlns.writer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * The character entity parser replaces all known occurrences of an entity in the format &amp;entityname;.
 *
 * @author Thomas Morgner
 */
public class CharacterEntityParser {
  private String[] charMap;

  /**
   * the entities, keyed by entity name.
   */
  private final HashMap entities;

  /**
   * Creates a new CharacterEntityParser and initializes the parser with the given set of entities.
   *
   * @param characterEntities the entities used for the parser
   */
  public CharacterEntityParser( final Properties characterEntities ) {
    if ( characterEntities == null ) {
      throw new NullPointerException( "CharacterEntities must not be null" );
    }

    entities = new HashMap( characterEntities );
    charMap = new String[ 65536 ];

    final Iterator entries = entities.entrySet().iterator();
    while ( entries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) entries.next();
      final String value = (String) entry.getValue();
      final String entityName = (String) entry.getKey();
      if ( value.length() != 1 ) {
        throw new IllegalStateException();
      }
      charMap[ value.charAt( 0 ) ] = entityName;
    }
  }

  /**
   * Creates a new CharacterEntityParser and initializes the parser with the given set of entities.
   *
   * @param characterEntities the entities used for the parser
   */
  public CharacterEntityParser( final HashMap characterEntities ) {
    if ( characterEntities == null ) {
      throw new NullPointerException( "CharacterEntities must not be null" );
    }

    entities = (HashMap) characterEntities.clone();
    charMap = new String[ 65536 ];

    final Iterator entries = entities.entrySet().iterator();
    while ( entries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) entries.next();
      final String value = (String) entry.getValue();
      final String entityName = (String) entry.getKey();
      if ( value.length() != 1 ) {
        throw new IllegalStateException();
      }
      charMap[ value.charAt( 0 ) ] = entityName;
    }
  }

  /**
   * create a new Character entity parser and initializes the parser with the entities defined in the XML standard.
   *
   * @return the CharacterEntityParser initialized with XML entities.
   */
  public static CharacterEntityParser createXMLEntityParser() {
    final HashMap entities = new HashMap();
    entities.put( "amp", "&" );
    entities.put( "quot", "\"" );
    entities.put( "lt", "<" );
    entities.put( "gt", ">" );
    entities.put( "apos", "\u0027" );
    return new CharacterEntityParser( entities );
  }

  /**
   * returns the entities used in the parser.
   *
   * @return the properties for this parser.
   */
  private HashMap getEntities() {
    return entities;
  }

  /**
   * Looks up the character for the entity name specified in <code>key</code>.
   *
   * @param key the entity name
   * @return the character as string with a length of 1
   */
  private String lookupCharacter( final String key ) {
    return (String) getEntities().get( key );
  }

  /**
   * Encode the given String, so that all known entites are encoded. All characters represented by these entites are now
   * removed from the string.
   *
   * @param value the original string
   * @return the encoded string.
   */
  public String encodeEntities( final String value ) {
    if ( value == null ) {
      throw new NullPointerException();
    }

    final int length = value.length();
    final StringBuffer writer = new StringBuffer( length );
    for ( int i = 0; i < length; i++ ) {
      final char character = value.charAt( i );
      final String lookup = charMap[ character ];
      if ( lookup == null ) {
        writer.append( character );
      } else {
        writer.append( '&' );
        writer.append( lookup );
        writer.append( ';' );
      }
    }
    return writer.toString();
  }

  /**
   * Decode the string, all known entities are replaced by their resolved characters.
   *
   * @param value the string that should be decoded.
   * @return the decoded string.
   */
  public String decodeEntities( final String value ) {
    if ( value == null ) {
      throw new NullPointerException();
    }

    int parserIndex = 0;
    int subStart = value.indexOf( '&', parserIndex );
    if ( subStart == -1 ) {
      return value;
    }
    int subEnd = value.indexOf( ';', subStart );
    if ( subEnd == -1 ) {
      return value;
    }

    final StringBuffer bufValue = new StringBuffer( value.substring( 0, subStart ) );
    do {
      // at this point we know, that there is at least one entity ..
      if ( value.charAt( subStart + 1 ) == '#' ) {
        final int subValue = parseInt( value.substring( subStart + 2, subEnd ), 0 );
        if ( ( subValue >= 1 ) && ( subValue <= 65536 ) ) {
          final char[] chr = new char[ 1 ];
          chr[ 0 ] = (char) subValue;
          bufValue.append( chr );
        } else {
          // invalid entity, do not decode ..
          bufValue.append( value.substring( subStart, subEnd ) );
        }
      } else {
        final String entity = value.substring( subStart + 1, subEnd );
        final String replaceString = lookupCharacter( entity );
        if ( replaceString != null ) {
          bufValue.append( decodeEntities( replaceString ) );
        } else {
          bufValue.append( '&' );
          bufValue.append( entity );
          bufValue.append( ';' );
        }
      }
      parserIndex = subEnd + 1;
      subStart = value.indexOf( '&', parserIndex );
      if ( subStart == -1 ) {
        bufValue.append( value.substring( parserIndex ) );
        subEnd = -1;
      } else {
        subEnd = value.indexOf( ';', subStart );
        if ( subEnd == -1 ) {
          bufValue.append( value.substring( parserIndex ) );
        } else {
          bufValue.append( value.substring( parserIndex, subStart ) );
        }
      }
    }
    while ( subStart != -1 && subEnd != -1 );

    return bufValue.toString();
  }

  /**
   * Parses the given string into an int-value. On errors the default value is returned.
   *
   * @param s          the string
   * @param defaultVal the default value that should be used in case of errors
   * @return the parsed int or the default value.
   */
  private int parseInt( final String s, final int defaultVal ) {
    if ( s == null ) {
      return defaultVal;
    }
    try {
      return Integer.parseInt( s );
    } catch ( Exception e ) {
      // ignored ..
    }
    return defaultVal;
  }
}

