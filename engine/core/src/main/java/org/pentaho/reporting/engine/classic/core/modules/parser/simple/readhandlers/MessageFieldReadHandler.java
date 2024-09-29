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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.MessageFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class MessageFieldReadHandler extends AbstractTextElementReadHandler {
  private PropertyStringReadHandler stringReadHandler;
  private MessageFieldElementFactory elementFactory;

  public MessageFieldReadHandler() {
    elementFactory = new MessageFieldElementFactory();
    stringReadHandler = new PropertyStringReadHandler();
  }

  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    stringReadHandler.init( rootHandler, uri, tagName );
  }

  protected TextElementFactory getTextElementFactory() {
    return elementFactory;
  }

  public void characters( final char[] ch, final int start, final int length ) throws SAXException {
    stringReadHandler.characters( ch, start, length );
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );
    stringReadHandler.startParsing( atts );
    elementFactory.setNullString( atts.getValue( getUri(), "nullstring" ) );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    stringReadHandler.doneParsing();
    final String text = stringReadHandler.getResult();
    elementFactory.setFormatString( text );
    super.doneParsing();
  }
}
