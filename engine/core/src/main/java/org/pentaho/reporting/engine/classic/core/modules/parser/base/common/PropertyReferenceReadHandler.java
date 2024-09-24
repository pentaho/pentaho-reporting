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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.StringValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.ValueConverter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class PropertyReferenceReadHandler extends PropertyStringReadHandler {
  private static final Log logger = LogFactory.getLog( PropertyReferenceReadHandler.class );

  private static final String CLASS_ATT = "class";
  private static final String NAME_ATT = "name";

  private String propertyName;
  private Object value;
  private ValueConverter valueType;

  public PropertyReferenceReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  public void startParsing( final PropertyAttributes attrs ) throws SAXException {
    super.startParsing( attrs );
    propertyName = attrs.getValue( getUri(), PropertyReferenceReadHandler.NAME_ATT );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
    }

    final String className =
        CompatibilityMapperUtil.mapClassName( attrs.getValue( getUri(), PropertyReferenceReadHandler.CLASS_ATT ) );
    if ( className == null ) {
      valueType = new StringValueConverter();
    } else {
      try {
        final ClassLoader classLoader = ObjectUtilities.getClassLoader( getClass() );
        final Class c = Class.forName( className );
        valueType = ConverterRegistry.getInstance().getValueConverter( c );
        if ( valueType == null ) {
          PropertyReferenceReadHandler.logger.warn( "Unable to find a suitable value-converter for " + c );
          valueType = new StringValueConverter();
        }
      } catch ( Exception e ) {
        throw new SAXException( "Attribute 'class' is invalid." );
      }
    }
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  public void doneParsing() throws SAXException {
    super.doneParsing();
    final String result = getResult();
    final boolean strictPropertyErrorHandling = ( result.trim().length() != 0 );

    try {
      value = valueType.toPropertyValue( result );
    } catch ( BeanException e ) {
      if ( strictPropertyErrorHandling ) {
        throw new ParseException( "Failed to parse property value for property " + propertyName, e );
      } else {
        PropertyReferenceReadHandler.logger.warn( "Failed to parse property value for property: " + propertyName, e );
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

  public String getPropertyName() {
    return propertyName;
  }
}
