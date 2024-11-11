/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
