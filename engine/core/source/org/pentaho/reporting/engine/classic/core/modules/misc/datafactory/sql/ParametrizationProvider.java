package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.Serializable;
import java.sql.Connection;

import org.pentaho.reporting.engine.classic.core.DataRow;

public interface ParametrizationProvider extends Serializable
{
  public String rewriteQueryForParametrization(Connection connection, String query, DataRow parameters);
  public String[] getPreparedParameterNames();
}