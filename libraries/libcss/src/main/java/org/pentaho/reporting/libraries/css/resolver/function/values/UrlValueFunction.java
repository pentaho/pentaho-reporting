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
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSResourceValue;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class UrlValueFunction implements StyleValueFunction {
  public UrlValueFunction() {
  }

  public boolean isAutoResolveable() {
    return false;
  }

  public CSSValue evaluate( final DocumentContext layoutProcess,
                            final LayoutElement element,
                            final CSSFunctionValue function )
    throws FunctionEvaluationException {
    final CSSValue[] params = function.getParameters();
    if ( params.length != 1 ) {
      throw new FunctionEvaluationException();
    }
    final CSSValue value = FunctionUtilities.resolveParameter( layoutProcess, element, params[ 0 ] );
    if ( value instanceof CSSResourceValue ) {
      return value;
    }
    if ( value instanceof CSSStringValue == false ) {
      throw new FunctionEvaluationException
        ( "Not even remotely an URI: " + value );
    }
    final CSSStringValue strval = (CSSStringValue) value;
    return FunctionUtilities.loadResource( layoutProcess, strval.getValue() );
  }
}
