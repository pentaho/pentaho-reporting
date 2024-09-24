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

import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMapEntry;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class ImageMapReadHandler extends AbstractXmlReadHandler {
  private ImageMap imageMap;
  private ArrayList readHandlerArrayList;

  public ImageMapReadHandler() {
    readHandlerArrayList = new ArrayList();
    imageMap = new ImageMap();
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
      imageMap.setAttribute( namespace, name, attributeValue );
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "area".equals( tagName ) ) {
      final AreaReadHandler readHandler = new AreaReadHandler();
      readHandlerArrayList.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < readHandlerArrayList.size(); i++ ) {
      final AreaReadHandler readHandler = (AreaReadHandler) readHandlerArrayList.get( i );
      imageMap.addMapEntry( (ImageMapEntry) readHandler.getObject() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return imageMap;
  }
}
