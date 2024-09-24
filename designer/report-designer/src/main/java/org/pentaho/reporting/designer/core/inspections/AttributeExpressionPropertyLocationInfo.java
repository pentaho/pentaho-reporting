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

package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.engine.classic.core.ReportElement;

public class AttributeExpressionPropertyLocationInfo extends LocationInfo {
  private String attributeName;
  private String attributeNamespace;
  private String expressionProperty;

  public AttributeExpressionPropertyLocationInfo( final ReportElement reportElement,
                                                  final String attributeNamespace,
                                                  final String attributeName,
                                                  final String expressionProperty ) {
    super( reportElement );
    if ( attributeName == null ) {
      throw new NullPointerException();
    }
    if ( attributeNamespace == null ) {
      throw new NullPointerException();
    }
    if ( expressionProperty == null ) {
      throw new NullPointerException();
    }

    this.attributeNamespace = attributeNamespace;
    this.expressionProperty = expressionProperty;
    this.attributeName = attributeName;
  }

  public String getAttributeNamespace() {
    return attributeNamespace;
  }

  public String getExpressionProperty() {
    return expressionProperty;
  }

  public String getAttributeName() {
    return attributeName;
  }

}
