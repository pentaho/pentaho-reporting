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
