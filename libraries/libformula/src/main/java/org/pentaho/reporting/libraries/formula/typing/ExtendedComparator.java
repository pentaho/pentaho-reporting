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

import org.pentaho.reporting.libraries.formula.EvaluationException;

/**
 * A comparator, that offers type support. Unlike the plain Java-Comparator, this class is able to compare
 *
 * @author Thomas Morgner
 */
public interface ExtendedComparator {
  public boolean isEqual( final Type type1,
                          final Object value1,
                          final Type type2,
                          final Object value2 );

  /**
   * Returns null, if the types are not comparable and are not convertible at all.
   *
   * @param type1
   * @param value1
   * @param type2
   * @param value2
   * @return
   */
  public int compare( final Type type1,
                      final Object value1,
                      final Type type2,
                      final Object value2 ) throws EvaluationException;
}
