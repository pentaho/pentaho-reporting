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


package org.pentaho.reporting.libraries.css.resolver.function.content;

import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.resolver.FunctionEvaluationException;
import org.pentaho.reporting.libraries.css.resolver.function.ContentFunction;
import org.pentaho.reporting.libraries.css.resolver.function.FunctionUtilities;
import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.VariableToken;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 16.04.2006, 18:41:16
 *
 * @author Thomas Morgner
 */
public class StringValueFunction implements ContentFunction {
  public StringValueFunction() {
  }

  public ContentToken evaluate( final DocumentContext layoutProcess,
                                final LayoutElement element,
                                final CSSFunctionValue function )
    throws FunctionEvaluationException {
    final CSSValue[] params = function.getParameters();
    if ( params.length != 1 ) {
      throw new FunctionEvaluationException();
    }
    final String name = FunctionUtilities.resolveString( layoutProcess, element, params[ 0 ] );
    return new VariableToken( name );
  }
}
