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

import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabOtherSubGroupBodyReadHandler extends AbstractElementReadHandler {
  private CrosstabOtherGroupReadHandler groupOtherReadHandler;

  public CrosstabOtherSubGroupBodyReadHandler() throws ParseException {
    super( CrosstabOtherGroupBodyType.INSTANCE );
  }

  public CrosstabOtherGroupBody getElement() {
    return (CrosstabOtherGroupBody) super.getElement();
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
      // Handle the subgroup
      if ( "crosstab-other-group".equals( tagName ) ) {
        groupOtherReadHandler = new CrosstabOtherGroupReadHandler();
        return groupOtherReadHandler;
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

    final CrosstabOtherGroupBody body = getElement();
    if ( groupOtherReadHandler != null ) {
      body.setGroup( groupOtherReadHandler.getElement() );
    } else {
      throw new ParseException( "Either a 'crosstab-other-group' element must be present" );
    }
  }
}
