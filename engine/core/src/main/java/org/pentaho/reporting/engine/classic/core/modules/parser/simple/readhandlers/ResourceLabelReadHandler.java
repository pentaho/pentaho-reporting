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

import org.pentaho.reporting.engine.classic.core.elementfactory.ResourceLabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class ResourceLabelReadHandler extends AbstractTextElementReadHandler {
  private PropertyStringReadHandler stringReadHandler;
  private ResourceLabelElementFactory elementFactory;

  public ResourceLabelReadHandler() {
    elementFactory = new ResourceLabelElementFactory();
    stringReadHandler = new PropertyStringReadHandler();
  }

  protected TextElementFactory getTextElementFactory() {
    return elementFactory;
  }

  public void init( final RootXmlReadHandler rootXmlReadHandler, final String namespace, final String tag )
    throws SAXException {
    super.init( rootXmlReadHandler, namespace, tag );
    stringReadHandler.init( rootXmlReadHandler, namespace, tag );
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
    elementFactory.setResourceKey( atts.getValue( getUri(), "resource-key" ) );
    elementFactory.setResourceBase( atts.getValue( getUri(), "resource-base" ) );
    elementFactory.setNullString( atts.getValue( getUri(), "nullstring" ) );
    stringReadHandler.startParsing( atts );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    stringReadHandler.doneParsing();
    final String key = stringReadHandler.getResult();
    if ( key.trim().length() > 0 ) {
      elementFactory.setResourceKey( key );
    }
    super.doneParsing();
  }
}
