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
