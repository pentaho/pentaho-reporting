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


package org.pentaho.reporting.libraries.formula.typing.coretypes;

import org.pentaho.reporting.libraries.formula.typing.DefaultType;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * This class regroups all related Types to date and time values.
 *
 * @author Cedric Pronzato
 */
public class DateTimeType extends DefaultType {
  /**
   * This Type represents an instant in time described by a date and a time of day.
   */
  public static final DateTimeType DATETIME_TYPE;

  /**
   * This Type represents an instant in time described by a date only.
   */
  public static final DateTimeType DATE_TYPE;

  /**
   * This Type represents an instant in time described by a time of day only.
   */
  public static final DateTimeType TIME_TYPE;
  private static final long serialVersionUID = -9003249643428553897L;

  static {
    DATE_TYPE = new DateTimeType();
    DATE_TYPE.addFlag( Type.DATE_TYPE );
    DATE_TYPE.addFlag( Type.NUMERIC_TYPE );
    DATE_TYPE.lock();

    TIME_TYPE = new DateTimeType();
    TIME_TYPE.addFlag( Type.TIME_TYPE );
    TIME_TYPE.addFlag( Type.NUMERIC_TYPE );
    TIME_TYPE.lock();

    DATETIME_TYPE = new DateTimeType();
    DATETIME_TYPE.addFlag( Type.DATETIME_TYPE );
    DATETIME_TYPE.addFlag( Type.NUMERIC_TYPE );
    DATETIME_TYPE.lock();
  }

  protected DateTimeType() {
    addFlag( Type.NUMERIC_TYPE );
    addFlag( Type.SCALAR_TYPE );
  }
}
