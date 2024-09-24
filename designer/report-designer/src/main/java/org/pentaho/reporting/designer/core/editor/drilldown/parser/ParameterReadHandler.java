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

package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import org.pentaho.reporting.designer.core.editor.drilldown.model.Parameter;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010 Time: 17:34:04
 *
 * @author Thomas Morgner.
 */
public class ParameterReadHandler extends AbstractXmlReadHandler {
  private Parameter parameter;
  private ArrayList<ParameterAttributeReadHandler> attributeReadHandlers;
  private ParameterValuesReadHandler valuesReadHandler;

  public ParameterReadHandler() {
    attributeReadHandlers = new ArrayList<ParameterAttributeReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String name = attrs.getValue( getUri(), "name" );// NON-NLS
    parameter = new Parameter( name );
    parameter.setMandatory( "true".equals( attrs.getValue( getUri(), "is-mandatory" ) ) );// NON-NLS
    parameter.setStrict( "true".equals( attrs.getValue( getUri(), "is-strict" ) ) );// NON-NLS
    parameter.setMultiSelect( "true".equals( attrs.getValue( getUri(), "is-multi-select" ) ) );// NON-NLS
    parameter.setType( attrs.getValue( getUri(), "type" ) );// NON-NLS
    parameter.setTimezoneHint( attrs.getValue( getUri(), "timezone-hint" ) );// NON-NLS
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "attribute".equals( tagName ) )//NON-NLS
    {
      final ParameterAttributeReadHandler readHandler = new ParameterAttributeReadHandler();
      attributeReadHandlers.add( readHandler );
      return readHandler;
    }
    if ( "values".equals( tagName ) )//NON-NLS
    {
      if ( valuesReadHandler == null ) {
        valuesReadHandler = new ParameterValuesReadHandler( parameter.getType() );
      }
      return valuesReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < attributeReadHandlers.size(); i++ ) {
      final ParameterAttributeReadHandler readHandler = attributeReadHandlers.get( i );
      parameter.setAttribute( readHandler.getNamespace(), readHandler.getName(), readHandler.getValue() );
    }
    if ( valuesReadHandler != null ) {
      parameter.setSelections( valuesReadHandler.getSelections() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return parameter;
  }

  public Parameter getParameter() {
    return parameter;
  }
}
