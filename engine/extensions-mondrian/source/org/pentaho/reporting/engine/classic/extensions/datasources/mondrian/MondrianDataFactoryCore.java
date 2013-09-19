package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class MondrianDataFactoryCore extends DefaultDataFactoryCore
{
  private static final Log logger = LogFactory.getLog(MondrianDataFactoryCore.class);

  public MondrianDataFactoryCore()
  {
  }

  public String getDisplayConnectionName(final DataFactoryMetaData metaData,
                                         final DataFactory dataFactory)
  {
    final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
    final String designTimeName = mdxDataFactory.getDesignTimeName();
    final CubeFileProvider cubeFileProvider = mdxDataFactory.getCubeFileProvider();

    final StringBuilder b = new StringBuilder();
    if (StringUtils.isEmpty(designTimeName) == false)
    {
      b.append(designTimeName);
    }

    if (cubeFileProvider != null)
    {
      final String cubeName = cubeFileProvider.getCubeConnectionName();
      final String fileName = cubeFileProvider.getDesignTimeFile();
      if (StringUtils.isEmpty(cubeName) == false)
      {
        if (b.length() != 0)
        {
          b.append(" - ");
        }
        b.append(cubeName);
      }
      if (StringUtils.isEmpty(fileName) == false)
      {
        if (b.length() != 0)
        {
          b.append(" ");
        }
        b.append("(");
        b.append(fileName);
        b.append(")");
      }
    }
    if (b.length() != 0)
    {
      return b.toString();
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
