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

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ExpressionMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * @noinspection HardCodedStringLiteral
 */
public class ExpressionReadHandler extends AbstractMetaDataReadHandler {
  private ArrayList<ExpressionPropertyReadHandler> propertyHandlers;
  private SharedBeanInfo beanInfo;
  private ExpressionMetaDataBuilder builder;

  public ExpressionReadHandler() {
    propertyHandlers = new ArrayList<ExpressionPropertyReadHandler>();
    builder = new ExpressionMetaDataBuilder();
  }

  public ExpressionMetaDataBuilder getBuilder() {
    return builder;
  }

  protected boolean isDerivedName() {
    return true;
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

    getBuilder().layoutComputation( parseLayoutProcessorMode( attrs ) );
    getBuilder().impl( parseExpressionImpl( attrs ) );
    getBuilder().resultType( parseResultType( attrs ) );
    getBuilder().bundle( getBundle(), "" );
    this.beanInfo = new SharedBeanInfo( getBuilder().getImpl() );
  }

  private Class<?> parseResultType( final Attributes attrs ) throws ParseException {
    final Class<?> resultType;
    final String resultTypeText = attrs.getValue( getUri(), "result" );
    if ( resultTypeText == null ) {
      throw new ParseException( "Attribute 'result' is undefined", getLocator() );
    }
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( ExpressionReadHandler.class );
      resultType = Class.forName( resultTypeText, false, loader );
    } catch ( final Exception e ) {
      throw new ParseException( "Attribute 'result' is not valid", e, getLocator() );
    }
    return resultType;
  }

  private int parseLayoutProcessorMode( final Attributes attrs ) {
    final String layoutProcessorMode = attrs.getValue( getUri(), "layout-processor-mode" );
    int layoutComputation;
    if ( "global".equals( layoutProcessorMode ) ) {
      layoutComputation = DefaultExpressionMetaData.GLOBAL_LAYOUT_PROCESSOR;
    } else if ( "element".equals( layoutProcessorMode ) ) {
      layoutComputation = DefaultExpressionMetaData.ELEMENT_LAYOUT_PROCESSOR;
    } else {
      layoutComputation = DefaultExpressionMetaData.NO_LAYOUT_PROCESSOR;
    }
    return layoutComputation;
  }

  private Class<? extends Expression> parseExpressionImpl( final Attributes attrs ) throws ParseException {
    final String implText = attrs.getValue( getUri(), "class" );
    Class<? extends Expression> expressionClass;
    if ( implText == null ) {
      throw new ParseException( "Attribute 'class' is undefined", getLocator() );
    }
    try {
      expressionClass = ObjectUtilities.loadAndValidate( implText, ExpressionReadHandler.class, Expression.class );
      if ( expressionClass == null ) {
        throw new ParseException( "Attribute 'class' is not valid", getLocator() );
      }
    } catch ( final ParseException pe ) {
      throw pe;
    } catch ( final Exception e ) {
      throw new ParseException( "Attribute 'class' is not valid", e, getLocator() );
    }
    return expressionClass;
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

    if ( "property".equals( tagName ) ) {
      final ExpressionPropertyReadHandler readHandler = new ExpressionPropertyReadHandler( beanInfo, getBundle() );
      propertyHandlers.add( readHandler );
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
    for ( int i = 0; i < propertyHandlers.size(); i++ ) {
      final ExpressionPropertyReadHandler handler = propertyHandlers.get( i );
      getBuilder().property( handler.getObject() );
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
    return new DefaultExpressionMetaData( getBuilder() );
  }
}
