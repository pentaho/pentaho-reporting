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
import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class RgbValueFunction implements StyleValueFunction {
  public RgbValueFunction() {
  }

  protected int validateParameter( final CSSValue value ) throws FunctionEvaluationException {
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
      return (int) ( nval.getValue() % 256 );
    }
    if ( nval.getType().equals( CSSNumericType.PERCENTAGE ) ) {
      return (int) ( nval.getValue() * 256.0 / 100.0 );
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
      final int redValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 0 ] ) );
      final int greenValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 1 ] ) );
      final int blueValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 2 ] ) );
      return new CSSColorValue( redValue, greenValue, blueValue );
    } else if ( values.length == 4 ) {
      final int redValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 0 ] ) );
      final int greenValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 1 ] ) );
      final int blueValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 2 ] ) );
      final int alphaValue = validateParameter
        ( FunctionUtilities.resolveParameter( layoutProcess, element, values[ 3 ] ) );
      return new CSSColorValue( redValue, greenValue, blueValue, alphaValue );
    } else {
      throw new FunctionEvaluationException( "Expected either three or four parameters." );
    }
  }
}
