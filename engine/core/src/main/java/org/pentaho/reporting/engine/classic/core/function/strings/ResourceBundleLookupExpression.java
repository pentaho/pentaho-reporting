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

package org.pentaho.reporting.engine.classic.core.function.strings;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionUtilities;

import java.util.ResourceBundle;

/**
 * Performs a resource-bundle lookup using the value read from the defined field as key in the resource-bundle. This
 * expression behaves like the Resource-field.
 *
 * @author Thomas Morgner
 */
public class ResourceBundleLookupExpression extends AbstractExpression {
  /**
   * The field from where to read the key value.
   */
  private String field;

  /**
   * The name of the used resource bundle. If null, the default resource-bundle will be used instead.
   */
  private String resourceIdentifier;

  /**
   * Default Constructor.
   */
  public ResourceBundleLookupExpression() {
  }

  /**
   * Returns the name of the datarow-column from where to read the resourcebundle key value.
   *
   * @return the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the datarow-column from where to read the resourcebundle key value.
   *
   * @param field
   *          the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns the name of the resource-bundle. If none is defined here, the default resource-bundle is used instead.
   *
   * @return the resource-bundle identifier.
   */
  public String getResourceIdentifier() {
    return resourceIdentifier;
  }

  /**
   * Defines name of the resource-bundle. If none is defined here, the default resource-bundle is used instead.
   *
   * @param resourceIdentifier
   *          the resource-bundle identifier.
   */
  public void setResourceIdentifier( final String resourceIdentifier ) {
    this.resourceIdentifier = resourceIdentifier;
  }

  /**
   * Returns the current value for the data source.
   *
   * @return the value.
   */
  public Object getValue() {
    final Object key = getDataRow().get( getField() );
    if ( key == null ) {
      return null;
    }

    final ResourceBundleFactory resourceBundleFactory = getResourceBundleFactory();
    final ResourceBundle bundle;
    if ( resourceIdentifier == null ) {
      bundle = ExpressionUtilities.getDefaultResourceBundle( this );
    } else {
      bundle = resourceBundleFactory.getResourceBundle( resourceIdentifier );
    }
    return bundle.getObject( String.valueOf( key ) );
  }
}
