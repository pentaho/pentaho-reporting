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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubReportParameterReadHandler extends AbstractXmlReadHandler {
  private String key;
  private String value;

  public SubReportParameterReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    key = attrs.getValue( getUri(), "key" );
    if ( key == null ) {
      throw new NullPointerException();
    }
    value = attrs.getValue( getUri(), "value" );
    if ( value == null ) {
      throw new NullPointerException();
    }
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
