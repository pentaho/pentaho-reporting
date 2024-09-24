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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class UrlParameterSeparatorFunction implements Function {
  public UrlParameterSeparatorFunction() {
  }

  public String getCanonicalName() {
    return "URLPARAMETERSEPARATOR";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() == 0 ) {
      return new TypeValuePair( TextType.TYPE, "?" );
    }
    final String text = context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
    if ( text == null ) {
      return new TypeValuePair( TextType.TYPE, "?" );
    }
    if ( text.indexOf( '?' ) == -1 ) {
      return new TypeValuePair( TextType.TYPE, "?" );
    }

    if ( text.endsWith( "?" ) ) {
      return new TypeValuePair( TextType.TYPE, text );
    }

    return new TypeValuePair( TextType.TYPE, text + "&" );
  }
}
