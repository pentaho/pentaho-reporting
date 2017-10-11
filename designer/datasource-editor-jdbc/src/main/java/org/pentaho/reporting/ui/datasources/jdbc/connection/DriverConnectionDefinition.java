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
 * Copyright (c) 2008 - 2017 Hitachi Vantara, .  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.connection;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DriverConnectionDefinition extends JdbcConnectionDefinition
{
  private static final Log log = LogFactory.getLog(DriverConnectionDefinition.class);

  private String driverClass;
  private String connectionString;
  private Properties properties;

  public DriverConnectionDefinition(final String name,
                                    final String driverClass,
                                    final String connectionString,
                                    final String username,
                                    final String password)
  {
    super(name);
    if (driverClass == null)
    {
      throw new NullPointerException();
    }
    if (connectionString == null)
    {
      throw new NullPointerException();
    }
    this.driverClass = driverClass;
    this.connectionString = connectionString;
    this.properties = new Properties();
    if (username != null)
    {
      this.properties.setProperty("user", username);
    }
    if (password != null)
    {
      this.properties.setProperty("password", password);
    }
    if (name != null)
    {
      this.properties.setProperty("::pentaho-reporting::name", name);
    }
  }

  public DriverConnectionDefinition(final String name,
                                    final String driverClass,
                                    final String connectionString,
                                    final String username,
                                    final String password,
                                    final String hostName,
                                    final String dbName,
                                    final String dbType,
                                    final String port,
                                    final Properties properties)
  {
    this(name, driverClass, connectionString, username, password);
    if (properties == null)
    {
      throw new NullPointerException();
    }
    this.properties.putAll(properties);
    this.setHostName(hostName);
    this.setDatabaseName(dbName);
    this.setDatabaseType(dbType);
    this.setPort(port);
  }

  public String getPort()
  {
    return properties.getProperty("::pentaho-reporting::port");
  }

  public void setPort(final String port)
  {
    if (port == null)
    {
      properties.remove("::pentaho-reporting::port");
    }
    else
    {
      properties.setProperty("::pentaho-reporting::port", port);
    }
  }

  public String getDriverClass()
  {
    return driverClass;
  }

  public String getConnectionString()
  {
    return connectionString;
  }

  public String getUsername()
  {
    return properties.getProperty("user");
  }

  public String getPassword()
  {
    return properties.getProperty("password");
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof DriverConnectionDefinition))
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    final DriverConnectionDefinition that = (DriverConnectionDefinition) o;

    if (!connectionString.equals(that.connectionString))
    {
      return false;
    }
    if (!driverClass.equals(that.driverClass))
    {
      return false;
    }
    if (!properties.equals(that.properties))
    {
      return false;
    }
    return true;
  }

  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + driverClass.hashCode();
    result = 31 * result + connectionString.hashCode();
    result = 31 * result + properties.hashCode();
    return result;
  }

  public String getDatabaseName()
  {
    return properties.getProperty("::pentaho-reporting::database-name");
  }

  public void setDatabaseName(final String databaseName)
  {
    if (databaseName == null)
    {
      properties.remove("::pentaho-reporting::database-name");
    }
    else
    {
      properties.setProperty("::pentaho-reporting::database-name", databaseName);
    }
  }

  public String getHostName()
  {
    return properties.getProperty("::pentaho-reporting::hostname");
  }

  public void setHostName(final String hostName)
  {
    if (hostName == null)
    {
      properties.remove("::pentaho-reporting::hostname");
    }
    else
    {
      properties.setProperty("::pentaho-reporting::hostname", hostName);
    }
  }

  public String getDatabaseType()
  {
    return properties.getProperty("::pentaho-reporting::database-type");
  }

  public void setDatabaseType(final String databaseType)
  {
    if (databaseType == null)
    {
      properties.remove("::pentaho-reporting::database-type");
    }
    else
    {
      properties.setProperty("::pentaho-reporting::database-type", databaseType);
    }
  }

  public Properties getProperties()
  {
    return properties;
  }
}
