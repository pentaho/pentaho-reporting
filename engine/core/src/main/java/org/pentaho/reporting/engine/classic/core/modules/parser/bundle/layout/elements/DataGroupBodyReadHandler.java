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

import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataGroupBodyReadHandler extends AbstractElementReadHandler {
  private ItemBandReadHandler itemBandReadHandler;
  private NoDataBandReadHandler noDataBandReadHandler;
  private DetailsHeaderBandReadHandler detailsHeaderBandReadHandler;
  private DetailsFooterBandReadHandler detailsFooterBandReadHandler;

  public DataGroupBodyReadHandler() throws ParseException {
    super( GroupDataBodyType.INSTANCE );
  }

  public GroupDataBody getElement() {
    return (GroupDataBody) super.getElement();
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

    if ( isSameNamespace( uri ) ) {
      if ( "details".equals( tagName ) ) {
        if ( itemBandReadHandler == null ) {
          itemBandReadHandler = new ItemBandReadHandler();
        }
        return itemBandReadHandler;
      }
      if ( "details-header".equals( tagName ) ) {
        if ( detailsHeaderBandReadHandler == null ) {
          detailsHeaderBandReadHandler = new DetailsHeaderBandReadHandler();
        }
        return detailsHeaderBandReadHandler;
      }
      if ( "details-footer".equals( tagName ) ) {
        if ( detailsFooterBandReadHandler == null ) {
          detailsFooterBandReadHandler = new DetailsFooterBandReadHandler();
        }
        return detailsFooterBandReadHandler;
      }
      if ( "no-data".equals( tagName ) ) {
        if ( noDataBandReadHandler == null ) {
          noDataBandReadHandler = new NoDataBandReadHandler();
        }
        return noDataBandReadHandler;
      }
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
    final GroupDataBody body = getElement();
    if ( noDataBandReadHandler != null ) {
      body.setNoDataBand( noDataBandReadHandler.getElement() );
    }
    if ( itemBandReadHandler != null ) {
      body.setItemBand( itemBandReadHandler.getElement() );
    }
    if ( detailsFooterBandReadHandler != null ) {
      body.setDetailsFooter( detailsFooterBandReadHandler.getElement() );
    }
    if ( detailsHeaderBandReadHandler != null ) {
      body.setDetailsHeader( detailsHeaderBandReadHandler.getElement() );
    }
  }
}
