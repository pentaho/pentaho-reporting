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

import java.sql.Connection;
import java.sql.SQLException;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * User: Martin Date: 21.07.2006 Time: 12:54:42
 */
public class JndiConnectionDefinition extends JdbcConnectionDefinition
{
  private String jndiName;
  private String username;
  private String password;
  private String databaseType;

  public JndiConnectionDefinition(final String name,
                                  final String jndiName,
                                  final String databaseType,
                                  final String username,
                                  final String password)
  {
    super(name);
    if (StringUtils.isEmpty(jndiName))
    {
      throw new IllegalArgumentException("The provided jndiName can not be empty");
    }

    this.jndiName = jndiName;
    this.databaseType = databaseType;
    this.username = username;
    this.password = password;
  }

  public String getJndiName()
  {
    return jndiName;
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
    if (!super.equals(o))
    {
      return false;
    }

    final JndiConnectionDefinition that = (JndiConnectionDefinition) o;

    if (databaseType != null ? !databaseType.equals(that.databaseType) : that.databaseType != null)
    {
      return false;
    }
    if (!jndiName.equals(that.jndiName))
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
    int result = super.hashCode();
    result = 31 * result + jndiName.hashCode();
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    result = 31 * result + (databaseType != null ? databaseType.hashCode() : 0);
    return result;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public String getDatabaseType()
  {
    return databaseType;
  }
}