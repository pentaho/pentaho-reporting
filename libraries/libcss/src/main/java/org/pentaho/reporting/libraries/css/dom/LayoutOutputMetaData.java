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

package org.pentaho.reporting.libraries.css.dom;

import org.pentaho.reporting.libraries.css.keys.page.PageSize;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

public interface LayoutOutputMetaData {
  public boolean isFeatureSupported( OutputProcessorFeature.BooleanOutputProcessorFeature feature );

  public double getNumericFeatureValue( OutputProcessorFeature.NumericOutputProcessorFeature feature );

  public boolean isContentSupported( Object content );

  public PageSize getDefaultPageSize();

  /**
   * Resolve one of the built-in fonts.
   *
   * @param name
   * @return
   */
  public CSSValue getNormalizedFontFamilyName( final CSSConstant name );

  public CSSValue getNormalizedFontFamilyName( final CSSStringValue value );

  public CSSValue getDefaultFontFamily();


  public FontMetrics getFontMetrics( final LayoutStyle style );
}
