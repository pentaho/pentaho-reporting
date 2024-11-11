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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class InlineTableColumnReadHandler extends AbstractXmlReadHandler {
  private String name;
  private Class type;

  public InlineTableColumnReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is not defined.", getLocator() );
    }

    final String type = attrs.getValue( getUri(), "type" );
    if ( type == null ) {
      throw new ParseException( "Required attribute 'type' is not defined.", getLocator() );
    }

    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( AbstractXmlReadHandler.class );
      this.type = Class.forName( CompatibilityMapperUtil.mapClassName( type ), false, loader );
    } catch ( ClassNotFoundException e ) {
      throw new ParseException( "Required attribute 'type' is not valid.", getLocator() );
    }
  }

  public String getName() {
    return name;
  }

  public Class getType() {
    return type;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
