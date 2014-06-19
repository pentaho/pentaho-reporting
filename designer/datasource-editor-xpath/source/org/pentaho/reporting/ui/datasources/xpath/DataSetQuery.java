/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.xpath;

/**
 * A common data-object for representing named queries.
 */
public class DataSetQuery implements Cloneable
{
  private String queryName;
  private String query;
  private boolean legacyQuery;

  public DataSetQuery(final String queryName, final String query, final boolean legacyQuery)
  {
    this.legacyQuery = legacyQuery;
    //noinspection ConstantConditions
    if (queryName == null)
    {
      throw new IllegalArgumentException("queryName must not be null");
    }
    //noinspection ConstantConditions
    if (query == null)
    {
      throw new IllegalArgumentException("query must not be null");
    }

    this.queryName = queryName;
    this.query = query;
  }

  public boolean isLegacyQuery()
  {
    return legacyQuery;
  }

  public void setLegacyQuery(final boolean legacyQuery)
  {
    this.legacyQuery = legacyQuery;
  }

  public String getQueryName()
  {
    return queryName;
  }

  public void setQueryName(final String queryName)
  {
    //noinspection ConstantConditions
    if (queryName == null)
    {
      throw new IllegalArgumentException("queryName must not be null");
    }

    this.queryName = queryName;
  }

  public String getQuery()
  {
    return query;
  }

  public void setQuery(final String query)
  {
    this.query = query;
  }

  public String toString()
  {
    return queryName.length() > 0 ? queryName : " ";
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

    final DataSetQuery that = (DataSetQuery) o;

    if (queryName != null ? !queryName.equals(that.queryName) : that.queryName != null)
    {
      return false;
    }
    if (legacyQuery != that.legacyQuery)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int hashCode = (queryName != null ? queryName.hashCode() : 0);
    hashCode *= 23 + (legacyQuery? 0 : 1);
    return hashCode;
  }

  protected Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}
