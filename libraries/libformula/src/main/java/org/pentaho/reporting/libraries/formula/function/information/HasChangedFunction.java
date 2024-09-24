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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Creation-Date: 24.11.2006, 13:02:41
 *
 * @author Thomas Morgner
 */
public class HasChangedFunction implements Function {
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final long serialVersionUID = 5952911264465883971L;

  public HasChangedFunction() {
  }

  public String getCanonicalName() {
    return "HASCHANGED";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    // we expect strings and will check, whether the reference for theses
    // strings is dirty.

    final int parCount = parameters.getParameterCount();
    for ( int i = 0; i < parCount; i++ ) {
      final Object value = parameters.getValue( i );
      if ( value == null ) {
        continue;
      }

      if ( context.isReferenceDirty( value ) ) {
        return RETURN_TRUE;
      }
    }
    return RETURN_FALSE;
  }
}
