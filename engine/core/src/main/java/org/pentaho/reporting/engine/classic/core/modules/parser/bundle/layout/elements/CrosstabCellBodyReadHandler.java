/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class CrosstabCellBodyReadHandler extends AbstractElementReadHandler {
  private ArrayList<CrosstabCellReadHandler> crosstabCellReadHandlers;
  private DetailsHeaderBandReadHandler detailsHeaderReadHandler;

  public CrosstabCellBodyReadHandler() throws ParseException {
    super( CrosstabCellBodyType.INSTANCE );
    crosstabCellReadHandlers = new ArrayList<CrosstabCellReadHandler>();
  }

  public CrosstabCellBody getElement() {
    return (CrosstabCellBody) super.getElement();
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

    if ( isSameNamespace( uri ) ) {
      if ( "details-header".equals( tagName ) ) {
        detailsHeaderReadHandler = new DetailsHeaderBandReadHandler();
        return detailsHeaderReadHandler;
      }
      if ( "crosstab-cell".equals( tagName ) ) {
        final CrosstabCellReadHandler readHandler = new CrosstabCellReadHandler();
        crosstabCellReadHandlers.add( readHandler );
        return readHandler;
      }
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final CrosstabCellBody body = getElement();
    if ( detailsHeaderReadHandler != null ) {
      body.setHeader( detailsHeaderReadHandler.getElement() );
    }
    for ( int i = 0; i < crosstabCellReadHandlers.size(); i++ ) {
      final CrosstabCellReadHandler readHandler = crosstabCellReadHandlers.get( i );
      body.addElement( readHandler.getElement() );
    }
  }
}
