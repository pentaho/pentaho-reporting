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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process.util;

public class PaginationTableState
{
  private PaginationTableState parent;
  private boolean rowOpen;
  private boolean suspended;
  private long pageHeight;
  private long pageEnd;

  public PaginationTableState(final long pageHeight, final long pageEnd)
  {
    this.pageHeight = pageHeight;
    this.pageEnd = pageEnd;
  }

  public PaginationTableState(final PaginationTableState parent)
  {
    this.parent = parent;
    this.suspended = parent.isVisualStateCollectionSuspended();
    this.pageHeight = parent.pageHeight;
    this.pageEnd = parent.pageEnd;
  }

  public PaginationTableState(final PaginationTableState parent, final boolean rowOpen, final boolean suspended)
  {
    this(parent);
    this.rowOpen = rowOpen;
    this.suspended |= suspended;
  }

  public long getPageHeight()
  {
    return pageHeight;
  }

  public long getPageEnd()
  {
    return pageEnd;
  }

  public boolean isVisualStateCollectionSuspended()
  {
    if (suspended)
    {
      return true;
    }

    if (rowOpen)
    {
      return true;
    }

    return false;
  }

  public PaginationTableState pop()
  {
    return parent;
  }
}
