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

import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabOtherGroupReadHandler extends AbstractElementReadHandler {
  private GroupHeaderReadHandler headerReadHandler;
  private GroupFooterReadHandler footerReadHandler;
  private StringReadHandler fieldReadHandler;
  private CrosstabOtherSubGroupBodyReadHandler otherGroupBodyReadHandler;
  private CrosstabRowSubGroupBodyReadHandler rowGroupBodyReadHandler;

  public CrosstabOtherGroupReadHandler() throws ParseException {
    super( CrosstabOtherGroupType.INSTANCE );
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
      if ( "field".equals( tagName ) ) {
        if ( fieldReadHandler == null ) {
          fieldReadHandler = new StringReadHandler();
        }
        return fieldReadHandler;
      }
      if ( "group-footer".equals( tagName ) ) {
        if ( footerReadHandler == null ) {
          footerReadHandler = new GroupFooterReadHandler();
        }
        return footerReadHandler;
      }
      if ( "crosstab-other-group-body".equals( tagName ) ) {
        otherGroupBodyReadHandler = new CrosstabOtherSubGroupBodyReadHandler();
        return otherGroupBodyReadHandler;
      }
      if ( "crosstab-row-group-body".equals( tagName ) ) {
        rowGroupBodyReadHandler = new CrosstabRowSubGroupBodyReadHandler();
        return rowGroupBodyReadHandler;
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

    final CrosstabOtherGroup group = getElement();
    if ( fieldReadHandler != null ) {
      group.setField( fieldReadHandler.getResult() );
    }
    if ( headerReadHandler != null ) {
      group.setHeader( headerReadHandler.getElement() );
    }
    if ( footerReadHandler != null ) {
      group.setFooter( footerReadHandler.getElement() );
    }
    if ( rowGroupBodyReadHandler != null ) {
      group.setBody( rowGroupBodyReadHandler.getElement() );
    } else if ( otherGroupBodyReadHandler != null ) {
      group.setBody( otherGroupBodyReadHandler.getElement() );
    }
  }

  public CrosstabOtherGroup getElement() {
    return (CrosstabOtherGroup) super.getElement();
  }
}
