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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ElementMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * @noinspection HardCodedStringLiteral
 */
public class ElementReadHandler extends AbstractMetaDataReadHandler {
  private ElementMetaDataBuilder builder;

  private ArrayList<StyleReadHandler> styleHandlers;
  private ArrayList<AttributeReadHandler> attributeHandlers;
  private GlobalMetaDefinition globalMetaDefinition;

  public ElementReadHandler( final GlobalMetaDefinition globalMetaDefinition ) {
    this.globalMetaDefinition = globalMetaDefinition;

    this.attributeHandlers = new ArrayList<AttributeReadHandler>();
    this.styleHandlers = new ArrayList<StyleReadHandler>();

    this.builder = new ElementMetaDataBuilder();
  }

  public ElementMetaDataBuilder getBuilder() {
    return builder;
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
    super.startParsing( attrs );
    getBuilder().namespace( attrs.getValue( getUri(), "namespace" ) ); // NON-NLS
    getBuilder().typeClassification( parseTypeClassification( attrs ) );
    getBuilder().elementType( parseElementType( attrs ) );
    getBuilder().contentType( parseContentType( attrs ) );
    getBuilder().bundle( getBundle(), "element." );
  }

  private Class<?> parseContentType( final Attributes attrs ) throws ParseException {
    final String contentType = attrs.getValue( getUri(), "content-type" );
    if ( contentType == null ) {
      return Object.class;
    } else {
      Class<?> aClass = ObjectUtilities.loadAndValidate( contentType, ElementReadHandler.class, Object.class );
      if ( aClass == null ) {
        return Object.class;
      }
      return aClass;
    }
  }

  private Class<? extends ElementType> parseElementType( final Attributes attrs ) throws ParseException {
    final String elementTypeText = attrs.getValue( getUri(), "implementation" );
    if ( elementTypeText == null ) {
      throw new ParseException( "Attribute 'implementation' is undefined", getLocator() );
    }
    Class<? extends ElementType> c =
        ObjectUtilities.loadAndValidate( elementTypeText, ElementReadHandler.class, ElementType.class );
    if ( c == null ) {
      throw new ParseException( "Attribute 'implementation' is not valid", getLocator() );
    }
    return c;
  }

  private ElementMetaData.TypeClassification parseTypeClassification( final Attributes attrs ) {
    final String eType = attrs.getValue( getUri(), "type-classification" );
    if ( "section".equals( eType ) ) {
      return ElementMetaData.TypeClassification.SECTION;
    } else if ( "data".equals( eType ) ) {
      return ElementMetaData.TypeClassification.DATA;
    } else if ( "control".equals( eType ) ) {
      return ElementMetaData.TypeClassification.CONTROL;
    } else if ( "footer".equals( eType ) ) {
      return ElementMetaData.TypeClassification.FOOTER;
    } else if ( "group-footer".equals( eType ) ) {
      return ElementMetaData.TypeClassification.RELATIONAL_FOOTER;
    } else if ( "header".equals( eType ) ) {
      return ElementMetaData.TypeClassification.HEADER;
    } else if ( "group-header".equals( eType ) ) {
      return ElementMetaData.TypeClassification.RELATIONAL_HEADER;
    } else if ( "subreport".equals( eType ) ) {
      return ElementMetaData.TypeClassification.SUBREPORT;
    }

    if ( "true".equals( attrs.getValue( getUri(), "container" ) ) ) {
      return ElementMetaData.TypeClassification.SECTION;
    } else {
      return ElementMetaData.TypeClassification.DATA;
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
    if ( getUri().equals( uri ) == false ) {
      return null;
    }

    if ( "attribute-group-ref".equals( tagName ) ) {
      return new AttributeGroupRefReadHandler( getBuilder().attributesRef(), globalMetaDefinition );
    } else if ( "style-group-ref".equals( tagName ) ) {
      return new StyleGroupRefReadHandler( getBuilder().stylesRef(), globalMetaDefinition, getBundle() );
    } else if ( "attribute".equals( tagName ) ) {
      final String prefix = "element." + getBuilder().getName() + ".";
      final AttributeReadHandler readHandler = new AttributeReadHandler( getBundle(), prefix );
      attributeHandlers.add( readHandler );
      return readHandler;
    } else if ( "style".equals( tagName ) ) {
      final StyleReadHandler readHandler = new StyleReadHandler( getBundle() );
      styleHandlers.add( readHandler );
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
    for ( int i = 0; i < attributeHandlers.size(); i++ ) {
      final AttributeReadHandler handler = attributeHandlers.get( i );
      AttributeMetaData metaData = handler.getMetaData();
      getBuilder().attribute( metaData );
    }

    for ( int i = 0; i < styleHandlers.size(); i++ ) {
      final StyleReadHandler handler = styleHandlers.get( i );
      StyleMetaData metaData = handler.getMetaData();
      getBuilder().style( metaData );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return new DefaultElementMetaData( getBuilder() );
  }
}
