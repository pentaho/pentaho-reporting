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

package org.pentaho.reporting.engine.classic.core.wizard.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.MetaSelector;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MetaSelectorReadHandler extends AbstractXmlReadHandler {
  private String domain;
  private String name;
  private Object value;

  public MetaSelectorReadHandler() {
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
    domain = attrs.getValue( getUri(), "domain" );
    if ( domain == null ) {
      throw new ParseException( "Required attribute 'domain' is missing.", getLocator() );
    }

    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
    }

    final String type = attrs.getValue( getUri(), "type" );
    final String rawValue = attrs.getValue( getUri(), "value" );
    if ( rawValue == null ) {
      value = null;
    } else if ( type == null ) {
      value = rawValue;
    } else {
      try {
        final ClassLoader loader = ObjectUtilities.getClassLoader( MetaSelectorReadHandler.class );
        final Class aClass = Class.forName( CompatibilityMapperUtil.mapClassName( type ), false, loader );
        value = ConverterRegistry.toPropertyValue( rawValue, aClass );
      } catch ( ClassNotFoundException e ) {
        throw new ParseException( "Required attribute 'type' is invalid.", e, getLocator() );
      } catch ( BeanException e ) {
        throw new ParseException( "Required attribute 'value' is invalid.", e, getLocator() );
      }
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return new MetaSelector( domain, name, value );
  }
}
