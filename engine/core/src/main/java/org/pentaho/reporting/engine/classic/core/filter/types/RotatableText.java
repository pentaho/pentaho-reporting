/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
