package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

public class SQLDataFactoryCore extends DefaultDataFactoryCore
{
  private static final Log logger = LogFactory.getLog(SQLDataFactoryCore.class);
  private static final String CONNECTION_NAME = "::pentaho-reporting::name";

  public SQLDataFactoryCore()
  {
  }

  public String getDisplayConnectionName(final DataFactoryMetaData metaData,
                                         final DataFactory dataFactory)
  {
    final SimpleSQLReportDataFactory sqlDataFactory = (SimpleSQLReportDataFactory) dataFactory;
    final ConnectionProvider theConnectionProvider = sqlDataFactory.getConnectionProvider();
    if (theConnectionProvider instanceof DriverConnectionProvider)
    {
      final DriverConnectionProvider theDriverConnectionProvider = (DriverConnectionProvider) theConnectionProvider;
      return theDriverConnectionProvider.getProperty(CONNECTION_NAME);
    }
    if (theConnectionProvider instanceof JndiConnectionProvider)
    {
      final JndiConnectionProvider theDriverConnectionProvider = (JndiConnectionProvider) theConnectionProvider;
      return theDriverConnectionProvider.getConnectionPath();
    }
    return null;
  }

  public Object getQueryHash(final DataFactoryMetaData dataFactoryMetaData,
                             final DataFactory dataFactory,
                             final String queryName, final DataRow parameter)
  {
    final SimpleSQLReportDataFactory sqlDataFactory = (SimpleSQLReportDataFactory) dataFactory;
    return sqlDataFactory.getQueryHash(queryName, parameter);
  }

  public String[] getReferencedFields(final DataFactoryMetaData metaData,
                                      final DataFactory element,
                                      final String query,
                                      final DataRow parameter)
  {
    try
    {
    final SimpleSQLReportDataFactory sqlDataFactory = (SimpleSQLReportDataFactory) element;
    return sqlDataFactory.getReferencedFields(query, parameter);
  }
    catch (ReportDataFactoryException e)
    {
      logger.warn("Unable to compute referenced fields for query '" + query + "':", e);
      return null;
    }
  }
}
