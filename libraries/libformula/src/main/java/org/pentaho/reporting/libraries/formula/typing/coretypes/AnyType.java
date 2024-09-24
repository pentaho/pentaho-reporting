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
public final class AnyType extends DefaultType {
  public static final AnyType TYPE;
  public static final AnyType ANY_ARRAY;
  public static final AnyType ANY_SEQUENCE;
  private static final long serialVersionUID = 5871721530497016577L;

  static {
    TYPE = new AnyType();
    TYPE.addFlag( Type.SCALAR_TYPE );
    TYPE.lock();

    ANY_ARRAY = new AnyType();
    ANY_ARRAY.addFlag( Type.ARRAY_TYPE );
    ANY_ARRAY.lock();

    ANY_SEQUENCE = new AnyType();
    ANY_SEQUENCE.addFlag( Type.SEQUENCE_TYPE );
    ANY_SEQUENCE.lock();
  }

  private AnyType() {
    addFlag( Type.ANY_TYPE );
  }
}
