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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.ResourceBundle;

public class ResourceFieldType extends AbstractElementType implements RotatableText {
  public static final ResourceFieldType INSTANCE = new ResourceFieldType();
  private static final Log logger = LogFactory.getLog( ResourceFieldType.class );

  public ResourceFieldType() {
    super( "resource-field" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {

    final Object resourceKeyRaw = ElementTypeUtils.queryFieldName( element );
    if ( resourceKeyRaw == null ) {
      return rotate( element, "<null>", runtime );
    }

    return rotate( element, resourceKeyRaw.toString(), runtime );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element from which to read attribute.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object resourceKeyRaw = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( resourceKeyRaw == null ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }

    final String resourceKey = String.valueOf( resourceKeyRaw );
    final String resourceId = ElementTypeUtils.queryResourceId( runtime, element );
    if ( resourceId == null ) {
      return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
    }

    try {
      final ResourceBundleFactory resourceBundleFactory = runtime.getResourceBundleFactory();
      final ResourceBundle bundle = resourceBundleFactory.getResourceBundle( resourceId );
      if ( bundle != null ) {
        return rotate( element, bundle.getString( resourceKey ), runtime );
      }
    } catch ( Exception e ) {
      // on errors return null.
      ResourceFieldType.logger.warn( "Failed to retrieve a value for key " + resourceId );
    }

    return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );

  }
}
