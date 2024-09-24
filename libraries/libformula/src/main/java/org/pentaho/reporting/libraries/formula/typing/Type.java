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

import java.io.Serializable;

public interface Type extends Serializable {
  public static final int ANY_TYPE = 0x000001; // "type.any";
  public static final int TEXT_TYPE = 0x000002; //"type.text";
  public static final int ERROR_TYPE = 0x000004; // "type.error";
  public static final int SCALAR_TYPE = 0x000008; // "type.scalar";
  public static final int NUMERIC_TYPE = 0x000010; //"type.numeric";
  public static final int LOGICAL_TYPE = 0x000020; //"type.logical";
  public static final int DATE_TYPE = 0x000040; // "type.date";
  public static final int TIME_TYPE = 0x000080; // "type.time";
  public static final int DATETIME_TYPE = 0x000100; // "type.datetime";
  public static final int ARRAY_TYPE = 0x000200; // "type.array";
  public static final int SEQUENCE_TYPE = 0x000400; // "type.sequence";
  public static final int NUMERIC_SEQUENCE_TYPE = 0x000800; //"type.numeric.sequence";
  public static final int NUMERIC_UNIT = 0x001000; // "unit.numeric";

  public boolean isFlagSet( int name );
}
