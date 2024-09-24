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

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabColumnSubGroupBodyReadHandler extends AbstractElementReadHandler {
  private CrosstabColumnGroupReadHandler groupColumnReadHandler;

  public CrosstabColumnSubGroupBodyReadHandler() throws ParseException {
    super( CrosstabColumnGroupBodyType.INSTANCE );
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
      if ( "crosstab-column-group".equals( tagName ) ) {
        groupColumnReadHandler = new CrosstabColumnGroupReadHandler();
        return groupColumnReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  public CrosstabColumnGroupBody getElement() {
    return (CrosstabColumnGroupBody) super.getElement();
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();

    final CrosstabColumnGroupBody body = getElement();
    if ( groupColumnReadHandler != null ) {
      body.setGroup( groupColumnReadHandler.getElement() );
    } else {
      throw new ParseException( "A 'crosstab-column-group' element must be present" );
    }
  }
}
