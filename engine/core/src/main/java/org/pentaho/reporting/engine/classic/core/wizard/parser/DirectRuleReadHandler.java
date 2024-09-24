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

package org.pentaho.reporting.engine.classic.core.wizard.parser;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeReference;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeReferences;
import org.pentaho.reporting.engine.classic.core.wizard.DirectFieldSelectorRule;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class DirectRuleReadHandler extends AbstractXmlReadHandler {
  private String field;
  private DirectFieldSelectorRule rule;
  private RuleMetaAttributesReadHandler attributesReadHandler;
  private ArrayList mappings;

  public DirectRuleReadHandler() {
    this.mappings = new ArrayList();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    field = attrs.getValue( getUri(), "field" );
    if ( field == null ) {
      throw new ParseException( "Required attribute 'field' is missing.", getLocator() );
    }
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
      if ( "data-attributes".equals( tagName ) ) {
        attributesReadHandler = new RuleMetaAttributesReadHandler();
        return attributesReadHandler;
      }
      if ( "data-attribute-mapping".equals( tagName ) ) {
        final DataAttributeMappingReadHandler readHandler = new DataAttributeMappingReadHandler();
        mappings.add( readHandler );
        return readHandler;
      }
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
    if ( attributesReadHandler == null ) {
      throw new SAXException( "Mandatory element 'column-attributes' is missing." );
    }

    final DataAttributes attributes = (DataAttributes) attributesReadHandler.getObject();
    final DefaultDataAttributeReferences references = new DefaultDataAttributeReferences();
    for ( int i = 0; i < mappings.size(); i++ ) {
      final DataAttributeMappingReadHandler handler = (DataAttributeMappingReadHandler) mappings.get( i );
      references.setReference( handler.getTargetDomain(), handler.getTargetName(), (DataAttributeReference) handler
          .getObject() );
    }

    rule = new DirectFieldSelectorRule( field, attributes, references );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return rule;
  }
}
