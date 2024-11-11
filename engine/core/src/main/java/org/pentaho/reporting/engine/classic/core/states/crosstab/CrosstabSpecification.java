/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.states.crosstab;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

import java.io.Serializable;

public interface CrosstabSpecification extends Serializable {
  public ReportStateKey getKey();

  public void startRow();

  public void endRow();

  public void add( final DataRow dataRow );

  public String[] getColumnDimensionNames();

  public String[] getRowDimensionNames();

  public Object[] getKeyAt( int column );

  public int indexOf( final int startPosition, Object[] key );

  /**
   * The number of columns encountered.
   *
   * @return
   */
  public int size();

  public void endCrosstab();
}
