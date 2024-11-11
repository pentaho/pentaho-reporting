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


package org.pentaho.reporting.libraries.css.resolver.function.content;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;
import org.pentaho.reporting.libraries.css.counter.CounterStyleFactory;
import org.pentaho.reporting.libraries.css.counter.numeric.DecimalCounterStyle;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.ContentFunction;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionUtilities;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CounterToken;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 16.04.2006, 15:22:11
 *
 * @author Thomas Morgner
 */
public class CounterValueFunction implements ContentFunction {
  public CounterValueFunction() {
  }

  public ContentToken evaluate( final DocumentContext layoutProcess,
                                final LayoutElement element,
                                final CSSFunctionValue function )
    throws FunctionEvaluationException {
    // Accepts one or two parameters ...
    final CSSValue[] params = function.getParameters();
    if ( params.length < 1 ) {
      throw new FunctionEvaluationException();
    }
    final String counterName =
      FunctionUtilities.resolveString( layoutProcess, element, params[ 0 ] );

    CounterStyle cstyle = null;
    if ( params.length > 1 ) {
      final String styleName =
        FunctionUtilities.resolveString( layoutProcess, element, params[ 1 ] );
      cstyle = CounterStyleFactory.getInstance().getCounterStyle( styleName );
    }

    if ( cstyle == null ) {
      // todo: Create a global context ..
      //      final DocumentContext documentContext = layoutProcess.getDocumentContext();
      //      cstyle = documentContext.getCounterStyle(counterName);
      cstyle = new DecimalCounterStyle();
    }
    return new CounterToken( counterName, cstyle );
  }
}
