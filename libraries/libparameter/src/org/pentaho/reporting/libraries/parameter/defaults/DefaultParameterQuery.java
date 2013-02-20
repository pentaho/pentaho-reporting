package org.pentaho.reporting.libraries.parameter.defaults;

import java.util.HashMap;

import org.pentaho.reporting.libraries.parameter.ParameterData;
import org.pentaho.reporting.libraries.parameter.ParameterDataTable;
import org.pentaho.reporting.libraries.parameter.ParameterException;
import org.pentaho.reporting.libraries.parameter.ParameterQuery;

public class DefaultParameterQuery implements ParameterQuery
{
  private HashMap<String, ParameterDataTable> backend;

  public DefaultParameterQuery()
  {
    backend = new HashMap<String, ParameterDataTable>();
  }

  public String[] getQueryNames ()
  {
    return backend.keySet().toArray(new String[backend.size()]);
  }

  public ParameterDataTable getQuery (final String queryId)
  {
    return backend.get(queryId);
  }

  public void setQuery (final String queryId, final ParameterDataTable data)
  {
    backend.put(queryId, data);
  }

  public ParameterDataTable performQuery(final String queryIdentifier,
                                         final ParameterData parameterData) throws ParameterException
  {
    final ParameterDataTable parameterDataTable = backend.get(queryIdentifier);
    if (parameterDataTable == null)
    {
      throw new ParameterException("Invalid query-identifier: " + queryIdentifier);
    }
    return parameterDataTable;
  }
}
