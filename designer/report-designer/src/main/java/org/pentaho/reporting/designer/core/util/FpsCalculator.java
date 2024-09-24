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

package org.pentaho.reporting.designer.core.util;

/**
 * Implements a simple FPS calculator using a simple average.
 */
public class FpsCalculator {
  private int frames;
  private long startTime;
  private long endTime;
  private boolean active;

  public FpsCalculator() {
    reset();
  }

  public boolean isActive() {
    return active;
  }

  public void setActive( final boolean active ) {
    this.active = active;
  }

  public void reset() {
    startTime = 0;
    endTime = 0;
    frames = 0;
  }

  public void tick() {
    if ( startTime == 0 ) {
      startTime = System.currentTimeMillis();
    }
    frames += 1;
    endTime = System.currentTimeMillis();
  }

  public double getFps() {
    if ( frames == 0 ) {
      return 0;
    }
    final double time = ( endTime - startTime ) / 1000.0;
    return (double) frames / time;
  }
}
