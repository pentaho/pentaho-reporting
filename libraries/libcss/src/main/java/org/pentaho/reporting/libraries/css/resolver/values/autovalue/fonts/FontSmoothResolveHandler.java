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


package org.pentaho.reporting.libraries.css.resolver.values.autovalue.fonts;

import org.pentaho.reporting.libraries.css.StyleSheetUtility;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutOutputMetaData;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.dom.OutputProcessorFeature;
import org.pentaho.reporting.libraries.css.keys.font.FontSmooth;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 18.12.2005, 15:13:24
 *
 * @author Thomas Morgner
 */
public class FontSmoothResolveHandler implements ResolveHandler {
  public FontSmoothResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      FontStyleKeys.FONT_SIZE
    };
  }

  /**
   * Resolves a single property.
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    // as this is an 'auto' handler, we can assume that 'auto' is the
    // current value
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( FontStyleKeys.FONT_SIZE );

    final LayoutOutputMetaData metaData = process.getOutputMetaData();
    final int resolution = (int) metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    final double fontSize = StyleSheetUtility.convertLengthToDouble( value, resolution );
    final double threshold = metaData.getNumericFeatureValue( OutputProcessorFeature.FONT_SMOOTH_THRESHOLD );
    if ( fontSize < threshold ) {
      layoutContext.setValue( FontStyleKeys.FONT_SMOOTH, FontSmooth.NEVER );
    } else {
      layoutContext.setValue( FontStyleKeys.FONT_SMOOTH, FontSmooth.ALWAYS );
    }
  }
}
