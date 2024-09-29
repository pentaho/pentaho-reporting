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


package org.pentaho.reporting.libraries.css.resolver.values.percentages.lines;

import org.pentaho.reporting.libraries.css.StyleSheetUtility;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutOutputMetaData;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.dom.OutputProcessorFeature;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.line.LineHeight;
import org.pentaho.reporting.libraries.css.keys.line.LineStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class LineHeightResolveHandler implements ResolveHandler {
  public LineHeightResolveHandler() {
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return the array of required style keys.
   */
  public StyleKey[] getRequiredStyles() {
    return new StyleKey[] {
      FontStyleKeys.FONT_SIZE,
      FontStyleKeys.FONT_SIZE_ADJUST,

    };
  }

  /**
   * Resolves a single property.
   */
  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    if ( LineHeight.NONE.equals( value ) ) {
      // query the anchestor, if there's one ..
      handleNone( process, currentNode );
      return;
    }

    if ( LineHeight.NORMAL.equals( value ) ) {
      handleNormal( process, currentNode );
      return;
    }

    if ( value instanceof CSSNumericValue == false ) {
      // fall back to normal ..
      handleNormal( process, currentNode );
      return;
    }

    final CSSNumericValue nval = (CSSNumericValue) value;
    if ( isLengthValue( nval ) ) {
      layoutContext.setValue( LineStyleKeys.LINE_HEIGHT, nval );
      return;
    }

    final double factor;
    if ( nval.getType().equals( CSSNumericType.PERCENTAGE ) ) {
      factor = nval.getValue() / 100d;
    } else if ( nval.getType().equals( CSSNumericType.NUMBER ) ) {
      factor = nval.getValue();
    } else {
      handleNormal( process, currentNode );
      return;
    }


    final LayoutOutputMetaData metaData = process.getOutputMetaData();
    final int resolution = (int) metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    final double fontSize = StyleSheetUtility.convertLengthToDouble( value, resolution );
    layoutContext.setValue( LineStyleKeys.LINE_HEIGHT,
      CSSNumericValue.createValue( CSSNumericType.PT, fontSize * factor ) );

  }

  private boolean isLengthValue( final CSSNumericValue nval ) {
    final CSSNumericType type = nval.getNumericType();
    return ( type.isLength() );
  }

  private void handleNormal( final DocumentContext process,
                             final LayoutElement currentNode ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( FontStyleKeys.FONT_SIZE );
    final LayoutOutputMetaData metaData = process.getOutputMetaData();
    final int resolution = (int) metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    final double fontSize = StyleSheetUtility.convertLengthToDouble( value, resolution );
    if ( fontSize < 10 ) {
      layoutContext.setValue( LineStyleKeys.LINE_HEIGHT,
        CSSNumericValue.createValue( CSSNumericType.PT, fontSize * 1.2 ) );
    } else if ( fontSize < 24 ) {
      layoutContext.setValue( LineStyleKeys.LINE_HEIGHT,
        CSSNumericValue.createValue( CSSNumericType.PT, fontSize * 1.1 ) );
    } else {
      layoutContext.setValue( LineStyleKeys.LINE_HEIGHT,
        CSSNumericValue.createValue( CSSNumericType.PT, fontSize * 1.05 ) );
    }

  }

  private void handleNone( final DocumentContext process,
                           final LayoutElement currentNode ) {
    final double fontSize;
    final LayoutElement parent = currentNode.getParentLayoutElement();
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    if ( parent == null ) {
      // fall back to normal;
      final CSSValue value = layoutContext.getValue( FontStyleKeys.FONT_SIZE );
      final LayoutOutputMetaData metaData = process.getOutputMetaData();
      final int resolution = (int) metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
      fontSize = StyleSheetUtility.convertLengthToDouble( value, resolution );
    } else {
      final CSSValue value = parent.getLayoutStyle().getValue( FontStyleKeys.FONT_SIZE );
      final LayoutOutputMetaData metaData = process.getOutputMetaData();
      final int resolution = (int) metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
      fontSize = StyleSheetUtility.convertLengthToDouble( value, resolution );
    }
    layoutContext.setValue( LineStyleKeys.LINE_HEIGHT, CSSNumericValue.createValue( CSSNumericType.PT, fontSize ) );
  }
}
