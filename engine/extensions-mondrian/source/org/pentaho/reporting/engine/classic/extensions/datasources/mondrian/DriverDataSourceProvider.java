package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * Todo: Document me!
 * <p/>
 * Date: 24.08.2009
 * Time: 19:01:45
 *
 * @author Thomas Morgner.
 */
public class DriverDataSourceProvider implements DataSourceProvider
{
  private Properties properties;
  private String url;
  private String driver;

  public DriverDataSourceProvider()
  {
    this.properties = new Properties();
  }

  public String getProperty(final String key)
  {
    return properties.getProperty(key);
  }

  public Object setProperty(final String key, final String value)
  {
    if (value == null)
    {
      return properties.remove(key);
    }
    else
    {
      return properties.setProperty(key, value);
    }
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(final String url)
  {
    this.url = url;
  }

  public String getDriver()
  {
    return driver;
  }

  public void setDriver(final String driver)
  {
    this.driver = driver;
  }

  public DataSource getDataSource() throws SQLException
  {
    try
    {
      if (driver != null)
      {
        Class.forName(driver);
      }
    }
    catch (Exception e)
    {
      throw new SQLException("Unable to load the driver: " + driver, e.getMessage()); //$NON-NLS-1$
    }

    return DriverDataSourceCache.createDataSource(url, properties);
  }

  public String[] getPropertyNames()
  {
    return (String[]) properties.keySet().toArray(new String[properties.size()]);
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final DriverDataSourceProvider that = (DriverDataSourceProvider) o;

    if (driver != null ? !driver.equals(that.driver) : that.driver != null)
    {
      return false;
    }
    if (properties.equals(that.properties))
    {
      return false;
    }
    if (url != null ? !url.equals(that.url) : that.url != null)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (properties != null ? properties.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (driver != null ? driver.hashCode() : 0);
    return result;
  }

  public Object getConnectionHash()
  {
    final ArrayList<Object> hash = new ArrayList<Object>();
    hash.add(getClass().getName());
    hash.add(properties);
    hash.add(url);
    hash.add(driver);
    return hash;
  }
}
