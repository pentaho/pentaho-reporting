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
