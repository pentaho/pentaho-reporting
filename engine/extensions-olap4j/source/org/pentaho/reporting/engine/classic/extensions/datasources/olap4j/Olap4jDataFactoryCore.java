package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

public class Olap4jDataFactoryCore extends DefaultDataFactoryCore
{
  private static final Log logger = LogFactory.getLog(Olap4jDataFactoryCore.class);

  public Olap4jDataFactoryCore()
  {
  }

  public String getDisplayConnectionName(final DataFactoryMetaData metaData,
                                         final DataFactory dataFactory)
  {
    final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
    final OlapConnectionProvider connectionProvider = mdxDataFactory.getConnectionProvider();
    if (connectionProvider instanceof DriverConnectionProvider)
    {
      final DriverConnectionProvider driverConnectionProvider = (DriverConnectionProvider) connectionProvider;
      return driverConnectionProvider.getProperty("::pentaho-reporting::name");
    }
    else if (connectionProvider instanceof JndiConnectionProvider)
    {
      final JndiConnectionProvider jndiConnectionProvider = (JndiConnectionProvider) connectionProvider;
      return jndiConnectionProvider.getConnectionPath();
    }
    return null;
  }

  public Object getQueryHash(final DataFactoryMetaData dataFactoryMetaData,
                             final DataFactory dataFactory,
                             final String queryName, final DataRow parameter)
  {
    try
    {
      final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
      return mdxDataFactory.getQueryHash(queryName, parameter);
    }
    catch (ReportDataFactoryException e)
    {
      logger.warn("Unable to create query hash", e);
      return null;
    }
  }

  public String[] getReferencedFields(final DataFactoryMetaData metaData,
                                      final DataFactory element,
                                      final String query,
                                      final DataRow parameter)
  {
    try
    {
      final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) element;
      return mdxDataFactory.getReferencedFields(query, parameter);
    }
    catch (ReportDataFactoryException e)
    {
      logger.warn("Unable to collect referenced fields", e);
      return null;
    }
  }
}
