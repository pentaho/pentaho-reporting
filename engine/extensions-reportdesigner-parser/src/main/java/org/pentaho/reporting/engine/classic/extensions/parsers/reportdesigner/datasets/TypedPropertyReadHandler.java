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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.DateConverter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Date;

public class TypedPropertyReadHandler extends StringReadHandler {
  private Object value;
  private String name;
  private String type;
  private Class typeClass;

  public TypedPropertyReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    name = attrs.getValue( getUri(), "name" );
    type = attrs.getValue( getUri(), "type" );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final String result = getResult();
    if ( Boolean.class.getName().equals( type ) ) {
      if ( StringUtils.isEmpty( result ) == false ) {
        value = Boolean.valueOf( result );
      }
      typeClass = Boolean.class;
    } else if ( Integer.class.getName().equals( type ) ) {
      if ( StringUtils.isEmpty( result ) == false ) {
        value = new Integer( result );
      }
      typeClass = Integer.class;
    } else if ( Double.class.getName().equals( type ) ) {
      if ( StringUtils.isEmpty( result ) == false ) {
        value = new Double( result );
      }
      typeClass = Double.class;
    } else if ( Date.class.getName().equals( type ) ) {
      if ( StringUtils.isEmpty( result ) == false ) {
        value = DateConverter.getObject( result );
      }
      typeClass = Date.class;
    } else {
      value = result;
      typeClass = String.class;
    }
  }

  public Class getType() {
    return typeClass;
  }

  public String getName() {
    return name;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return value;
  }
}
