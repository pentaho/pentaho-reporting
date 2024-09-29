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


package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.libraries.base.util.EmptyIterator;

import java.util.Iterator;

public class OutputFunctionLevelStorage implements LevelStorage {
  private class GenericIterator<T> implements Iterator<T> {
    private boolean hasNextElement;
    private T element;

    private GenericIterator( T element ) {
      this.hasNextElement = true;
      this.element = element;
    }

    public boolean hasNext() {
      return hasNextElement;
    }

    public T next() {
      if ( hasNextElement == false ) {
        throw new IllegalStateException();
      }
      hasNextElement = false;
      return element;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private final Function outputFunction;
  private final int level;
  private final boolean pageListener;

  public OutputFunctionLevelStorage( final int level, final Function outputFunction, final boolean pageListener ) {
    if ( outputFunction == null ) {
      throw new NullPointerException();
    }
    this.level = level;
    this.pageListener = pageListener;
    this.outputFunction = outputFunction;
  }

  public int getLevelNumber() {
    return level;
  }

  public Iterator<Function> getFunctions() {
    return new GenericIterator<Function>( outputFunction );
  }

  public Iterator<Function> getPageFunctions() {
    if ( pageListener ) {
      return new GenericIterator<Function>( outputFunction );
    } else {
      return EmptyIterator.emptyIterator();
    }
  }

  public Iterator<Expression> getActiveExpressions() {
    return new GenericIterator<Expression>( outputFunction );
  }
}
