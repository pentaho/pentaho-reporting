/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.sorting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

import javax.swing.table.TableModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class TableSorter {
  private static final Log logger = LogFactory.getLog( TableSorter.class );

  private TableModel model;
  private SortConstraint[] constraints;
  private Tuple[] sortData;
  private HashMap<String, Integer> columnNames;

  public TableSorter() {
  }

  public TableSorter init( final TableModel model, final SortConstraint[] constraints ) {
    this.model = model;
    this.constraints = constraints;
    return this;
  }

  private HashMap<String, Integer> createColumnNameIndex() {
    HashMap<String, Integer> idx = new HashMap<String, Integer>();
    TableModel tableModel = getModel();
    int cc = tableModel.getColumnCount();
    for ( int i = 0; i < cc; i += 1 ) {
      idx.put( tableModel.getColumnName( i ), i );
    }
    return idx;
  }

  public TableSorter populate() {
    this.columnNames = createColumnNameIndex();
    this.sortData = calculateSortColumnIndex();
    return this;
  }

  protected Tuple[] calculateSortColumnIndex() {
    ArrayList<Tuple> index = new ArrayList<Tuple>();
    for ( final SortConstraint constraint : constraints ) {
      int idx = findIndex( constraint.getField() );
      if ( idx >= 0 ) {
        index.add( new Tuple( idx, constraint ) );
      } else {
        logger.debug( "Sort constraint contained references invalid column '" + constraint.getField() + "'" );
      }
    }
    return index.toArray( new Tuple[index.size()] );
  }

  private int findIndex( final String columnName ) {
    Integer integer = columnNames.get( columnName );
    if ( integer == null ) {
      return -1;
    }
    return integer.intValue();
  }

  protected TableModel getModel() {
    return model;
  }

  protected SortConstraint[] getConstraints() {
    return constraints;
  }

  public Tuple[] getSortData() {
    return sortData;
  }

  public int[] sortData() {
    if ( sortData.length > 0 ) {
      IndexElement[] sortableArray = createSortableData();
      Arrays.sort( sortableArray );
      return buildRawIndex( sortableArray );
    } else {
      return buildIdentityMapping();
    }
  }

  private int[] buildIdentityMapping() {
    int[] raw = new int[model.getRowCount()];
    for ( int i = 0; i < raw.length; i++ ) {
      raw[i] = i;
    }
    return raw;
  }

  private int[] buildRawIndex( final IndexElement[] sortableArray ) {
    int[] raw = new int[sortableArray.length];
    for ( int i = 0; i < sortableArray.length; i++ ) {
      IndexElement indexElement = sortableArray[i];
      raw[i] = indexElement.getSourceRow();
    }
    return raw;
  }

  private IndexElement[] createSortableData() {
    IndexElement[] indexElements = new IndexElement[model.getRowCount()];
    for ( int i = 0; i < indexElements.length; i++ ) {
      indexElements[i] = createIndexElement( i );
    }
    return indexElements;
  }

  protected IndexElement createIndexElement( final int row ) {
    return new IndexElement( row );
  }

  protected Comparator<Object> getComparator() {
    return GenericComparator.INSTANCE;
  }

  /**
   * Used to cheat Java's sort method to sort an int-array with a custom handler.
   */
  protected class IndexElement implements Comparable<IndexElement> {
    private int sourceRow;
    private WeakReference<Object[]> rowData;

    public IndexElement( final int sourceRow ) {
      this.sourceRow = sourceRow;
    }

    public int getSourceRow() {
      return sourceRow;
    }

    protected Object[] getRowData() {
      if ( rowData != null ) {
        Object[] objects = rowData.get();
        if ( objects != null ) {
          return objects;
        }
      }

      Tuple[] sortData = getSortData();
      Object[] row = new Object[sortData.length];
      for ( int i = 0; i < row.length; i++ ) {
        row[i] = getModel().getValueAt( getSourceRow(), sortData[i].columnIndex );
      }

      rowData = new WeakReference<Object[]>( row );
      return row;
    }

    public int compareTo( final IndexElement o ) {
      Object[] data = getRowData();
      Object[] otherData = o.getRowData();
      Tuple[] sortData = getSortData();
      Comparator<Object> comparator = getComparator();
      for ( int i = 0; i < sortData.length; i++ ) {
        Tuple tuple = sortData[i];
        Object rawMine = data[i];
        Object rawTheirs = otherData[i];
        int result = comparator.compare( rawMine, rawTheirs ) * tuple.sortOrder;
        if ( result != 0 ) {
          return result;
        }
      }
      return Integer.compare( sourceRow, o.sourceRow );
    }
  }

  protected static class Tuple {
    public final int columnIndex;
    public final String name;
    public final int sortOrder;

    public Tuple( final int columnIndex, final SortConstraint c ) {
      this.columnIndex = columnIndex;
      this.name = c.getField();
      this.sortOrder = c.isAscending() ? 1 : -1;
    }
  }

  public static int[] sort( final TableModel model, final SortConstraint[] constraints ) {
    TableSorter tableSorter = ClassicEngineBoot.getInstance().getObjectFactory().get( TableSorter.class );
    return tableSorter.init( model, constraints ).populate().sortData();
  }
}
