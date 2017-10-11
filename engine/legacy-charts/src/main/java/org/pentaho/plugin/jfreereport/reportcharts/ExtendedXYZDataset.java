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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.data.xy.DefaultXYZDataset;

/**
 * This class is allows us to accumulate the maximum z value during data collection and hand that value to the chart at
 * render time - without creating a tighter coupling between the data and rendering classes.
 *
 * @author Gretchen Moran
 */
public class ExtendedXYZDataset extends DefaultXYZDataset {
  private static final long serialVersionUID = -6629387979880168707L;
  private double maxZValue;

  public ExtendedXYZDataset() {
  }

  public double getMaxZValue() {
    return maxZValue;
  }

  public void setMaxZValue( final double maxZValue ) {
    this.maxZValue = maxZValue;
  }
}
