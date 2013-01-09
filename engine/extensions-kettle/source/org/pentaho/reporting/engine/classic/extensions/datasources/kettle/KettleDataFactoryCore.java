package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

public class KettleDataFactoryCore extends DefaultDataFactoryCore
{
  public KettleDataFactoryCore()
  {
  }

  public String[] getReferencedFields(final DataFactoryMetaData metaData,
                                      final DataFactory element,
                                      final String query,
                                      final DataRow parameter)
  {
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) element;
    final KettleTransformationProducer transformationProducer = kettleDataFactory.getQuery(query);
    if (transformationProducer == null)
    {
      return null;
    }
    
    return transformationProducer.getReferencedFields();
  }

  public Object getQueryHash(final DataFactoryMetaData dataFactoryMetaData,
                             final DataFactory dataFactory,
                             final String queryName,
                             final DataRow parameter)
  {
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) dataFactory;
    return kettleDataFactory.getQueryHash(queryName);
  }
}
