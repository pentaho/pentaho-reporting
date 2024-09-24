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
 * Creation-Date: 02.11.2006, 09:37:54
 *
 * @author Thomas Morgner
 */
public class NumberType extends DefaultType {
  public static final NumberType GENERIC_NUMBER;
  public static final NumberType GENERIC_NUMBER_ARRAY;
  public static final NumberType NUMBER_SEQUENCE;
  private static final long serialVersionUID = 2070930250111567639L;

  static {
    GENERIC_NUMBER = new NumberType();
    GENERIC_NUMBER.addFlag( Type.SCALAR_TYPE );
    GENERIC_NUMBER.addFlag( Type.NUMERIC_TYPE );
    GENERIC_NUMBER.lock();

    GENERIC_NUMBER_ARRAY = new NumberType();
    GENERIC_NUMBER_ARRAY.addFlag( Type.ARRAY_TYPE );
    GENERIC_NUMBER_ARRAY.lock();

    NUMBER_SEQUENCE = new NumberType();
    NUMBER_SEQUENCE.addFlag( Type.SEQUENCE_TYPE );
    NUMBER_SEQUENCE.addFlag( Type.NUMERIC_SEQUENCE_TYPE );
    NUMBER_SEQUENCE.lock();
  }

  public NumberType() {
    addFlag( Type.NUMERIC_TYPE );
  }
}
