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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class ExpressionPropertyReadHandler extends PropertyStringReadHandler {
  public static final String NAME_ATT = "name";
  public static final String CLASS_ATT = "class";

  private BeanUtility beanUtility;
  private String propertyName;
  private String propertyType;
  private String expressionName;
  private String originalExpressionClass;
  private String expressionClass;

  private static final Log logger = LogFactory.getLog( ExpressionPropertyReadHandler.class );
  private boolean strictParsing;

  public ExpressionPropertyReadHandler( final BeanUtility expression, final String originalExpressionClass,
      final String expressionClass, final String expressionName ) {
    if ( expression == null ) {
      throw new NullPointerException();
    }
    this.originalExpressionClass = originalExpressionClass;
    this.expressionClass = expressionClass;
    this.expressionName = expressionName;
    this.beanUtility = expression;
    this.strictParsing =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.parser.base.StrictParseMode" ) );
    if ( strictParsing == true ) {
      // if we have really really ancient reports, then strict parsing is not an option ..
      strictParsing = ObjectUtilities.equal( originalExpressionClass, expressionClass );
    }
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
    propertyType =
        CompatibilityMapperUtil.mapClassName( attrs.getValue( getUri(), ExpressionPropertyReadHandler.CLASS_ATT ) );
    propertyName = attrs.getValue( getUri(), ExpressionPropertyReadHandler.NAME_ATT );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
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
    final String propertyName =
        CompatibilityMapperUtil.mapExpressionProperty( originalExpressionClass, expressionClass, this.propertyName );
    if ( beanUtility == null ) {
      throw new ParseException( "No current beanUtility", getLocator() );
    }
    try {
      if ( propertyType != null ) {
        final ClassLoader cl = ObjectUtilities.getClassLoader( ExpressionPropertyReadHandler.class );
        final Class c = Class.forName( propertyType, false, cl );
        beanUtility.setPropertyAsString( propertyName, c, result );
      } else {
        beanUtility.setPropertyAsString( propertyName, result );
      }
    } catch ( BeanException e ) {
      if ( strictParsing ) {
        throw new ParseException( "Unable to assign property '" + propertyName + "' to expression '" + expressionName
            + '\'', e, getLocator() );
      }

      logger.warn( "Legacy-Parser warning: Unable to assign property '" + propertyName + "' to expression '"
          + expressionName + '\'', new ParseException( "Unable to assign property '" + propertyName
            + "' to expression '" + expressionName + '\'', e, getLocator() ) );
    } catch ( ClassNotFoundException e ) {
      throw new ParseException( "Unable to assign property '" + propertyName + "' to expression '" + expressionName
          + '\'', e, getLocator() );
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

}
