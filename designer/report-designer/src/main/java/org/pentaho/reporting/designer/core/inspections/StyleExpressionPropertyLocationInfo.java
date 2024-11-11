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
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class StyleExpressionPropertyLocationInfo extends LocationInfo {
  private StyleKey styleKey;
  private String expressionProperty;

  public StyleExpressionPropertyLocationInfo( final ReportElement reportElement,
                                              final StyleKey styleKey,
                                              final String expressionProperty ) {
    super( reportElement );
    if ( styleKey == null ) {
      throw new NullPointerException();
    }
    if ( expressionProperty == null ) {
      throw new NullPointerException();
    }

    this.styleKey = styleKey;
    this.expressionProperty = expressionProperty;
  }

  public StyleKey getStyleKey() {
    return styleKey;
  }

  public String getExpressionProperty() {
    return expressionProperty;
  }
}
