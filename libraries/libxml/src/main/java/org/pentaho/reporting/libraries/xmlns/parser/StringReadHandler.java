/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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
