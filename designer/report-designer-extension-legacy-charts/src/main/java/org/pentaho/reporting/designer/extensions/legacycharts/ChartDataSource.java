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

package org.pentaho.reporting.designer.extensions.legacycharts;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

public enum ChartDataSource {
  CATEGORY( CategoryDataset.class ),
  PIE( PieDataset.class ),
  XY( XYDataset.class ),
  XYZ( XYZDataset.class ),
  VALUE( ValueDataset.class );

  private Class resultType;

  private ChartDataSource( final Class resultType ) {
    this.resultType = resultType;
  }

  public Class getResultType() {
    return resultType;
  }
}
