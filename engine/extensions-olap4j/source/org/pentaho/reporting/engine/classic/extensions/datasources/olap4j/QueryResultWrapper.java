package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.olap4j.CellSet;
import org.olap4j.PreparedOlapStatement;

public class QueryResultWrapper
{
  private CellSet cellSet;
  private PreparedOlapStatement statement;

  public QueryResultWrapper(final PreparedOlapStatement statement, final CellSet cellSet)
  {
    this.statement = statement;
    this.cellSet = cellSet;
  }

  public CellSet getCellSet()
  {
    return cellSet;
  }

  public PreparedOlapStatement getStatement()
  {
    return statement;
  }
}
