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

package org.pentaho.reporting.libraries.css.resolver.function.values;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionUtilities;
import org.pentaho.reporting.libraries.css.resolver.function.StyleValueFunction;
import org.pentaho.reporting.libraries.css.util.ColorUtil;
import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class HslValueFunction implements StyleValueFunction {
  public HslValueFunction() {
  }

  protected int validateHueParameter( final CSSValue value ) throws FunctionEvaluationException {
    final CSSNumericValue nval;
    if ( value instanceof CSSStringValue ) {
      // I shouldn't do this, but ..
      final CSSStringValue strVal = (CSSStringValue) value;
      nval = FunctionUtilities.parseNumberValue( strVal.getValue() );
    } else if ( value instanceof CSSNumericValue == false ) {
      throw new FunctionEvaluationException( "Expected a number" );
    } else {
      nval = (CSSNumericValue) value;
    }
    if ( nval.getType().equals( CSSNumericType.NUMBER ) ) {
      return (int) ( nval.getValue() % 360 );
    }
    throw new FunctionEvaluationException( "Expected a number, not a length" );
  }

  protected float validateOtherParameter( final CSSValue value ) throws FunctionEvaluationException {
    final CSSNumericValue nval;
    if ( value instanceof CSSStringValue ) {
      // I shouldn't do this, but ..
      final CSSStringValue strVal = (CSSStringValue) value;
      nval = FunctionUtilities.parseNumberValue( strVal.getValue() );
    } else if ( value instanceof CSSNumericValue == false ) {
      throw new FunctionEvaluationException( "Expected a number" );
    } else {
      nval = (CSSNumericValue) value;
    }
    if ( nval.getType().equals( CSSNumericType.PERCENTAGE ) ) {
      return (float) ( nval.getValue() % 100 );
    }
    throw new FunctionEvaluationException( "Expected a number, not a length" );
  }

  public boolean isAutoResolveable() {
    return true;
  }


  public CSSValue evaluate( final DocumentContext layoutProcess,
                            final LayoutElement element,
                            final CSSFunctionValue function )
    throws FunctionEvaluationException {

    final CSSValue[] values = function.getParameters();
    if ( values.length == 3 ) {
      final int hueValue = validateHueParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 0 ] ) );
      final float saturationValue = validateOtherParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 1 ] ) );
      final float lightValue = validateOtherParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 2 ] ) );
      final float[] rgb = ColorUtil.hslToRGB
        ( hueValue, saturationValue, lightValue );
      return new CSSColorValue( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] );
    } else if ( values.length == 4 ) {
      final int hueValue = validateHueParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 0 ] ) );
      final float saturationValue = validateOtherParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 1 ] ) );
      final float lightValue = validateOtherParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 2 ] ) );
      final float alphaValue = validateOtherParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 3 ] ) );
      final float[] rgb =
        ColorUtil.hslToRGB( hueValue, saturationValue, lightValue );
      return new CSSColorValue( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ], alphaValue );
    } else {
      throw new FunctionEvaluationException( "Expected either three or four parameters." );
    }
  }
}

