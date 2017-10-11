/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public abstract class AbstractParameterReadHandler extends AbstractXmlReadHandler {
  private String name;
  private Class type;
  private boolean mandatory;
  private Object defaultValue;
  private ArrayList<ParameterAttributeReadHandler> attributeReadHandlers;

  protected AbstractParameterReadHandler() {
    attributeReadHandlers = new ArrayList<ParameterAttributeReadHandler>();
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
    if ( name == null ) {
      throw new ParseException( "Required parameter 'name' is missing.", getLocator() );
    }

    final String typeText = attrs.getValue( getUri(), "type" );
    if ( typeText == null ) {
      type = Object.class;
    } else {
      try {
        final String realClassName = CompatibilityMapperUtil.mapClassName( typeText );
        final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
        type = Class.forName( realClassName, false, classLoader );
      } catch ( final Throwable e ) {
        throw new ParseException( "Required parameter 'type' is invalid.", getLocator() );
      }
    }

    mandatory = "true".equals( attrs.getValue( getUri(), "mandatory" ) );

    final String defaultValueText = attrs.getValue( getUri(), "default-value" );
    if ( defaultValueText != null ) {
      try {
        defaultValue = ConverterRegistry.toPropertyValue( defaultValueText, type );
      } catch ( BeanException e ) {
        throw new ParseException( "Specified parameter 'default-value' is invalid.", getLocator() );
      }
    }
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "attribute".equals( tagName ) ) {
      final ParameterAttributeReadHandler readHandler = new ParameterAttributeReadHandler();
      attributeReadHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void applyAttributes( final AbstractParameter parameter ) {
    final ParameterAttributeReadHandler[] parameterAttributeReadHandlers = getAttributeReadHandlers();
    for ( int i = 0; i < parameterAttributeReadHandlers.length; i++ ) {
      final ParameterAttributeReadHandler handler = parameterAttributeReadHandlers[i];
      parameter.setParameterAttribute( handler.getNamespace(), handler.getName(), handler.getResult() );
    }
  }

  private ParameterAttributeReadHandler[] getAttributeReadHandlers() {
    return attributeReadHandlers.toArray( new ParameterAttributeReadHandler[attributeReadHandlers.size()] );
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public String getName() {
    return name;
  }

  public Class getType() {
    return type;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }
}
