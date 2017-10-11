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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;

/**
 * Creation-Date: 10.11.2006, 20:41:29
 *
 * @author Thomas Morgner
 */
public class QueryPhysicalPageInterceptor implements GraphicsContentInterceptor {
  private PageDrawable drawable;
  private PhysicalPageKey pageKey;

  public QueryPhysicalPageInterceptor( final PhysicalPageKey pageKey ) {
    if ( pageKey == null ) {
      throw new NullPointerException();
    }

    this.pageKey = pageKey;
  }

  public boolean isLogicalPageAccepted( final LogicalPageKey key ) {
    return false;
  }

  public void processLogicalPage( final LogicalPageKey key, final PageDrawable page ) {
  }

  public boolean isPhysicalPageAccepted( final PhysicalPageKey key ) {
    return pageKey.equals( key );
  }

  public void processPhysicalPage( final PhysicalPageKey key, final PageDrawable page ) {
    this.drawable = page;
  }

  public boolean isMoreContentNeeded() {
    return drawable == null;
  }

  public PageDrawable getDrawable() {
    return drawable;
  }
}
