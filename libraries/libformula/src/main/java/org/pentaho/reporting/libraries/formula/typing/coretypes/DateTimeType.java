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
