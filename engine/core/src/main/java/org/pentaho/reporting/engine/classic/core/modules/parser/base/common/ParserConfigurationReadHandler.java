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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParserConfigurationReadHandler extends AbstractPropertyXmlReadHandler {
  private HashMap fieldHandlers;

  public ParserConfigurationReadHandler() {
    this.fieldHandlers = new HashMap();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( getUri().equals( uri ) == false ) {
      return null;
    }

    if ( "property".equals( tagName ) ) {
      final String name = atts.getValue( getUri(), "name" );
      if ( name == null ) {
        throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
      }

      final PropertyReferenceReadHandler readHandler = new PropertyReferenceReadHandler();
      fieldHandlers.put( name, readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final Iterator it = fieldHandlers.entrySet().iterator();
    while ( it.hasNext() ) {
      final Map.Entry entry = (Map.Entry) it.next();
      final String key = (String) entry.getKey();
      if ( key.startsWith( "::" ) ) {
        throw new ParseException( "The key value '" + key
            + "' is invalid. Internal keys (starting with '::') cannot be redefined.", getLocator() );
      }
      final PropertyReferenceReadHandler readHandler = (PropertyReferenceReadHandler) entry.getValue();
      getRootHandler().setHelperObject( key, readHandler.getObject() );
    }
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
