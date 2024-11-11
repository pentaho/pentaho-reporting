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

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabGroupType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabGroupReadHandler extends AbstractElementReadHandler {
  private GroupHeaderReadHandler headerReadHandler;
  private GroupFooterReadHandler footerReadHandler;

  private CrosstabOtherSubGroupBodyReadHandler otherSubGroupBodyReadHandler;
  private CrosstabRowSubGroupBodyReadHandler rowSubGroupBodyReadHandler;
  private NoDataBandReadHandler noDataBandReadHandler;

  public CrosstabGroupReadHandler() throws ParseException {
    super( CrosstabGroupType.INSTANCE );
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
    if ( BundleNamespaces.LAYOUT.equals( uri ) ) {
      if ( "group-header".equals( tagName ) ) {
        if ( headerReadHandler == null ) {
          headerReadHandler = new GroupHeaderReadHandler();
        }
        return headerReadHandler;
      }
      if ( "group-footer".equals( tagName ) ) {
        if ( footerReadHandler == null ) {
          footerReadHandler = new GroupFooterReadHandler();
        }
        return footerReadHandler;
      }
      if ( "no-data".equals( tagName ) ) {
        noDataBandReadHandler = new NoDataBandReadHandler();
        return noDataBandReadHandler;
      }
      if ( "crosstab-other-group-body".equals( tagName ) ) {
        otherSubGroupBodyReadHandler = new CrosstabOtherSubGroupBodyReadHandler();
        return otherSubGroupBodyReadHandler;
      }
      if ( "crosstab-row-group-body".equals( tagName ) ) {
        rowSubGroupBodyReadHandler = new CrosstabRowSubGroupBodyReadHandler();
        return rowSubGroupBodyReadHandler;
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

    final CrosstabGroup group = getElement();
    if ( headerReadHandler != null ) {
      group.setHeader( headerReadHandler.getElement() );
    }
    if ( footerReadHandler != null ) {
      group.setFooter( footerReadHandler.getElement() );
    }
    if ( noDataBandReadHandler != null ) {
      group.setNoDataBand( noDataBandReadHandler.getElement() );
    }
    if ( rowSubGroupBodyReadHandler != null ) {
      group.setBody( rowSubGroupBodyReadHandler.getElement() );
    } else if ( otherSubGroupBodyReadHandler != null ) {
      group.setBody( otherSubGroupBodyReadHandler.getElement() );
    }
  }

  public CrosstabGroup getElement() {
    return (CrosstabGroup) super.getElement();
  }
}
