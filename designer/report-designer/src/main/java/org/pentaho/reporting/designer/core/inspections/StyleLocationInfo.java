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
public class StyleLocationInfo extends LocationInfo {
  private StyleKey styleKey;
  private boolean expression;

  public StyleLocationInfo( final ReportElement reportElement,
                            final StyleKey styleKey,
                            final boolean expression ) {
    super( reportElement );
    this.expression = expression;
    if ( styleKey == null ) {
      throw new NullPointerException();
    }
    this.styleKey = styleKey;
  }

  public StyleKey getStyleKey() {
    return styleKey;
  }

  public boolean isExpression() {
    return expression;
  }
}
