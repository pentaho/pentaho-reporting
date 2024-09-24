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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * This function returns text where an old text is substituted with a new text.
 *
 * @author Cedric Pronzato
 */
public class SubstituteFunction implements Function {
  private static final long serialVersionUID = -1557813953499941337L;

  public SubstituteFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 3 || parameterCount > 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );
    final Type oldTextType = parameters.getType( 1 );
    final Object oldTextValue = parameters.getValue( 1 );

    final String text = typeRegistry.convertToText( textType, textValue );
    final String oldText = typeRegistry.convertToText( oldTextType, oldTextValue );
    if ( oldText.length() == 0 ) {
      return new TypeValuePair( TextType.TYPE, text );
    }

    final Type newTextType = parameters.getType( 2 );
    final Object newTextValue = parameters.getValue( 2 );
    final String newText = typeRegistry.convertToText( newTextType, newTextValue );
    if ( parameterCount == 3 ) {
      int oldIndex = 0;
      int index = text.indexOf( oldText );
      if ( index == -1 ) {
        return new TypeValuePair( TextType.TYPE, text );
      }

      final StringBuffer result = new StringBuffer( text.length() );
      while ( index >= 0 ) {
        result.append( text.substring( oldIndex, index ) );
        result.append( newText );
        oldIndex = index + oldText.length();

        index = text.indexOf( oldText, oldIndex );
      }
      result.append( text.substring( oldIndex ) );
      return new TypeValuePair( TextType.TYPE, result.toString() );
    }

    // Instead of replacing all occurences, the user only requested to replace
    // a specific one.
    final Type whichType = parameters.getType( 3 );
    final Object whichValue = parameters.getValue( 3 );
    final Number n = typeRegistry.convertToNumber( whichType, whichValue );
    if ( n.doubleValue() < 1 ) {
      throw EvaluationException.getInstance(
        LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final int nthOccurence = n.intValue();

    int index = text.indexOf( oldText );
    if ( index == -1 ) {
      return new TypeValuePair( TextType.TYPE, text );
    }

    String result = text;
    int counter = 1;
    while ( index >= 0 ) {
      if ( counter == nthOccurence ) {
        final StringBuffer buffer = new StringBuffer( result );
        buffer.replace( index, index + oldText.length(), newText );
        result = buffer.toString();
        return new TypeValuePair( TextType.TYPE, result );
      }

      index = result.indexOf( oldText, index + 1 );
      counter += 1;
    }
    return new TypeValuePair( TextType.TYPE, result );
  }

  public String getCanonicalName() {
    return "SUBSTITUTE";
  }

}
