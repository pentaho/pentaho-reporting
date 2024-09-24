/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ObjectConverterFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class DesignerExpressionPropertyReadHandler extends PropertyStringReadHandler {
  public static final String NAME_ATT = "name";
  public static final String CLASS_ATT = "class";

  private BeanUtility beanUtility;
  private String propertyName;
  private String propertyType;
  private String expressionName;
  private boolean array;
  private ArrayList arrayProperties;


  public DesignerExpressionPropertyReadHandler( final BeanUtility expression,
                                                final String expressionName ) {
    if ( expression == null ) {
      throw new NullPointerException();
    }
    this.arrayProperties = new ArrayList();
    this.expressionName = expressionName;
    this.beanUtility = expression;
  }


  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  public void startParsing( final PropertyAttributes attrs )
    throws SAXException {
    super.startParsing( attrs );
    propertyType = CompatibilityMapperUtil.mapClassName
      ( attrs.getValue( getUri(), DesignerExpressionPropertyReadHandler.CLASS_ATT ) );
    propertyName = attrs.getValue( getUri(), DesignerExpressionPropertyReadHandler.NAME_ATT );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
    }

    final String value = attrs.getValue( getUri(), "array" );
    if ( value != null ) {
      // SIC! This is how it is parsed ...
      this.array = true;
    }
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final PropertyAttributes attrs ) throws SAXException {
    if ( array == false ) {
      return null;
    }
    if ( isSameNamespace( uri ) && "property".equals( tagName ) ) {
      final PropertyReadHandler readHandler = new PropertyReadHandler();
      arrayProperties.add( readHandler );
      return readHandler;
    }
    return super.getHandlerForChild( uri, tagName, attrs );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  public void doneParsing()
    throws SAXException {
    super.doneParsing();
    final String result = getResult();
    if ( beanUtility == null ) {
      throw new ParseException( "No current beanUtility", getLocator() );
    }

    try {

      if ( array ) {
        final Class type = beanUtility.getPropertyType( propertyName );


        final HashMap values = new HashMap();
        final int elementCount = arrayProperties.size();
        for ( int i = 0; i < elementCount; i++ ) {
          final PropertyReadHandler handler = (PropertyReadHandler) arrayProperties.get( i );
          values.put( handler.getName(), handler.getResult() );
        }

        final Class componentType = type.getComponentType();
        final ArrayList realValues = new ArrayList();
        for ( int i = 0; i < elementCount; i++ ) {
          final String text = (String) values.get( String.valueOf( i ) );
          if ( text == null ) {
            throw new ParseException( "Disrupted array - dont play games with me!", getLocator() );
          }
          realValues.add( ObjectConverterFactory.convert( componentType, text, getLocator() ) );
        }

        final Object[] o = (Object[]) Array.newInstance( type.getComponentType(), realValues.size() );
        final Object[] objects = realValues.toArray( o );
        beanUtility.setProperty( propertyName, objects );
        return;
      }

      if ( propertyType != null ) {
        final ClassLoader cl = ObjectUtilities.getClassLoader( DesignerExpressionPropertyReadHandler.class );
        final Class c = Class.forName( propertyType, false, cl );
        beanUtility.setPropertyAsString( propertyName, c, result );
      } else {
        beanUtility.setProperty( propertyName, ObjectConverterFactory.convert
          ( beanUtility.getPropertyType( propertyName ), result, getLocator() ) );
      }
    } catch ( BeanException e ) {
      if ( isIgnorable() ) {
        return;
      }

      throw new ParseException( "Unable to assign property '" + propertyName
        + "' to expression '" + expressionName + '\'', e, getLocator() );
    } catch ( ClassNotFoundException e ) {
      if ( isIgnorable() ) {
        return;
      }

      throw new ParseException( "Unable to assign property '" + propertyName
        + "' to expression '" + expressionName + '\'', e, getLocator() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }

  public boolean isIgnorable() {
    if ( "position".equals( propertyName ) ) {
      return true;
    }
    if ( "minimumSize".equals( propertyName ) ) {
      return true;
    }
    if ( "preferredSize".equals( propertyName ) ) {
      return true;
    }
    if ( "maximumSize".equals( propertyName ) ) {
      return true;
    }
    if ( "background".equals( propertyName ) ) {
      return true;
    }
    if ( "dynamicContent".equals( propertyName ) ) {
      return true;
    }

    return false;
  }
}
