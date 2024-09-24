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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.BandElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers.AbstractElementReadHandler;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Creation-Date: 20.08.2007, 20:29:52
 *
 * @author Thomas Morgner
 */
public class BandReadHandler extends AbstractElementReadHandler {
  private BandElementFactory bandFactory;
  private ArrayList items;
  private Band band;

  public BandReadHandler() {
    band = new Band();
    this.bandFactory = new BandElementFactory( band );
    this.items = new ArrayList();
  }

  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );
    bandFactory.setName( atts.getValue( getUri(), "id" ) );
  }

  protected ElementFactory getElementFactory() {
    return bandFactory;
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes attrs )
    throws SAXException {
    if ( ObjectUtilities.equal( uri, getUri() ) == false ) {
      return null;
    }
    if ( "band".equals( tagName ) ) {
      final XmlReadHandler readHandler = new BandReadHandler();
      items.add( readHandler );
      return readHandler;
    }

    if ( "element".equals( tagName ) ) {
      final XmlReadHandler readHandler = new ContentElementReadHandler();
      items.add( readHandler );
      return readHandler;
    }

    if ( "rectangle".equals( tagName ) ) {
      final XmlReadHandler readHandler = new RectangleElementReadHandler();
      items.add( readHandler );
      return readHandler;
    }

    if ( "round-rectangle".equals( tagName ) ) {
      final XmlReadHandler readHandler = new RoundRectangleElementReadHandler();
      items.add( readHandler );
      return readHandler;
    }

    if ( "horizontal-line".equals( tagName ) ) {
      final XmlReadHandler readHandler = new HorizontalLineElementReadHandler();
      items.add( readHandler );
      return readHandler;
    }

    if ( "vertical-line".equals( tagName ) ) {
      final XmlReadHandler readHandler = new VerticalLineElementReadHandler();
      items.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    super.doneParsing();
    for ( int i = 0; i < items.size(); i++ ) {
      final XmlReadHandler handler = (XmlReadHandler) items.get( i );
      final Element element = (Element) handler.getObject();
      band.addElement( element );
    }
  }
}
