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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.util;

import org.olap4j.Position;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class SortedMemberAddingStrategy implements MemberAddingStrategy {
  private static class MemberComparator implements Comparator<Member> {
    private Map<Dimension, Integer> dimensionOrder;

    public MemberComparator( final List<Position> positions ) {
      LinkedHashSet<Dimension> dimensionInOrder = new LinkedHashSet<Dimension>();
      for ( final Position position : positions ) {
        for ( final Member member : position.getMembers() ) {
          dimensionInOrder.add( member.getDimension() );
        }
      }

      dimensionOrder = new HashMap<Dimension, Integer>();
      for ( final Dimension dimension : dimensionInOrder ) {
        dimensionOrder.put( dimension, dimensionOrder.size() );
      }
    }

    private int getDimensionPositionOnAxis( final Dimension d ) {
      Integer integer = dimensionOrder.get( d );
      if ( integer != null ) {
        return integer.intValue();
      }
      return -1;
    }

    public int compare( final Member o1, final Member o2 ) {
      Dimension d1 = o1.getLevel().getDimension();
      Dimension d2 = o2.getLevel().getDimension();
      int dimOrder = Integer.compare( getDimensionPositionOnAxis( d1 ), getDimensionPositionOnAxis( d2 ) );
      if ( dimOrder != 0 ) {
        return dimOrder;
      }

      Hierarchy h1 = o1.getHierarchy();
      Hierarchy h2 = o2.getHierarchy();
      int hierarchyOrder = h1.getName().compareTo( h2.getName() );
      if ( hierarchyOrder != 0 ) {
        return hierarchyOrder;
      }

      Level level1 = o1.getLevel();
      Level level2 = o2.getLevel();
      int levelOrder = Integer.compare( level1.getDepth(), level2.getDepth() );
      if ( levelOrder != 0 ) {
        return levelOrder;
      }
      return o1.getLevel().getUniqueName().compareTo( o2.getLevel().getUniqueName() );
    }
  }

  private TreeSet<Member> set;

  public SortedMemberAddingStrategy( final List<Position> positions ) {
    this.set = new TreeSet<Member>( new MemberComparator( positions ) );
  }

  public void add( final Member m ) {
    this.set.add( m );
  }

  public Collection<Member> values() {
    return set;
  }
}
