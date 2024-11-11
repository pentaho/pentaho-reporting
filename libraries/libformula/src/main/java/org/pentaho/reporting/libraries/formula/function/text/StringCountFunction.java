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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * This function counts the occurences of search_text in text.
 *
 * @author Gunter Rombauts
 */
public class StringCountFunction implements Function {
  private static final long serialVersionUID = -1557813953499941337L;

  public StringCountFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );
    final Type searchTextType = parameters.getType( 1 );
    final Object searchTextValue = parameters.getValue( 1 );

    final String text = typeRegistry.convertToText( textType, textValue );
    final String searchText = typeRegistry.convertToText( searchTextType, searchTextValue );
    if ( searchText.length() == 0 ) {
      return new TypeValuePair( NumberType.GENERIC_NUMBER, 0 );
    }

    int index = text.indexOf( searchText );
    if ( index == -1 ) {
      return new TypeValuePair( NumberType.GENERIC_NUMBER, 0 );
    }

    int occcounter = 0;
    while ( index >= 0 ) {
      final int oldIndex = index + searchText.length();

      index = text.indexOf( searchText, oldIndex );
      occcounter += 1;
    }
    return new TypeValuePair( NumberType.GENERIC_NUMBER, occcounter );
  }

  public String getCanonicalName() {
    return "STRINGCOUNT";
  }

}
