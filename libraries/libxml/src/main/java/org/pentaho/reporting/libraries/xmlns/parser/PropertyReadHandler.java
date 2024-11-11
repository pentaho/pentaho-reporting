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


package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * The Property-ReadHandler reads character data from an element along with a 'name' attribute. This way, this class is
 * most suitable for creating name-value-pairs.
 *
 * @author Thomas Morgner
 */
public class PropertyReadHandler extends StringReadHandler {
  private String name;
  private String nameAttribute;
  private boolean nameMandatory;

  /**
   * The Default-Constructor.
   */
  public PropertyReadHandler() {
    nameAttribute = "name";
    nameMandatory = true;
  }

  public PropertyReadHandler( final String name, final boolean nameMandatory ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.nameAttribute = name;
    this.nameMandatory = nameMandatory;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    name = attrs.getValue( getUri(), nameAttribute );
    if ( nameMandatory && name == null ) {
      throw new ParseException( "Required attribute '" + nameAttribute + "' missing", getLocator() );
    }
  }

  /**
   * Returns the declared property-name.
   *
   * @return the property name.
   */
  public String getName() {
    return name;
  }
}
