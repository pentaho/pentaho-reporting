/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Creation-Date: 02.11.2006, 10:27:03
 *
 * @author Thomas Morgner
 */
public class PercentageOperator implements PostfixOperator {
  private static final BigDecimal HUNDRED = new BigDecimal( 100.0 );
  private static final long serialVersionUID = -5578115447971169716L;


  public PercentageOperator() {
  }


  public TypeValuePair evaluate( final FormulaContext context, final TypeValuePair value1 )
    throws EvaluationException {
    final Object rawValue = value1.getValue();
    if ( rawValue == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final Type type = value1.getType();
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    if ( type.isFlagSet( Type.NUMERIC_TYPE ) == false &&
      type.isFlagSet( Type.ANY_TYPE ) == false ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    // return the same as zero minus value.
    final Number number = typeRegistry.convertToNumber( type, rawValue );
    final BigDecimal value = NumberUtil.getAsBigDecimal( number );
    final BigDecimal percentage = NumberUtil.divide( value, HUNDRED );
    return new TypeValuePair( NumberType.GENERIC_NUMBER, percentage );
  }

  public String toString() {
    return "%";
  }

}

