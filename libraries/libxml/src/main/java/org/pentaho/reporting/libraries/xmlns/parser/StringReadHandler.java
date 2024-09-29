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


package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A XmlReadHandler that reads character-data for the given element.
 *
 * @author Thomas Morgner
 */
public class StringReadHandler extends AbstractXmlReadHandler {

  /**
   * A buffer containing the characters read so far.
   */
  private StringBuffer buffer;

  /**
   * The string under construction.
   */
  private String result;

  /**
   * Creates a new handler.
   */
  public StringReadHandler() {
    super();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs )
    throws SAXException {
    this.buffer = new StringBuffer();
  }

  /**
   * This method is called to process the character data between element tags.
   *
   * @param ch     the character buffer.
   * @param start  the start index.
   * @param length the length.
   * @throws SAXException if there is a parsing error.
   */
  public void characters( final char[] ch, final int start, final int length )
    throws SAXException {
    this.buffer.append( ch, start, length );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing()
    throws SAXException {
    this.result = this.buffer.toString();
    this.buffer = null;
  }

  /**
   * Returns the result as string.
   *
   * @return the parse-result as string.
   */
  public String getResult() {
    return result;
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return getResult();
  }
}
