/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class DrillDownGroupReadHandler extends AbstractXmlReadHandler {
  private ArrayList<DrillDownProfileReadHandler> elements;
  private String groupName;
  private DrillDownProfile[] result;

  public DrillDownGroupReadHandler() {
    elements = new ArrayList<DrillDownProfileReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    groupName = attrs.getValue( getUri(), "name" );
    if ( groupName == null ) {
      throw new ParseException( "Mandatory attribute 'name' is missing", getLocator() );
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( getUri().equals( uri ) == false ) {
      return null;
    }
    if ( "drilldown-profile".equals( tagName ) ) {
      final DrillDownProfileReadHandler readHandler = new DrillDownProfileReadHandler( groupName );
      elements.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final DrillDownProfile[] result = new DrillDownProfile[ elements.size() ];
    for ( int i = 0; i < elements.size(); i++ ) {
      final DrillDownProfileReadHandler handler = elements.get( i );
      result[ i ] = (DrillDownProfile) handler.getObject();
    }
    this.result = result;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return result;
  }
}
