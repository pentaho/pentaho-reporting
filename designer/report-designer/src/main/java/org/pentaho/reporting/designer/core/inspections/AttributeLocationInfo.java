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

public class AttributeLocationInfo extends LocationInfo {
  private String attributeNamespace;
  private String attributeName;
  private boolean expression;

  public AttributeLocationInfo( final ReportElement reportElement,
                                final String attributeNamespace,
                                final String attributeName,
                                final boolean expression ) {
    super( reportElement );
    if ( attributeName == null ) {
      throw new NullPointerException();
    }
    if ( attributeNamespace == null ) {
      throw new NullPointerException();
    }

    this.expression = expression;
    this.attributeNamespace = attributeNamespace;
    this.attributeName = attributeName;
  }

  public String getAttributeNamespace() {
    return attributeNamespace;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public boolean isExpression() {
    return expression;
  }

}
