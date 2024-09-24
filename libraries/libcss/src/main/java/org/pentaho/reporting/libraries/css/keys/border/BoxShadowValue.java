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

package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSGenericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSType;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 30.10.2005, 19:53:45
 *
 * @author Thomas Morgner
 */
public class BoxShadowValue implements CSSValue {
  private CSSColorValue color;
  private CSSNumericValue horizontalOffset;
  private CSSNumericValue verticalOffset;
  private CSSNumericValue blurRadius;

  public BoxShadowValue( final CSSColorValue color,
                         final CSSNumericValue horizontalOffset,
                         final CSSNumericValue verticalOffset,
                         final CSSNumericValue blurRadius ) {
    this.color = color;
    this.horizontalOffset = horizontalOffset;
    this.verticalOffset = verticalOffset;
    this.blurRadius = blurRadius;
  }

  public CSSColorValue getColor() {
    return color;
  }

  public CSSNumericValue getHorizontalOffset() {
    return horizontalOffset;
  }

  public CSSNumericValue getVerticalOffset() {
    return verticalOffset;
  }

  public CSSNumericValue getBlurRadius() {
    return blurRadius;
  }

  public String getCSSText() {
    return horizontalOffset + " " + verticalOffset + " " + blurRadius + " " + color;
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
