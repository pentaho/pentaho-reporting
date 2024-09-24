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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class AttributeGroupReadHandler extends AbstractXmlReadHandler {
  private ArrayList<AttributeReadHandler> attributeHandlers;
  private String name;
  private GlobalMetaDefinition attributeGroups;
  private AttributeGroup attributeGroup;
  private String bundle;

  public AttributeGroupReadHandler( final GlobalMetaDefinition attributeGroups ) {
    this.attributeGroups = attributeGroups;
    this.attributeHandlers = new ArrayList<AttributeReadHandler>();
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
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Attribute 'name' is undefined", getLocator() );
    }

    bundle = attrs.getValue( getUri(), "bundle-name" );
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
    if ( "attribute".equals( tagName ) ) {
      final AttributeReadHandler handler = new AttributeReadHandler( bundle, "" );
      attributeHandlers.add( handler );
      return handler;
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
    final AttributeReadHandler[] attributes =
        attributeHandlers.toArray( new AttributeReadHandler[attributeHandlers.size()] );
    final AttributeDefinition[] definitions = new AttributeDefinition[attributeHandlers.size()];
    for ( int i = 0; i < attributes.length; i++ ) {
      definitions[i] = attributes[i].getObject();
    }
    attributeGroup = new AttributeGroup( name, definitions );
    attributeGroups.addAttributeGroup( attributeGroup );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return attributeGroup;
  }
}
