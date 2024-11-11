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

import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabRowSubGroupBodyReadHandler extends AbstractElementReadHandler {
  private CrosstabRowGroupReadHandler groupRowReadHandler;

  public CrosstabRowSubGroupBodyReadHandler() throws ParseException {
    super( CrosstabRowGroupBodyType.INSTANCE );
  }

  public CrosstabRowGroupBody getElement() {
    return (CrosstabRowGroupBody) super.getElement();
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
      if ( "crosstab-row-group".equals( tagName ) ) {
        groupRowReadHandler = new CrosstabRowGroupReadHandler();
        return groupRowReadHandler;
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

    final CrosstabRowGroupBody body = getElement();
    if ( groupRowReadHandler != null ) {
      body.setGroup( groupRowReadHandler.getElement() );
    } else {
      throw new ParseException( "Either a 'crosstab-row-group' element must be present" );
    }
  }
}
