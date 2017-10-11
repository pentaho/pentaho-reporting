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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
