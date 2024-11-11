/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ScriptableDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ConfigReadHandler configReadHandler;
  private ArrayList<PropertyReadHandler> queries;
  private ScriptableDataFactory dataFactory;

  public ScriptableDataSourceReadHandler() {
    queries = new ArrayList<PropertyReadHandler>();
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
    if ( !isSameNamespace( uri ) ) {
      return null;
    }
    if ( "config".equals( tagName ) ) {
      configReadHandler = new ConfigReadHandler();
      return configReadHandler;
    }

    if ( "query".equals( tagName ) ) {
      final PropertyReadHandler queryReadHandler = new PropertyReadHandler();
      queries.add( queryReadHandler );
      return queryReadHandler;
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
    final ScriptableDataFactory srdf = new ScriptableDataFactory();
    if ( configReadHandler == null ) {
      throw new ParseException( "Required element 'config' is missing.", getLocator() );
    }

    srdf.setLanguage( configReadHandler.getLanguage() );
    if ( !StringUtils.isEmpty( configReadHandler.getScript() ) ) {
      srdf.setScript( configReadHandler.getScript() );
    }
    if ( !StringUtils.isEmpty( configReadHandler.getShutdownScript() ) ) {
      srdf.setShutdownScript( configReadHandler.getShutdownScript() );
    }
    for ( int i = 0; i < queries.size(); i++ ) {
      final PropertyReadHandler handler = queries.get( i );
      srdf.setQuery( handler.getName(), handler.getResult() );
    }
    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occurred.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
