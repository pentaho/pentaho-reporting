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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.settings;

import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigurationReadHandler extends AbstractXmlReadHandler {
  private ModifiableConfiguration configuration;
  private HashMap fieldHandlers;

  public ConfigurationReadHandler( final ModifiableConfiguration configuration ) {
    this.configuration = configuration;
    this.fieldHandlers = new HashMap();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the namespace.
   * @param tagName
   *          the tag name.
   * @param attrs
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes attrs )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "property".equals( tagName ) ) {
      final String name = attrs.getValue( getUri(), "name" );
      if ( name == null ) {
        throw new SAXException( "Required attribute 'name' is missing." );
      }

      final StringReadHandler readHandler = new StringReadHandler();
      fieldHandlers.put( name, readHandler );
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
    final Iterator it = fieldHandlers.entrySet().iterator();
    while ( it.hasNext() ) {
      final Map.Entry entry = (Map.Entry) it.next();
      final String key = (String) entry.getKey();
      final StringReadHandler readHandler = (StringReadHandler) entry.getValue();
      configuration.setConfigProperty( key, readHandler.getResult() );
    }
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return configuration;
  }
}
