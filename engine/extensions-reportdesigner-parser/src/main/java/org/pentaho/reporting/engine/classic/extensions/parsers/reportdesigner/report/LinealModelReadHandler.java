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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.model.Guideline;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class LinealModelReadHandler extends AbstractXmlReadHandler {
  private ArrayList guidelines;
  private Guideline[] guidelineValues;

  public LinealModelReadHandler() {
    guidelines = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "guideLine".equals( tagName ) ) {
      final GuidelineReadHandler readHandler = new GuidelineReadHandler();
      guidelines.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    guidelineValues = new Guideline[ guidelines.size() ];
    for ( int i = 0; i < guidelines.size(); i++ ) {
      final GuidelineReadHandler handler = (GuidelineReadHandler) guidelines.get( i );
      guidelineValues[ i ] = (Guideline) handler.getObject();
    }
  }

  public Guideline[] getGuidelineValues() {
    return guidelineValues;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return guidelineValues;
  }
}
