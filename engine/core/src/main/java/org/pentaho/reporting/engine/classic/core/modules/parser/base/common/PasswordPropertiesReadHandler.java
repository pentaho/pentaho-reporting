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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PasswordPropertiesReadHandler extends PropertiesReadHandler {
  public PasswordPropertiesReadHandler() {
  }

  public PasswordPropertiesReadHandler( final String propertyTagName ) {
    super( propertyTagName );
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
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( tagName.equals( getPropertyTagName() ) ) {
      final String attrName = atts.getValue( "name" );
      final PropertyReadHandler prh;
      if ( attrName != null && attrName.toLowerCase().contains( "password" ) ) {
        prh = new PasswordPropertyReadHandler();
      } else {
        prh = new PropertyReadHandler();
      }
      getPropertyHandlers().add( prh );
      return prh;
    }
    return null;
  }

}
