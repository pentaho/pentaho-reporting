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
 * Copyright (c) 2002-2017 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.libraries.base.util.URLEncoder;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public class EnvFunction implements Function {

  private static final Object NULL_OBJECT = new Object();

  public EnvFunction() {
  }

  public String getCanonicalName() {
    return "ENV";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final String fieldName =
        context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );

    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    final ReportEnvironment reportEnvironment = rfc.getProcessingContext().getEnvironment();
    Object property = reportEnvironment.getEnvironmentProperty( fieldName );

    if ( "selfURL".equals( fieldName ) && property != null && !property.toString().isEmpty() ) {
      final ReportFormulaContext tmp = (ReportFormulaContext) rfc.getBackend();
      final ReportFormulaContext backend = tmp != null ? (ReportFormulaContext) tmp.getBackend() : null;
      ExpressionRuntime runtime = backend != null ? backend.getRuntime() : null;
      ProcessingContext processingContext = runtime != null ? runtime.getProcessingContext() : null;
      ResourceKey contentBase = processingContext != null ? processingContext.getContentBase() : null;
      ResourceKey parent = contentBase != null ? contentBase.getParent() : null;
      Object identifier = parent != null ? parent.getIdentifier() : null;

      String prop = convertToString( property );
      String id = convertToString( identifier );

      if ( prop != null && id != null && !prop.isEmpty() && !id.isEmpty() ) {
        id = URLEncoder.encodeUTF8( id.replaceAll( "/", ":" ) );
        if ( prop.endsWith( "/" ) ) {
          prop = prop + id;
        } else {
          prop = prop + "/" + id;
        }
        if ( !prop.endsWith( "/" ) ) {
          prop = prop + "/viewer?";
        } else {
          prop = prop + "viewer?";
        }
      }
      property = prop;
    }

    return new TypeValuePair( AnyType.TYPE, property );
  }

  private static String convertToString( Object o ) {
    String result = null;
    if ( o != null && o != NULL_OBJECT ) {
      result = o.toString();
    }
    return result;
  }
}
