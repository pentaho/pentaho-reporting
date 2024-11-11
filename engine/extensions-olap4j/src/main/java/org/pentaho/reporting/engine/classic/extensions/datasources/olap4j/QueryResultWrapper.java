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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.olap4j.CellSet;
import org.olap4j.PreparedOlapStatement;

public class QueryResultWrapper {
  private CellSet cellSet;
  private PreparedOlapStatement statement;

  public QueryResultWrapper( final PreparedOlapStatement statement, final CellSet cellSet ) {
    this.statement = statement;
    this.cellSet = cellSet;
  }

  public CellSet getCellSet() {
    return cellSet;
  }

  public PreparedOlapStatement getStatement() {
    return statement;
  }
}
