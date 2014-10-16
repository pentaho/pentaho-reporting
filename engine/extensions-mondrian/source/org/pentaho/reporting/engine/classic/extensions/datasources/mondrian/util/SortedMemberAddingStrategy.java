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
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import mondrian.olap.Dimension;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.Position;

public class SortedMemberAddingStrategy implements MemberAddingStrategy
{
  private static class MemberComparator implements Comparator<Member>
  {
    private Map<Dimension, Integer> dimensionOrder;

    public MemberComparator(List<Position> positions)
    {
      LinkedHashSet<Dimension> dimensionInOrder = new LinkedHashSet<Dimension>();
      for (Position position : positions)
      {
        for (Member member : position)
        {
          dimensionInOrder.add(member.getDimension());
        }
      }

      dimensionOrder = new HashMap<Dimension, Integer>();
      for (Dimension dimension : dimensionInOrder)
      {
        dimensionOrder.put(dimension, dimensionOrder.size());
      }
    }

    private int getDimensionPositionOnAxis(Dimension d)
    {
      Integer integer = dimensionOrder.get(d);
      if (integer != null)
      {
        return integer.intValue();
      }
      return -1;
    }

    public int compare(final Member o1, final Member o2)
    {
      Dimension d1 = o1.getLevel().getDimension();
      Dimension d2 = o2.getLevel().getDimension();
      int dimOrder = Integer.compare(getDimensionPositionOnAxis(d1), getDimensionPositionOnAxis(d2));
      if (dimOrder != 0)
      {
        return dimOrder;
      }

      Level level1 = o1.getLevel();
      Level level2 = o2.getLevel();
      int levelOrder = Integer.compare(level1.getDepth(), level2.getDepth());
      if (levelOrder != 0)
      {
        return levelOrder;
      }
      return o1.getLevel().getUniqueName().compareTo(o2.getLevel().getUniqueName());
    }
  }

  private TreeSet<Member> set;

  public SortedMemberAddingStrategy(final List<Position> positions)
  {
    this.set = new TreeSet<Member>(new MemberComparator(positions));
  }

  public void add(final Member m)
  {
    this.set.add(m);
  }

  public Collection<Member> values()
  {
    return set;
  }
}
