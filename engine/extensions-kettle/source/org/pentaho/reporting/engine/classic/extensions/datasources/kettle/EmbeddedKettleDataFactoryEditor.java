package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;

public interface EmbeddedKettleDataFactoryEditor extends DataSourcePlugin
{
  public void configure(String metaDataId);
}
