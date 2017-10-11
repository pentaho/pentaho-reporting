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

package org.pentaho.reporting.engine.classic.core.function.formula;


import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Created by dima.prokopenko@gmail.com on 9/29/2016.
 */
public class ResourceLookupFunction implements Function {

  public static final String NAME = "RESOURCELOOKUP";

  @Override public String getCanonicalName() {
    return NAME;
  }

  /**
   * RESOURCELOOKUP(bundleId, resourceId)
   *
   * @param context
   * @param parameters
   * @return
   * @throws EvaluationException
   */
  @Override public TypeValuePair evaluate( FormulaContext context, ParameterCallback parameters )
    throws EvaluationException {

    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    Type textType = parameters.getType( 0 );
    Object textValue = parameters.getValue( 0 );
    final String bundleId = context.getTypeRegistry().convertToText( textType, textValue );

    if ( bundleId == null || bundleId.isEmpty() ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    textType = parameters.getType( 1 );
    textValue = parameters.getValue( 1 );
    String key = context.getTypeRegistry().convertToText( textType, textValue );

    if ( key == null || key.isEmpty() ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    if ( context instanceof ReportFormulaContext ) {
      ReportFormulaContext fc = ReportFormulaContext.class.cast( context );
      try {
        ResourceBundleFactory factory = fc.getProcessingContext().getResourceBundleFactory();
        ResourceBundle bundle = factory.getResourceBundle( bundleId );
        if ( bundle != null && bundle.containsKey( key ) ) {
          key = bundle.getString( key );
        }
      } catch ( MissingResourceException | ClassCastException e ) {
        // no op
      }
    }

    return new TypeValuePair( TextType.TYPE, key );
  }
}
