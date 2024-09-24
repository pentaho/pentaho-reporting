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

/**
 * @deprecated This interface is ill-defined.
 */
public interface ICollectorFunction {

  /**
   * @return
   * @deprecated This methos is never used.
   */
  public Object getValue();

  public Object getDatasourceValue();

  /**
   * @return
   * @deprecated This methos is never used.
   */
  public String getGroup();

  public Object getCacheKey();

}
