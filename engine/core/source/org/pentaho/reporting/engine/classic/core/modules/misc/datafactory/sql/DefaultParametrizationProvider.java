package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.sql.Connection;

import org.pentaho.reporting.engine.classic.core.DataRow;

public class DefaultParametrizationProvider implements ParametrizationProvider
{
  private SQLParameterLookupParser parser;

  public DefaultParametrizationProvider()
  {
  }

  public String rewriteQueryForParametrization(final Connection connection,
                                               final String query,
                                               final DataRow dataRow)
  {
    parser = new SQLParameterLookupParser
        (SimpleSQLReportDataFactory.isExpandArrayParameterNeeded(query));
    return parser.translateAndLookup(query, dataRow);
  }

  public String[] getPreparedParameterNames()
  {
    return parser.getFields();
  }
}
