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
