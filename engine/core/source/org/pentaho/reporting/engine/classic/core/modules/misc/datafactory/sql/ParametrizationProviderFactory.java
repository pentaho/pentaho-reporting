package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.Serializable;
import java.sql.Connection;

public interface ParametrizationProviderFactory extends Serializable
{
  public ParametrizationProvider create(Connection connection); 
}
