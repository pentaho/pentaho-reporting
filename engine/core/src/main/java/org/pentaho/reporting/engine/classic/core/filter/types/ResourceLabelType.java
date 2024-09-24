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

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.ResourceBundle;

public class ResourceLabelType extends AbstractElementType implements RotatableText {
  private static final Log logger = LogFactory.getLog( ResourceLabelType.class );
  public static final ResourceLabelType INSTANCE = new ResourceLabelType();

  public ResourceLabelType() {
    super( "resource-label" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object resourceKeyRaw = ElementTypeUtils.queryStaticValue( element );
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

    final Object resourceKeyRaw = ElementTypeUtils.queryStaticValue( element );
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
      ResourceLabelType.logger.warn( "Failed to retrieve the value for resource-bundle " + resourceId + " with key "
          + resourceKey );
    }

    return rotate( element, element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ), runtime );
  }
}
