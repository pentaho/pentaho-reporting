/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
