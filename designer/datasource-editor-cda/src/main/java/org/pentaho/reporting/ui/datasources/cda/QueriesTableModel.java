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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.cda;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaQueryEntry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class QueriesTableModel extends AbstractTableModel implements Cloneable
{
  public static class QueryData
  {
    private CdaQueryEntry queryEntry;
    private String[] declaredParameter;

    public QueryData(final CdaQueryEntry queryEntry,
                     final String[] declaredParameter)
    {
      if (queryEntry == null)
      {
        throw new NullPointerException();
      }
      this.queryEntry = queryEntry;
      this.declaredParameter = declaredParameter;
    }

    public CdaQueryEntry getQueryEntry()
    {
      return queryEntry;
    }

    public String[] getDeclaredParameter()
    {
      return declaredParameter;
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

      final QueryData queryData = (QueryData) o;

      if (!queryEntry.equals(queryData.queryEntry))
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      return queryEntry != null ? queryEntry.hashCode() : 0;
    }
  }

  private ArrayList<QueryData> queries;

  public QueriesTableModel()
  {
    queries = new ArrayList<QueryData>();
  }

  public Object clone()
  {
    try
    {
      final QueriesTableModel queriestablemodel = (QueriesTableModel) super.clone();
      queriestablemodel.queries = (ArrayList<QueryData>) queries.clone();
      return queriestablemodel;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new IllegalStateException(cne);
    }
  }

  public void add(final QueryData query)
  {
    if (queries.contains(query))
    {
      throw new IllegalStateException();
    }
    queries.add(query);
    fireTableDataChanged();
  }

  public int size()
  {
    return queries.size();
  }

  public QueryData get(final int index)
  {
    return queries.get(index);
  }

  public QueryData getQueryById(final String name)
  {
    for (final QueryData query : queries)
    {
      if (ObjectUtilities.equal(name, query.getQueryEntry().getId()))
      {
        return query;
      }
    }
    return null;
  }
  
  public void clear()
  {
    queries.clear();
    fireTableDataChanged();
  }

  public String getName(final int row)
  {
    return (String) getValueAt(row, 0);
  }

  public String getQuery(final int row)
  {
    return (String) getValueAt(row, 1);
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex)
  {
    return String.class;
  }

  public String getColumnName(final int column)
  {
    if (column == 0)
    {
      return Messages.getString("QueriesTableModel.Query");
    }
    if (column == 1)
    {
      return Messages.getString("QueriesTableModel.CdaId");
    }
    throw new IndexOutOfBoundsException();
  }

  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    return (columnIndex == 0);
  }

  public int getColumnCount()
  {
    return 2;
  }

  public int getRowCount()
  {
    return queries.size();
  }

  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final QueryData queryData = queries.get(rowIndex);
    final CdaQueryEntry query = queryData.getQueryEntry();
    if (columnIndex == 0)
    {
      return query.getName();
    }
    if (columnIndex == 1)
    {
      return query.getId();
    }
    throw new IndexOutOfBoundsException();
  }

  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    final QueryData queryData = queries.get(rowIndex);
    final CdaQueryEntry query = queryData.getQueryEntry();
    if (columnIndex == 0)
    {
      query.setName((String) aValue);
      fireTableCellUpdated(rowIndex, columnIndex);
    }
  }

}
