/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
