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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ObjectConverterFactory;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ReportFunctionPropertyArrayReadHandler extends StringReadHandler {
  private Class componentType;
  private Object value;
  private String propertyName;

  public ReportFunctionPropertyArrayReadHandler( final Class componentType ) {
    this.componentType = componentType;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    propertyName = attrs.getValue( getUri(), "name" );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
    }
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    value = ObjectConverterFactory.convert( componentType, getResult(), getLocator() );
  }

  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() {
    return value;
  }
}
