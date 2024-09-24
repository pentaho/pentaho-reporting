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
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class InlineTableDataReadHandler extends StringReadHandler {
  private Class type;
  private boolean nullValue;
  private Object value;

  public InlineTableDataReadHandler( final Class type ) {
    if ( type == null ) {
      throw new NullPointerException();
    }
    this.type = type;
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
    super.startParsing( attrs );
    if ( "true".equals( attrs.getValue( getUri(), "null" ) ) ) {
      nullValue = true;
    }

    // redefine the declared type. Do not care whether the type will be compatible with the global one,
    // this is the user's problem.
    final String type = attrs.getValue( getUri(), "type" );
    if ( type != null ) {
      try {
        final ClassLoader loader = ObjectUtilities.getClassLoader( AbstractXmlReadHandler.class );
        this.type = Class.forName( CompatibilityMapperUtil.mapClassName( type ), false, loader );
      } catch ( ClassNotFoundException e ) {
        throw new ParseException( "Required attribute 'type' is not valid.", getLocator() );
      }
    }

  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( nullValue ) {
      value = null;
      return;
    }

    try {
      value = ConverterRegistry.toPropertyValue( getResult(), type );
    } catch ( BeanException e ) {
      try {
        value = ConverterRegistry.toPropertyValue( getResult().trim(), type );
      } catch ( BeanException ex ) {
        throw new ParseException(
            "Unable to convert value '" + getResult() + "' into a object of type '" + type + "'.", ex, getLocator() );
      }
    }
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
