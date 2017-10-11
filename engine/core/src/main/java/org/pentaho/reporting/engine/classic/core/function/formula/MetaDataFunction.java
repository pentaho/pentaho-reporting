/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

public class MetaDataFunction implements Function {
  public MetaDataFunction() {
  }

  public String getCanonicalName() {
    return "METADATA";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 3 && parameterCount != 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final String fieldName =
        context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
    final String metaNamespace =
        context.getTypeRegistry().convertToText( parameters.getType( 1 ), parameters.getValue( 1 ) );
    final String metaName = context.getTypeRegistry().convertToText( parameters.getType( 2 ), parameters.getValue( 2 ) );
    final Class type;
    if ( parameterCount == 4 ) {
      final String typeStr =
          context.getTypeRegistry().convertToText( parameters.getType( 3 ), parameters.getValue( 3 ) );
      final ClassLoader loader = ObjectUtilities.getClassLoader( MetaDataFunction.class );
      try {
        type = Class.forName( typeStr, false, loader );
      } catch ( ClassNotFoundException e ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
      }
    } else {
      type = String.class;
    }

    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    final DataAttributes data = rfc.getDataSchema().getAttributes( fieldName );
    if ( data == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final DefaultDataAttributeContext attributeContext =
        new DefaultDataAttributeContext( rfc.getProcessingContext().getOutputProcessorMetaData(), rfc
            .getLocalizationContext().getLocale() );
    final Object o = data.getMetaAttribute( metaNamespace, metaName, type, attributeContext );
    return new TypeValuePair( AnyType.TYPE, o );
  }
}
