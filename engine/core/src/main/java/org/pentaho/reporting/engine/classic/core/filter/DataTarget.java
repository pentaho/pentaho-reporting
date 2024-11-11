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


package org.pentaho.reporting.engine.classic.core.filter;

import java.io.Serializable;

/**
 * A DataTarget is a consumer in the DataProcessing chain. All Elements are DataTargets. Targets query their data from
 * assigned DataSources.
 *
 * @author Thomas Morgner
 */
public interface DataTarget extends Serializable, Cloneable {
  /**
   * Returns the assigned DataSource for this Target.
   *
   * @return The datasource.
   */
  public DataSource getDataSource();

  /**
   * Assigns a DataSource for this Target.
   *
   * @param ds
   *          The data source.
   */
  public void setDataSource( DataSource ds );

  /**
   * Clones this datatarget.
   *
   * @return a clone of the datatarget.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException;
}
