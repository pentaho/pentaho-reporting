package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import javax.sql.DataSource;

public interface DataSourceService {
  /**
   * This method clears the JNDI DS cache. The need exists because after a JNDI connection edit the old DS must be
   * removed from the cache.
   */
  public void clearCache();

  /**
   * This method clears the JNDI DS cache. The need exists because after a JNDI connection edit the old DS must be
   * removed from the cache.
   */
  public void clearDataSource( String dsName );

  /**
   * Since JNDI is supported different ways in different app servers, it's nearly impossible to have a ubiquitous way to
   * look up a datasource. This method is intended to hide all the lookups that may be required to find a jndi name.
   *
   * @param dsName
   *          The Datasource name
   * @return DataSource if there is one bound in JNDI
   */
  public DataSource getDataSource( String dsName ) throws DatasourceServiceException;

  /**
   * Since JNDI is supported different ways in different app servers, it's nearly impossible to have a ubiquitous way to
   * look up a datasource. This method is intended to hide all the lookups that may be required to find a jndi name, and
   * return the actual bound name.
   *
   * @param dsName
   *          The Datasource name (like SampleData)
   * @return The bound DS name if it is bound in JNDI (like "jdbc/SampleData")
   */
  public String getDSBoundName( String dsName ) throws DatasourceServiceException;

}
