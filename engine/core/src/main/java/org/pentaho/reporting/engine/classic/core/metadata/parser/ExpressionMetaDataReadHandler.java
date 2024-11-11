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


package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class ExpressionMetaDataReadHandler extends AbstractXmlReadHandler {
  private ArrayList<ExpressionReadHandler> elements;
  private ExpressionMetaDataCollection typeCollection;

  public ExpressionMetaDataReadHandler() {
    elements = new ArrayList<ExpressionReadHandler>();
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
    if ( getUri().equals( uri ) == false ) {
      return null;
    }
    if ( "expression".equals( tagName ) ) {
      final ExpressionReadHandler readHandler = new ExpressionReadHandler();
      elements.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final ExpressionMetaData[] result = new ExpressionMetaData[elements.size()];
    for ( int i = 0; i < elements.size(); i++ ) {
      final ExpressionReadHandler handler = elements.get( i );
      result[i] = (ExpressionMetaData) handler.getObject();
    }

    typeCollection = new ExpressionMetaDataCollection( result );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return typeCollection;
  }
}
