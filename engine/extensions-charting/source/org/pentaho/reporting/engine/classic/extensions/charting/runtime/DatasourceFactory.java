package org.pentaho.reporting.engine.classic.extensions.charting.runtime;

/**
 * Exists for compatibility with CCC.
 *
 * @author pdpi
 */
public class DatasourceFactory
{
  public DatasourceFactory()
  {
  }

  public Datasource createDatasource(final String type)
  {
    return new DefaultDatasource();
  }
}
