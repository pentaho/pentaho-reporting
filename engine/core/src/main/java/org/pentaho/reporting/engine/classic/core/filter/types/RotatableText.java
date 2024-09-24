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

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.RotatedTextDrawable;

public interface RotatableText {

  default Object rotate( final ReportElement element, final Object value, final ExpressionRuntime runtime ) {
    if ( element != null && value != null && isRotationSupported( runtime ) ) {
      final ElementStyleSheet style = element.getStyle();
      if ( style != null ) {
        final Object styleProperty = style.getStyleProperty( TextStyleKeys.TEXT_ROTATION, null );
        if ( styleProperty instanceof TextRotation ) {
          final TextRotation rotation = (TextRotation) styleProperty;
          return new RotatedTextDrawable( String.valueOf( value ), rotation );
        }
      }
    }
    return value;
  }

  static boolean isRotationSupported( final ExpressionRuntime runtime ) {
    if ( runtime != null ) {
      final ProcessingContext processingContext = runtime.getProcessingContext();
      if ( processingContext != null ) {
        final OutputProcessorMetaData outputProcessorMetaData = processingContext.getOutputProcessorMetaData();
        if ( outputProcessorMetaData != null ) {
          return !outputProcessorMetaData.isFeatureSupported( OutputProcessorFeature.IGNORE_ROTATION );
        }
      }
    }
    return false;
  }

}
