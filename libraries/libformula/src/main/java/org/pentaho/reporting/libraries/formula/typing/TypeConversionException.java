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

package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

/**
 * Creation-Date: 10.04.2007, 14:13:40
 *
 * @author Thomas Morgner
 */
public class TypeConversionException extends EvaluationException {
  private static final long serialVersionUID = -12507914833915502L;
  private static ThreadLocal localInstance = new ThreadLocal();

  protected TypeConversionException() {
    super( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
  }

  public static TypeConversionException getInstance() {
    final TypeConversionException o = (TypeConversionException) localInstance.get();
    if ( o == null ) {
      final TypeConversionException retval = new TypeConversionException();
      localInstance.set( retval );
      return retval;
    }

    o.fillInStackTrace();
    return o;
  }
}
