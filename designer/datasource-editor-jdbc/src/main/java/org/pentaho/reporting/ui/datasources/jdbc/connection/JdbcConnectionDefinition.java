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
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.connection;

/**
 * User: Martin Date: 21.07.2006 Time: 12:54:42
 */
public abstract class JdbcConnectionDefinition
{
  private String name;

  protected JdbcConnectionDefinition(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof JdbcConnectionDefinition))
    {
      return false;
    }

    final JdbcConnectionDefinition that = (JdbcConnectionDefinition) o;

    if (!name.equals(that.name))
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    return name.hashCode();
  }
}
