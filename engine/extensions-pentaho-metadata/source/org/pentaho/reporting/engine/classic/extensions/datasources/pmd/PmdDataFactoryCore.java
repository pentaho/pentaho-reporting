package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

public class PmdDataFactoryCore extends DefaultDataFactoryCore
{
  private static final Log logger = LogFactory.getLog(PmdDataFactoryCore.class);

  public PmdDataFactoryCore()
  {
  }

  public String[] getReferencedFields(final DataFactoryMetaData metaData,
                                      final DataFactory element,
                                      final String query,
                                      final DataRow parameter)
  {
    try
    {
      final SimplePmdDataFactory dataFactory = (SimplePmdDataFactory) element;
      return dataFactory.getReferencedFields(query, parameter);
    }
    catch (ReportDataFactoryException e)
    {
      logger.warn("Unable to compute referenced fields for query '" + query + "':", e);
      return null;
    }
  }

  public Object getQueryHash(final DataFactoryMetaData dataFactoryMetaData,
                             final DataFactory dataFactory,
                             final String queryName,
                             final DataRow parameter)
  {
    final SimplePmdDataFactory pmdDataFactory = (SimplePmdDataFactory) dataFactory;
    return pmdDataFactory.getQueryHash(queryName, parameter);
  }
}
