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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Computed structural data of a crosstab. It basically contains the full dataset of the column axis, which then allows
 * us to inject artificial rows into the dataset.
 * <p/>
 * This mode uses the order in which elements occur in the datastream as normalized order of dimension elements, and if
 * there is ambiguity, it sorts elements by their natural order as well.
 * <p/>
 * We have the assumption, that the data is already pre-sorted in some way and that all rows are given in that order. We
 * assume that all items are comparable and that the items are sorted according to the natural order of the key. This
 * mode is intended to be used for raw-data crosstabs only. It consumes less memory than the ordered merge.
 *
 * @author Thomas Morgner
 */
public class SortedMergeCrosstabSpecification implements CrosstabSpecification {
  private ArrayList<Object[]> entries;
  private ArrayList<DimensionNode> currentRow;
  private DimensionNode rootNode;
  private HashMap<DimensionNode, DimensionNode> existingNodes;

  private String[] columnSet;
  private String[] rowSet;
  private ReportStateKey key;
  private int rowCount;

  public SortedMergeCrosstabSpecification( final ReportStateKey key, final String[] dimensionColumnSet,
      final String[] rowColumnSet ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( dimensionColumnSet == null ) {
      throw new NullPointerException();
    }

    this.key = key;
    // todo: Make sure we allow an empty column set. This may produce funny crosstabs, but it must not fail hard.
    this.columnSet = dimensionColumnSet.clone();
    this.rowSet = rowColumnSet.clone();
    this.entries = new ArrayList<Object[]>();

    this.currentRow = new ArrayList<DimensionNode>();
    this.existingNodes = new HashMap<DimensionNode, DimensionNode>();
    this.rootNode = new DimensionNode( new Object[0], -1 );
    this.rowCount = -1;
  }

  public int indexOf( final int start, final Object[] key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( start < 0 ) {
      throw new IndexOutOfBoundsException();
    }

    final int size = entries.size();
    for ( int i = start; i < size; i++ ) {
      final Object[] objects = entries.get( i );
      if ( ObjectUtilities.equalArray( key, objects ) ) {
        return i;
      }
    }
    return -1;
  }

  public String[] getColumnDimensionNames() {
    return columnSet.clone();
  }

  public String[] getRowDimensionNames() {
    return rowSet.clone();
  }

  public ReportStateKey getKey() {
    return key;
  }

  public void startRow() {
    currentRow.clear();
    rowCount += 1;
  }

  public void endRow() {
    boolean modified = false;
    if ( currentRow.size() > 0 ) {
      for ( int i = 1; i < currentRow.size(); i++ ) {
        final DimensionNode dimensionNode = currentRow.get( i );
        if ( dimensionNode.addParent( currentRow.get( i - 1 ) ) ) {
          modified = true;
        }
      }
      if ( currentRow.get( 0 ).addParent( rootNode ) ) {
        modified = true;
      }
    }

    if ( modified ) {
      for ( final DimensionNode node : existingNodes.keySet() ) {
        node.rebalance();
      }
    }
  }

  public void endCrosstab() {
    final DimensionNode[] dimensionNodes = existingNodes.keySet().toArray( new DimensionNode[existingNodes.size()] );
    Arrays.sort( dimensionNodes );

    this.entries.clear();
    for ( int i = 0; i < dimensionNodes.length; i++ ) {
      final DimensionNode node = dimensionNodes[i];
      this.entries.add( node.getData() );
    }
  }

  public void add( final DataRow dataRow ) {
    if ( columnSet.length == 0 ) {
      return;
    }

    final Object[] newKey = new Object[columnSet.length];
    for ( int i = 0; i < columnSet.length; i++ ) {
      final String columnName = columnSet[i];
      newKey[i] = dataRow.get( columnName );
    }

    if ( currentRow.isEmpty() == false ) {
      final DimensionNode node = currentRow.get( currentRow.size() - 1 );
      if ( Arrays.equals( node.getData(), newKey ) ) {
        return;
      }
    }

    final DimensionNode node = createUniqueNode( newKey );
    if ( currentRow.contains( node ) ) {
      throw new InvalidReportStateException( "Unsorted column dimension data within a single row-dimension instance." );
    }
    currentRow.add( node );
  }

  private DimensionNode createUniqueNode( final Object[] data ) {
    final DimensionNode dimensionNode = new DimensionNode( data, rowCount );
    final DimensionNode existing = existingNodes.get( dimensionNode );
    if ( existing != null ) {
      return existing;
    }

    existingNodes.put( dimensionNode, dimensionNode );
    return dimensionNode;
  }

  public int size() {
    return entries.size();
  }

  public Object[] getKeyAt( final int column ) {
    final Object[] data = entries.get( column );
    return data.clone();
  }
}
