/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
