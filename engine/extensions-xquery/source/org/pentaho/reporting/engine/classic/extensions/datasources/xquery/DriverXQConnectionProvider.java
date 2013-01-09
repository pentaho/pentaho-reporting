/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.xquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import java.util.Properties;

/**
 * @author Thomas Morgner
 * @author Cedric Pronzato
 */
public class DriverXQConnectionProvider implements XQConnectionProvider
{
  private static final Log logger = LogFactory.getLog(DriverXQConnectionProvider.class);

  private Properties properties;
  private String url;
  private String driver;

  public DriverXQConnectionProvider()
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

  public XQConnection getConnection() throws XQException
  {
    if (driver != null)
    {
      final XQDataSource datasource = (XQDataSource)
          ObjectUtilities.loadAndInstantiate(driver, DriverXQConnectionProvider.class, XQDataSource.class);
      if (datasource == null)
      {
        throw new IllegalArgumentException("Unable to load XQJ datasource driver: " + driver);
      }
      else
      {
        datasource.setProperties(properties);
        return datasource.getConnection();
      }

    }
    else
    {
      throw new IllegalArgumentException("The xqdatasource must not be null");
    }

  }

  public String[] getPropertyNames()
  {
    return (String[]) properties.keySet().toArray(new String[properties.size()]);
  }

  public Properties getProperties()
  {
    return properties;
  }

  public void setProperties(Properties properties)
  {
    this.properties = properties;
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

    final DriverXQConnectionProvider that = (DriverXQConnectionProvider) o;
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
}