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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.states.datarow;

import java.util.Iterator;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.libraries.base.util.EmptyIterator;

public class OutputFunctionLevelStorage implements LevelStorage, Iterator<Function>
{
  private int level;
  private boolean pageListener;
  private Function outputFunction;
  private boolean hasNextElement;

  public OutputFunctionLevelStorage(final int level,
                                    final Function outputFunction, final boolean pageListener)
  {
    if (outputFunction == null)
    {
      throw new NullPointerException();
    }
    this.level = level;
    this.pageListener = pageListener;
    this.outputFunction = outputFunction;
  }

  public int getLevelNumber()
  {
    return level;
  }

  public Iterator<Function> getFunctions()
  {
    this.hasNextElement = true;
    return this;
  }

  public Iterator<Function> getPageFunctions()
  {
    if (pageListener)
    {
      this.hasNextElement = true;
    }
    else
    {
      this.hasNextElement = false;
    }
    return this;
  }

  public boolean hasNext()
  {
    return hasNextElement;
  }

  public Function next()
  {
    if (hasNextElement == false)
    {
      throw new IllegalStateException();
    }
    hasNextElement = false;
    return outputFunction;
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  public Iterator<Expression> getActiveExpressions()
  {
    return EmptyIterator.emptyIterator();
  }
}
