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


package org.pentaho.reporting.libraries.fonts.registry;

/**
 * Creation-Date: 24.07.2006, 18:36:21
 *
 * @author Thomas Morgner
 */
public final class BaselineInfo {
  public static final int HANGING = 0;
  public static final int MATHEMATICAL = 1;
  public static final int CENTRAL = 2;
  public static final int MIDDLE = 3;
  public static final int ALPHABETIC = 4;
  public static final int IDEOGRAPHIC = 5;

  private long[] baselines;
  private int dominantBaseline;

  public BaselineInfo() {
    this.baselines = new long[ 6 ];
  }

  public long[] getBaselines() {
    return (long[]) baselines.clone();
  }

  public void update( final BaselineInfo parent ) {
    System.arraycopy( parent.baselines, 0, this.baselines, 0, 6 );
    dominantBaseline = parent.dominantBaseline;
  }

  public void setBaselines( final long[] baselines ) {
    if ( baselines.length != 6 ) {
      throw new IllegalArgumentException();
    }
    System.arraycopy( baselines, 0, this.baselines, 0, 6 );
  }

  public long getBaseline( final int indx ) {
    return baselines[ indx ];
  }

  public void setBaseline( final int idx, final long baseline ) {
    baselines[ idx ] = baseline;
  }

  public int getDominantBaseline() {
    return dominantBaseline;
  }

  public void setDominantBaseline( final int dominantBaseline ) {
    this.dominantBaseline = dominantBaseline;
  }
}
