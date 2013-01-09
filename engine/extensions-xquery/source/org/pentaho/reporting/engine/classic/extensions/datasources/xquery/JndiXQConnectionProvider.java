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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.xquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;

/**
 * This class retrieves a connection to an XQuery engine using JNDI naming system.
 *
 * @author Cedric Pronzato
 */
public class JndiXQConnectionProvider implements XQConnectionProvider
{
  private static InitialContext initialContext;

  protected static synchronized InitialContext getInitialContext() throws NamingException
  {
    if (initialContext == null)
    {
      initialContext = new InitialContext();
    }
    return initialContext;
  }

  private static final Log logger = LogFactory.getLog(JndiXQConnectionProvider.class);
  private String connectionPath;
  private String username;
  private String password;

  public JndiXQConnectionProvider()
  {
  }

  public String getConnectionPath()
  {
    return connectionPath;
  }

  public void setConnectionPath(final String connectionPath)
  {
    this.connectionPath = connectionPath;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(final String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(final String password)
  {
    this.password = password;
  }

  public XQConnection getConnection() throws XQException
  {
    if (connectionPath == null)
    {
      throw new IllegalArgumentException("The JNDI path cannot be null");
    }

    XQDataSource ds = null;
    try
    {
      final Context ctx = getInitialContext();
      final Object lookup = ctx.lookup(connectionPath);
      if (lookup instanceof XQDataSource)
      {
        ds = (XQDataSource) lookup;
      }
      else
      {
        logger.warn(connectionPath + " is not a valid XQDataSource JNDI access");
      }
    }
    catch (Exception e)
    {
      JndiXQConnectionProvider.logger.error("Failed to access the JDNI-System", e);
    }

    if (ds != null)
    {
      if (username == null)
      {
        return ds.getConnection();
      }
      return ds.getConnection(username, password);
    }
    return null;
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

    final JndiXQConnectionProvider that = (JndiXQConnectionProvider) o;

    if (connectionPath != null ? !connectionPath.equals(that.connectionPath) : that.connectionPath != null)
    {
      return false;
    }
    if (password != null ? !password.equals(that.password) : that.password != null)
    {
      return false;
    }
    if (username != null ? !username.equals(that.username) : that.username != null)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (connectionPath != null ? connectionPath.hashCode() : 0);
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    return result;
  }
}