package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultDataFactoryCore implements DataFactoryCore
{
  public DefaultDataFactoryCore()
  {
  }

  public String[] getReferencedFields(final DataFactoryMetaData metaData,
                                      final DataFactory element,
                                      final String query,
                                      final DataRow parameter)
  {
    return null;
  }

  public ResourceReference[] getReferencedResources(final DataFactoryMetaData metaData,
                                                    final DataFactory element,
                                                    final ResourceManager resourceManager,
                                                    final String query, final DataRow parameter)
  {
    return new ResourceReference[0];
  }

  public String getDisplayConnectionName(final DataFactoryMetaData metaData,
                                         final DataFactory dataFactory)
  {
    return null;
  }

  public Object getQueryHash(final DataFactoryMetaData dataFactoryMetaData,
                             final DataFactory dataFactory,
                             final String queryName, final DataRow parameter)
  {
    return null;
  }
}
