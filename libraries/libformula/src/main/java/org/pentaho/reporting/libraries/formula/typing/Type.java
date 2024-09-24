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
