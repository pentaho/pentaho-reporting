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


package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A handler for reading an XML element.
 *
 * @author Thomas Morgner
 */
public interface XmlReadHandler {

  /**
   * This method is called at the start of an element.
   *
   * @param uri     the namespace uri.
   * @param tagName the tag name.
   * @param attrs   the attributes.
   * @throws SAXException if there is a parsing error.
   */
  public void startElement( String uri, String tagName, Attributes attrs )
    throws SAXException;

  /**
   * This method is called to process the character data between element tags.
   *
   * @param ch     the character buffer.
   * @param start  the start index.
   * @param length the length.
   * @throws SAXException if there is a parsing error.
   */
  public void characters( char[] ch, int start, int length )
    throws SAXException;

  /**
   * This method is called at the end of an element.
   *
   * @param uri     the namespace uri.
   * @param tagName the tag name.
   * @throws SAXException if there is a parsing error.
   */
  public void endElement( String uri, String tagName )
    throws SAXException;

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException;

  /**
   * Initialise.
   *
   * @param rootHandler the root handler.
   * @param uri         the namespace uri.
   * @param tagName     the tag name.
   * @throws SAXException if an parser-error occured.
   */
  public void init( RootXmlReadHandler rootHandler, String uri, String tagName )
    throws SAXException;

}
