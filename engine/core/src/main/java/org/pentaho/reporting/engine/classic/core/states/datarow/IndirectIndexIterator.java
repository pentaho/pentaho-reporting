/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.Iterator;

class IndirectIndexIterator<T extends Expression> implements Iterator<T> {
  private int[] indices;
  private Expression[] expressions;
  private int index;

  IndirectIndexIterator( final int[] indices, final Expression[] expressions ) {
    if ( indices == null ) {
      throw new IllegalStateException();
    }
    if ( expressions == null ) {
      throw new IllegalStateException();
    }

    this.indices = indices;
    this.expressions = expressions;
    this.index = 0;
  }

  public boolean hasNext() {
    return index < indices.length;
  }

  public T next() {
    final int indexForExpression = indices[index];
    final T value = (T) expressions[indexForExpression];
    index += 1;
    return value;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
