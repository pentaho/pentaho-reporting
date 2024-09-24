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

import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubGroupBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubGroupBodyReadHandler extends AbstractElementReadHandler {
  private RelationalGroupReadHandler groupReadHandler;
  private CrosstabGroupReadHandler crosstabGroupReadHandler;

  public SubGroupBodyReadHandler() throws ParseException {
    super( SubGroupBodyType.INSTANCE );
  }

  public SubGroupBody getElement() {
    return (SubGroupBody) super.getElement();
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
      if ( "group".equals( tagName ) ) {
        // Handle the subgroup
        groupReadHandler = new RelationalGroupReadHandler();
        return groupReadHandler;
      }
      if ( "crosstab".equals( tagName ) ) {
        // Handle the subgroup
        crosstabGroupReadHandler = new CrosstabGroupReadHandler();
        return crosstabGroupReadHandler;
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

    final SubGroupBody body = getElement();
    if ( groupReadHandler != null ) {
      body.setGroup( groupReadHandler.getElement() );
    } else if ( crosstabGroupReadHandler != null ) {
      body.setGroup( crosstabGroupReadHandler.getElement() );
    } else {
      throw new ParseException( "Mandatory element 'group' or 'crosstab' was not found." );
    }

  }
}
