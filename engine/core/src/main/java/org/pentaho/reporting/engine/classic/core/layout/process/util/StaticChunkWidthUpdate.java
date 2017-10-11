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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

public abstract class StaticChunkWidthUpdate {
  private StaticChunkWidthUpdate parent;

  protected StaticChunkWidthUpdate() {
  }

  protected void reuse( final StaticChunkWidthUpdate parent ) {
    this.parent = parent;
  }

  public abstract void update( long minChunkWidth );

  public void finish() {

  }

  public boolean isInline() {
    return false;
  }

  public StaticChunkWidthUpdate pop() {
    final StaticChunkWidthUpdate retval = parent;
    parent = null;
    return retval;
  }
}
