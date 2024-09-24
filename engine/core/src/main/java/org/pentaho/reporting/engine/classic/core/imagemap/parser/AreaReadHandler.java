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

package org.pentaho.reporting.engine.classic.core.imagemap.parser;

import org.pentaho.reporting.engine.classic.core.imagemap.AbstractImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.CircleImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.DefaultImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.PolygonImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.RectangleImageMapEntry;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.StringTokenizer;

public class AreaReadHandler extends AbstractXmlReadHandler {
  private AbstractImageMapEntry mapEntry;
  private static final float[] EMPTY_FLOATS = new float[0];

  public AreaReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    final String shape = attrs.getValue( getUri(), "shape" );
    final String coordinates = attrs.getValue( getUri(), "coords" );
    final float[] coords = parseFloatArray( coordinates );
    mapEntry = createMapEntry( shape, coords );

    final int length = attrs.getLength();
    for ( int i = 0; i < length; i++ ) {
      if ( "xmlns".equals( attrs.getQName( i ) ) || attrs.getQName( i ).startsWith( "xmlns:" ) ) {
        // workaround for buggy parsers
        continue;
      }
      final String name = attrs.getLocalName( i );
      if ( name.indexOf( ':' ) > -1 ) {
        // attribute with ':' are not valid and indicate a namespace definition or so
        continue;
      }
      final String namespace = attrs.getURI( i );
      final String attributeValue = attrs.getValue( i );

      if ( isSameNamespace( namespace ) ) {
        if ( "shape".equals( name ) ) {
          continue;
        }
        if ( "coords".equals( name ) ) {
          continue;
        }
      }
      mapEntry.setAttribute( namespace, name, attributeValue );
    }
  }

  private AbstractImageMapEntry createMapEntry( final String type, final float[] coordinates ) throws ParseException {
    if ( "rect".equals( type ) ) {
      if ( coordinates.length != 4 ) {
        throw new ParseException( "Rect-shape needs four coordinate-values", getLocator() );
      }
      return new RectangleImageMapEntry( coordinates[0], coordinates[1], coordinates[2], coordinates[3] );
    }
    if ( "circle".equals( type ) ) {
      if ( coordinates.length != 3 ) {
        throw new ParseException( "Circle-shape needs three coordinate-values", getLocator() );
      }
      return new CircleImageMapEntry( coordinates[0], coordinates[1], coordinates[2] );
    }
    if ( "poly".equals( type ) ) {
      if ( ( coordinates.length % 2 ) != 0 ) {
        throw new ParseException( "Polygon-shape needs an even number of coordinate-values", getLocator() );
      }
      return new PolygonImageMapEntry( coordinates );
    }

    if ( "default".equals( type ) ) {
      return new DefaultImageMapEntry();
    }

    if ( coordinates.length != 4 ) {
      throw new ParseException( "Implied Rect-shape needs four coordinate-values", getLocator() );
    }
    return new RectangleImageMapEntry( coordinates[0], coordinates[1], coordinates[2], coordinates[3] );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return mapEntry;
  }

  /**
   * Converts the given string into a array of <code>BigDecimal</code> numbers using the given separator as splitting
   * argument.<br/>
   * Take care that <code>BigDecimal</code> string constructor do not support inputs like "10f", "5d" ...
   *
   * @param s
   *          the string to be converted.
   * @return the array of numbers produced from the string.
   * @throws org.pentaho.reporting.libraries.xmlns.parser.ParseException
   *           if the string <code>s</code> does not contain valid numbers.
   */
  private float[] parseFloatArray( final String s ) throws ParseException {
    if ( StringUtils.isEmpty( s ) ) {
      return EMPTY_FLOATS;
    }

    try {
      final StringTokenizer stringTokenizer = new StringTokenizer( s, "," );
      final float[] ret = new float[stringTokenizer.countTokens()];

      int i = 0;
      while ( stringTokenizer.hasMoreTokens() ) {
        final String val = stringTokenizer.nextToken().trim();
        ret[i] = Float.parseFloat( val );
        i += 1;
      }

      return ret;
    } catch ( final NumberFormatException nfe ) {
      // re-throw the exception
      throw new ParseException( "Unable to convert array-text to real array.", getLocator() );
    }
  }
}
