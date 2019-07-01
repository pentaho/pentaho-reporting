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
 * Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.typing.NumberSequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.sequence.DefaultNumberSequence;

import java.math.BigDecimal;

/**
 * Creation-Date: 31.10.2006, 17:39:19
 *
 * @author Thomas Morgner
 */
public class SumAFunction extends SumFunction {

  public SumAFunction() {
  }

  @Override
  public String getCanonicalName() {
    return "SUMA";
  }

  @Override
  protected boolean isStrictSequenceNeeded() {
    return false;
  }

  @Override
  protected NumberSequence convertToNumberSequence( final FormulaContext context, final ParameterCallback parameters,
                                                    int paramIdx )
    throws EvaluationException {

    try {
      return super.convertToNumberSequence( context, parameters, paramIdx );
    } catch ( EvaluationException e ) {
      // Re throw the exception if an NA was found!
      if ( LibFormulaErrorValue.ERROR_NA == e.getErrorValue().getErrorCode() ) {
        throw e;
      }
    }

    // So, no auto conversion possible!
    Type type = parameters.getRaw( paramIdx ).getValueType();
    if ( type.isFlagSet( Type.TEXT_TYPE ) ) {
      return new DefaultNumberSequence( new StaticValue( BigDecimal.ZERO, NumberType.GENERIC_NUMBER ), context );
    } else if ( type.isFlagSet( Type.LOGICAL_TYPE ) ) {
      Boolean value = (Boolean) parameters.getRaw( paramIdx ).evaluate().getValue();
      return new DefaultNumberSequence(
        new StaticValue( ( value ) ? BigDecimal.ONE : BigDecimal.ZERO, NumberType.GENERIC_NUMBER ), context );
    }

    return new DefaultNumberSequence( context );
  }
}
