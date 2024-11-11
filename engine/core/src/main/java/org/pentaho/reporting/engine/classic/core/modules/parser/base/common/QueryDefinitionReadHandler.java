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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class QueryDefinitionReadHandler extends AbstractXmlReadHandler {
  private String name;
  private PropertyReadHandler script;
  private StringReadHandler query;

  public QueryDefinitionReadHandler() {
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( StringUtils.isEmpty( name ) ) {
      throw new ParseException( "Attribute 'name' is not defined.", getLocator() );
    }
    super.startParsing( attrs );
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "static-query".equals( tagName ) ) {
      query = new StringReadHandler();
      return query;
    }
    if ( "script".equals( tagName ) ) {
      script = new PropertyReadHandler( "language", false );
      return script;
    }

    return null;
  }

  public Object getObject() throws SAXException {
    return null;
  }

  public String getName() {
    return name;
  }

  public String getScriptLanguage() {
    if ( script == null ) {
      return null;
    }
    return script.getName();
  }

  public String getScript() {
    if ( script == null ) {
      return null;
    }
    return script.getResult();
  }

  public String getQuery() {
    if ( query == null ) {
      return null;
    }
    return query.getResult();
  }
}
