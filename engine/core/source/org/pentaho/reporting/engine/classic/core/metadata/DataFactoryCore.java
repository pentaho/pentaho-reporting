package org.pentaho.reporting.engine.classic.core.metadata;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface DataFactoryCore extends Serializable
{
  public String[] getReferencedFields(DataFactoryMetaData metaData,
                                      DataFactory element,
                                      String query, final DataRow parameter);

  public ResourceReference[] getReferencedResources(DataFactoryMetaData metaData,
                                                    DataFactory element,
                                                    ResourceManager resourceManager,
                                                    String query, final DataRow parameter);

  public String getDisplayConnectionName(final DataFactoryMetaData metaData,
                                    final DataFactory dataFactory);

  public Object getQueryHash(DataFactoryMetaData dataFactoryMetaData,
                             DataFactory dataFactory,
                             String queryName,
                             final DataRow parameter);
}
