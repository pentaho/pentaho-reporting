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
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.PendingToken;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 23.10.2006, 17:50:27
 *
 * @author Thomas Morgner
 */
public class PendingFunction implements ContentFunction {
  public PendingFunction() {
  }

  public ContentToken evaluate( final DocumentContext layoutProcess,
                                final LayoutElement element,
                                final CSSFunctionValue function )
    throws FunctionEvaluationException {
    final CSSValue[] parameters = function.getParameters();
    if ( parameters.length == 0 ) {
      throw new FunctionEvaluationException
        ( "One parameter must be given to the 'pending' function." );
    }

    final String name = FunctionUtilities.resolveString
      ( layoutProcess, element, parameters[ 0 ] );
    return new PendingToken( name );
  }
}
