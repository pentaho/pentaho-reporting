/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
