package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import javax.sql.DataSource;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.JndiDataSourceService;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;

public class PooledWithJndiDataSourceService extends PooledDatasourceService
{
  private JndiDataSourceService fallbackService;

  public PooledWithJndiDataSourceService()
  {
    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    fallbackService = objectFactory.get(JndiDataSourceService.class);
  }

  protected DataSource queryFallback(final String dataSource)
  {
    return fallbackService.getDataSource(dataSource);
  }
}
