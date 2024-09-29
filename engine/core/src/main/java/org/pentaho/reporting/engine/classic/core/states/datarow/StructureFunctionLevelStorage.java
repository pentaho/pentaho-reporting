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

public class StructureFunctionLevelStorage implements LevelStorage, Iterator<Function> {
  private int level;
  private boolean[] pageListener;
  private Function[] outputFunction;
  private int cursor;
  private boolean searchPageListeners;

  public StructureFunctionLevelStorage( final int level, final Function[] outputFunction, final boolean[] pageListener ) {
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
    this.cursor = 0;
    this.searchPageListeners = false;
    return this;
  }

  public Iterator<Function> getPageFunctions() {
    this.cursor = -1;
    this.cursor = findNextIndex();
    this.searchPageListeners = true;
    return this;
  }

  private int findNextIndex() {
    if ( searchPageListeners == false ) {
      return cursor + 1;
    }

    while ( cursor < outputFunction.length ) {
      cursor += 1;
      if ( cursor < outputFunction.length && pageListener[cursor] ) {
        break;
      }
    }
    return outputFunction.length;
  }

  public boolean hasNext() {
    return cursor < outputFunction.length;
  }

  public Function next() {
    if ( cursor < outputFunction.length == false ) {
      throw new IllegalStateException();
    }
    final Function retval = outputFunction[cursor];
    cursor = findNextIndex();
    return retval;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public Iterator<Expression> getActiveExpressions() {
    return EmptyIterator.emptyIterator();
  }
}
