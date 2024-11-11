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


package org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailHeader;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HeadersReadHandler extends AbstractXmlReadHandler {
  private ArrayList headerReadHandlers;

  private MailHeader[] headers;

  public HeadersReadHandler() {
    headerReadHandlers = new ArrayList();
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
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "formula-header".equals( tagName ) ) {
      final FormulaHeaderReadHandler readHandler = new FormulaHeaderReadHandler();
      headerReadHandlers.add( readHandler );
      return readHandler;
    }
    if ( "static-header".equals( tagName ) ) {
      final StaticHeaderReadHandler readHandler = new StaticHeaderReadHandler();
      headerReadHandlers.add( readHandler );
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
    headers = new MailHeader[headerReadHandlers.size()];
    for ( int i = 0; i < headerReadHandlers.size(); i++ ) {
      final XmlReadHandler handler = (XmlReadHandler) headerReadHandlers.get( i );
      headers[i] = (MailHeader) handler.getObject();
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occurred.
   */
  public Object getObject() throws SAXException {
    return headers;
  }

  public MailHeader[] getHeaders() {
    return headers;
  }
}
