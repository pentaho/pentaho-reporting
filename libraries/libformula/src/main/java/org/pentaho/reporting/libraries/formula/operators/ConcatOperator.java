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

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Concats two strings operator.
 *
 * @author Thomas Morgner
 */
public class ConcatOperator implements InfixOperator {
  private static final long serialVersionUID = 6579968694761281257L;

  public ConcatOperator() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final TypeValuePair value1,
                                 final TypeValuePair value2 )
    throws EvaluationException {
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    // Error or empty string, that's the question ..
    final Object raw1 = value1.getValue();
    final Object raw2 = value2.getValue();
    if ( raw1 == null || raw2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final String text1 = typeRegistry.convertToText( value1.getType(), raw1 );
    final String text2 = typeRegistry.convertToText( value2.getType(), raw2 );
    if ( text1 == null && text2 == null ) {
      throw EvaluationException.getInstance
        ( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    if ( text1 == null ) {
      return new TypeValuePair( TextType.TYPE, text2 );
    }
    if ( text2 == null ) {
      return new TypeValuePair( TextType.TYPE, text1 );
    }

    return new TypeValuePair( TextType.TYPE, text1 + text2 );
  }

  public int getLevel() {
    return 300;
  }


  public String toString() {
    return "&";
  }

  public boolean isLeftOperation() {
    return true;
  }

  /**
   * Defines, whether the operation is associative. For associative operations, the evaluation order does not matter, if
   * the operation appears more than once in an expression, and therefore we can optimize them a lot better than
   * non-associative operations (ie. merge constant parts and precompute them once).
   *
   * @return true, if the operation is associative, false otherwise
   */
  public boolean isAssociative() {
    return false;
  }

}
