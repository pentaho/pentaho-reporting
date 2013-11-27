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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

public class BlockLevelPaginationShiftState implements PaginationShiftState
{
  private PaginationShiftState parent;
  private long shift;
  private long initialShift;
  private boolean breakSuspended;
  private StackedObjectPool<BlockLevelPaginationShiftState> pool;

  public BlockLevelPaginationShiftState()
  {
  }

  public BlockLevelPaginationShiftState(final PaginationShiftState parent)
  {
    reuse(null, parent);
  }

  public void reuse(final StackedObjectPool<BlockLevelPaginationShiftState> pool,
                    final PaginationShiftState parent)
  {
    if (parent == null)
    {
      throw new NullPointerException();
    }
    this.pool = pool;
    this.parent = parent;
    this.initialShift = parent.getShiftForNextChild();
    this.shift = initialShift;
    this.breakSuspended = parent.isManualBreakSuspendedForChilds();
  }

  public void suspendManualBreaks()
  {
    breakSuspended = true;
  }

  public boolean isManualBreakSuspended()
  {
    return parent.isManualBreakSuspendedForChilds();
  }

  public boolean isManualBreakSuspendedForChilds()
  {
    return breakSuspended;
  }

  public void updateShiftFromChild(final long absoluteValue)
  {
    setShift(absoluteValue);
  }

  public void increaseShift(final long value)
  {
    if (value < 0)
    {
      throw new IllegalStateException();
    }
    this.shift += value;
  }

  public long getShiftForNextChild()
  {
    return shift;
  }

  public void setShift(final long value)
  {
    if (value < shift)
    {
      throw new IllegalStateException("Cannot shift backwards");
    }

    this.shift = value;
  }

  public PaginationShiftState pop()
  {
    parent.updateShiftFromChild(this.shift);
    if (this.pool != null)
    {
      this.pool.free(this);
      this.pool = null;
    }
    return parent;
  }
}
