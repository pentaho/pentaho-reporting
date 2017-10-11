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
* Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor.util;

import org.pentaho.reporting.libraries.base.util.XMLParserFactoryProducer;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Some utility methods to help parsing when using a DOM parser.
 *
 * @author Thomas Morgner
 */
public final class DOMUtilities {
  /**
   * An instance of the XML character entity parser.
   */
  private static final CharacterEntityParser XML_ENTITIES =
    CharacterEntityParser.createXMLEntityParser();

  /**
   * Hidden default constructor.
   */
  private DOMUtilities() {
  }

  /**
   * Parses the given input stream to form a document.
   *
   * @param instream the input stream that should be parsed.
   * @return the parsed document or <code>null</code>, when an error occured
   * @throws ParserConfigurationException if the parser could not be initalized.
   * @throws SAXException                 if the parsing failed due to errors in the xml document
   * @throws IOException                  if reading from the input stream failed.
   */
  public static Document parseInputStream( final InputStream instream )
    throws ParserConfigurationException, SAXException, IOException {
    final DocumentBuilderFactory dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
    final DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse( new InputSource( instream ) );
  }

  /**
   * extracts all text-elements of a particular element and returns an single string containing the contents of all
   * textelements and all character entity nodes. If a node is not known to the parser, its string value will be
   * delivered as <code>&entityname;</code>.
   *
   * @param e the element which is direct parent of all to be extracted textnodes.
   * @return the extracted String
   */
  public static String getText( final Element e ) {
    final NodeList nl = e.getChildNodes();
    final StringBuilder result = new StringBuilder( 100 );

    for ( int i = 0; i < nl.getLength(); i++ ) {
      final Node n = nl.item( i );
      if ( n.getNodeType() == Node.TEXT_NODE ) {
        final Text text = (Text) n;

        result.append( text.getData() );
      } else if ( n.getNodeType() == Node.ENTITY_REFERENCE_NODE ) {
        result.append( '&' );
        result.append( n.getNodeName() );
        result.append( ';' );
      }
    }
    return XML_ENTITIES.decodeEntities( result.toString() );
  }

}
