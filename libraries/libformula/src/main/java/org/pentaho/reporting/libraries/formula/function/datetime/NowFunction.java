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

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.util.Date;

/**
 * Return the serial number of the current date and time. This returns the current day and time serial number, using the
 * current locale. If you want only the serial number of the current day, use TODAY.
 *
 * @author Thomas Morgner
 * @since 23.03.2007
 */
public class NowFunction implements Function {
  private static final long serialVersionUID = 4108282053598696841L;

  public NowFunction() {
  }

  public String getCanonicalName() {
    return "NOW";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final Date now = DateUtil.now( context );

    final Date date = DateUtil.normalizeDate( now, DateTimeType.DATETIME_TYPE );
    return new TypeValuePair( DateTimeType.DATETIME_TYPE, date );
  }
}
