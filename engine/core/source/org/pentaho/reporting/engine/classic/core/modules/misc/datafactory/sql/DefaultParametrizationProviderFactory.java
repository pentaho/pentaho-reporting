package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.sql.Connection;

public class DefaultParametrizationProviderFactory implements ParametrizationProviderFactory
{
  public DefaultParametrizationProviderFactory()
  {
  }

  public ParametrizationProvider create(final Connection connection)
  {
    return new DefaultParametrizationProvider();
  }
}
