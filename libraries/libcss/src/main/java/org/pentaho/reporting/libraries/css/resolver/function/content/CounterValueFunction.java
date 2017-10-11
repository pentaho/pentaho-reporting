/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
