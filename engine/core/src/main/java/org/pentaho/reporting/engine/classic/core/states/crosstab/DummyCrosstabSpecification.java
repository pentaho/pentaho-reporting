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

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

public class DummyCrosstabSpecification implements CrosstabSpecification {
  private ReportStateKey key;
  private static final String[] EMPTY_NAMES = new String[0];
  private static final Object[] EMPTY_ROWS = new Object[0];

  public DummyCrosstabSpecification( final ReportStateKey key ) {
    this.key = key;
  }

  public ReportStateKey getKey() {
    return key;
  }

  public void startRow() {

  }

  public void endRow() {

  }

  public void add( final DataRow dataRow ) {

  }

  public void endCrosstab() {

  }

  public String[] getColumnDimensionNames() {
    return EMPTY_NAMES;
  }

  public String[] getRowDimensionNames() {
    return EMPTY_NAMES;
  }

  public Object[] getKeyAt( final int column ) {
    return EMPTY_ROWS;
  }

  public int indexOf( final int startPosition, final Object[] key ) {
    return -1;
  }

  public int size() {
    return 0;
  }
}
