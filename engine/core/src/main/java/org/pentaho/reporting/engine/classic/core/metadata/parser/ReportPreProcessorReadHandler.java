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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ReportPreProcessorMetaDataBuilder;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * @noinspection HardCodedStringLiteral
 */
public class ReportPreProcessorReadHandler extends AbstractMetaDataReadHandler {
  private final ReportPreProcessorMetaDataBuilder builder;

  private ArrayList<ReportPreProcessorPropertyReadHandler> attributeHandlers;
  private SharedBeanInfo beanInfo;

  public ReportPreProcessorReadHandler() {
    attributeHandlers = new ArrayList<ReportPreProcessorPropertyReadHandler>();

    builder = new ReportPreProcessorMetaDataBuilder();
  }

  public ReportPreProcessorMetaDataBuilder getBuilder() {
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
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    getBuilder().autoProcess( "true".equals( attrs.getValue( getUri(), "auto-process" ) ) );
    getBuilder().priority( ParserUtil.parseInt( attrs.getValue( getUri(), "priority" ), 0 ) );
    getBuilder().designMode( "true".equals( attrs.getValue( getUri(), "execute-in-design-mode" ) ) );
    getBuilder().impl( parseImpl( attrs ) );
    getBuilder().bundle( getBundle(), "" );
    beanInfo = new SharedBeanInfo( getBuilder().getImpl() );
  }

  private Class<? extends ReportPreProcessor> parseImpl( final Attributes attrs ) throws ParseException {
    final Class<? extends ReportPreProcessor> expressionClass;
    final String valueTypeText = attrs.getValue( getUri(), "class" );
    if ( valueTypeText == null ) {
      throw new ParseException( "Attribute 'class' is undefined", getLocator() );
    }
    try {
      expressionClass =
          ObjectUtilities
              .loadAndValidate( valueTypeText, ReportPreProcessorReadHandler.class, ReportPreProcessor.class );
      if ( ReportPreProcessor.class.isAssignableFrom( expressionClass ) == false ) {
        throw new ParseException( "Attribute 'class' is not valid", getLocator() );
      }
    } catch ( ParseException pe ) {
      throw pe;
    } catch ( Exception e ) {
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
      final ReportPreProcessorPropertyReadHandler readHandler =
          new ReportPreProcessorPropertyReadHandler( beanInfo, getBundle() );
      attributeHandlers.add( readHandler );
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
      final ReportPreProcessorPropertyReadHandler handler = attributeHandlers.get( i );
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
    return new DefaultReportPreProcessorMetaData( getBuilder() );
  }
}
