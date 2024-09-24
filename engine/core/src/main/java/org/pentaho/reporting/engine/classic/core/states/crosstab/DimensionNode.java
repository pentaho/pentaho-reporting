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

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DimensionNode implements Comparable<DimensionNode> {
  private Object[] data;
  private HashSet<DimensionNode> previousNodes;
  private HashSet<DimensionNode> nextNodes;
  private int distanceFromRoot;
  private int rowCount;
  private boolean valid;

  public DimensionNode( final Object[] data, final int rowCount ) {
    this.rowCount = rowCount;
    this.data = data.clone();
    this.previousNodes = new HashSet<DimensionNode>();
    this.nextNodes = new HashSet<DimensionNode>();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final DimensionNode that = (DimensionNode) o;

    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    if ( !Arrays.equals( data, that.data ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return Arrays.hashCode( data );
  }

  public boolean addParent( final DimensionNode node ) {
    node.checkLoop( this );
    if ( this.previousNodes.add( node ) ) {
      node.nextNodes.add( this );
      invalidate();
      return true;
    }
    return false;
  }

  private void removeParent( final DimensionNode previousNode ) {
    this.previousNodes.remove( previousNode );
    previousNode.nextNodes.remove( this );
    // no need to invalidate that node. Removing a child does not change the
    // distance of the parent node to the root of the tree.
  }

  private void checkLoop( final DimensionNode node ) {
    if ( node.equals( this ) ) {
      throw new InvalidReportStateException( "Looping nodes. This data-model cannot be normalized." );
    }
    for ( final DimensionNode previousNode : previousNodes ) {
      previousNode.checkLoop( node );
    }
  }

  private void invalidate() {
    if ( valid == false ) {
      return;
    }

    valid = false;
    for ( final DimensionNode dimensionNode : nextNodes ) {
      dimensionNode.invalidate();
    }
  }

  public void rebalance() {
    if ( valid ) {
      return;
    }

    if ( previousNodes.isEmpty() ) {
      distanceFromRoot = 0;
      valid = true;
      return;
    }

    int maxDepth = 0;
    for ( final DimensionNode previousNode : previousNodes ) {
      final int depth = previousNode.getDistanceFromRoot();
      if ( depth > maxDepth ) {
        maxDepth = depth;
      }
    }

    final ArrayList<DimensionNode> nodesForRemoval = new ArrayList<DimensionNode>();
    for ( final DimensionNode previousNode : previousNodes ) {
      final int depth = previousNode.getDistanceFromRoot();
      if ( depth < maxDepth ) {
        nodesForRemoval.add( previousNode );
      }
    }

    for ( int i = 0; i < nodesForRemoval.size(); i++ ) {
      removeParent( nodesForRemoval.get( i ) );
    }

    distanceFromRoot = maxDepth + 1;
    valid = true;
  }

  private int getDistanceFromRoot() {
    if ( !valid ) {
      rebalance();
    }

    return distanceFromRoot;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "DimensionNode" );
    sb.append( "{data=" ).append( Arrays.asList( data ).toString() );
    sb.append( ", nextNodes=" ).append( nextNodes );
    sb.append( ", rowCount=" ).append( rowCount );
    sb.append( '}' );
    return sb.toString();
  }

  public String printFormatted() {
    return printFormatted( 0 );
  }

  private String printFormatted( final int indent ) {
    final StringBuilder b = new StringBuilder();
    for ( int i = 0; i < indent; i += 1 ) {
      b.append( "    " );
    }
    b.append( "[" );
    b.append( Arrays.asList( data ).toString() );
    b.append( "] distance=" );
    b.append( getDistanceFromRoot() );
    b.append( " sort-order=" );
    b.append( rowCount );

    for ( final DimensionNode nextNode : nextNodes ) {
      b.append( "\n" );
      b.append( nextNode.printFormatted( indent + 1 ) );
    }
    return b.toString();
  }

  public Object[] getData() {
    return data;
  }

  public int compareTo( final DimensionNode o ) {
    final int distance = getDistanceFromRoot();
    final int otherDistance = o.getDistanceFromRoot();
    if ( distance < otherDistance ) {
      return -1;
    }
    if ( distance > otherDistance ) {
      return +1;
    }

    final int dataComparison = CrosstabKeyComparator.INSTANCE.compare( data, o.data );
    if ( dataComparison != 0 ) {
      return dataComparison;
    }

    // fallback to the row-count.
    if ( rowCount < o.rowCount ) {
      return -1;
    }
    if ( o.rowCount < rowCount ) {
      return +1;
    }
    return 0;
  }
}
