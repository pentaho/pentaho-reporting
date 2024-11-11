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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.BandType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class BandReadHandler extends AbstractElementReadHandler {
  private ArrayList<ElementReadHandler> elementHandlers;

  protected BandReadHandler( final ElementType elementType ) throws ParseException {
    this( elementType, true );
  }

  protected BandReadHandler( final ElementType elementType, final boolean autoCreate ) throws ParseException {
    elementHandlers = new ArrayList<ElementReadHandler>();
    if ( autoCreate ) {
      initialize( elementType );
    }
  }

  public BandReadHandler() throws ParseException {
    this( BandType.INSTANCE );
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
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {

    final ElementReadHandler handler = BundleElementRegistry.getInstance().getReadHandler( uri, tagName, getLocator() );
    if ( handler != null ) {
      elementHandlers.add( handler );
      return handler;
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();

    final Band band = getElement();
    for ( int i = 0; i < elementHandlers.size(); i++ ) {
      final ElementReadHandler readHandler = elementHandlers.get( i );
      band.addElement( readHandler.getElement() );
    }
  }

  public Band getElement() {
    return (Band) super.getElement();
  }
}
