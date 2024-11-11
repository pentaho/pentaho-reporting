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

import org.pentaho.reporting.engine.classic.extensions.modules.mailer.FormulaHeader;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FormulaHeaderReadHandler extends AbstractXmlReadHandler {
  private String name;
  private String value;

  public FormulaHeaderReadHandler() {

  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( StringUtils.isEmpty( name ) ) {
      throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
    }
    value = attrs.getValue( getUri(), "formula" );
    if ( StringUtils.isEmpty( value ) ) {
      throw new ParseException( "Required attribute 'formula' is missing.", getLocator() );
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
    return new FormulaHeader( name, value );
  }
}
