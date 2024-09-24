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
