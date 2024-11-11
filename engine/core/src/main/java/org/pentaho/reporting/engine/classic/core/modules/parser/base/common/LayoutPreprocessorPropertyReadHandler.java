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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class LayoutPreprocessorPropertyReadHandler extends PropertyStringReadHandler {
  private BeanUtility beanUtility;
  private String propertyName;
  private String propertyType;

  public LayoutPreprocessorPropertyReadHandler( final BeanUtility expression ) {
    super();
    this.beanUtility = expression;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  public void startParsing( final PropertyAttributes attrs ) throws SAXException {
    super.startParsing( attrs );
    propertyType = CompatibilityMapperUtil.mapClassName( attrs.getValue( getUri(), "class" ) );
    propertyName = attrs.getValue( getUri(), "name" );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
    }
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  public void doneParsing() throws SAXException {
    super.doneParsing();
    final String result = getResult();
    if ( beanUtility == null ) {
      throw new SAXException( "No current beanUtility" );
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
      throw new ParseException(
          "Unable to assign property '" + propertyName + "' to the specified Layout-PreProcessor", e, getLocator() );
    } catch ( ClassNotFoundException e ) {
      throw new ParseException( "Unable to assign property '" + propertyName + "' to expression Layout-PreProcessor",
          e, getLocator() );
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
