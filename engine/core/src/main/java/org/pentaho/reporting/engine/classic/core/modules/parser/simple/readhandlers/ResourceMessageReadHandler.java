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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ResourceMessageElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class ResourceMessageReadHandler extends AbstractTextElementReadHandler {
  private PropertyStringReadHandler stringReadHandler;
  private ResourceMessageElementFactory elementFactory;

  public ResourceMessageReadHandler() {
    elementFactory = new ResourceMessageElementFactory();
    stringReadHandler = new PropertyStringReadHandler();
  }

  protected TextElementFactory getTextElementFactory() {
    return elementFactory;
  }

  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    stringReadHandler.init( rootHandler, uri, tagName );
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );
    elementFactory.setFormatKey( atts.getValue( getUri(), "resource-key" ) );
    elementFactory.setResourceBase( atts.getValue( getUri(), "resource-base" ) );
    elementFactory.setNullString( atts.getValue( getUri(), "nullstring" ) );
    stringReadHandler.startParsing( atts );
  }

  public void characters( final char[] ch, final int start, final int length ) throws SAXException {
    stringReadHandler.characters( ch, start, length );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    stringReadHandler.doneParsing();
    final String key = stringReadHandler.getResult();
    if ( key.trim().length() > 0 ) {
      elementFactory.setFormatKey( key );
    }
    super.doneParsing();
  }
}
