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

package org.pentaho.reporting.engine.classic.core.sorting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;

public class QueryDataRowWrapperTest
{
  private List<SortConstraint> sortConstraintList;

  @Before
  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();

    sortConstraintList = Collections.unmodifiableList
        (Arrays.asList(new SortConstraint("A", false), new SortConstraint("B", true)));
  }

  @Test
  public void testExtraColumn() {
    QueryDataRowWrapper wrapper = new QueryDataRowWrapper(new StaticDataRow(), 10, 12, sortConstraintList);
    String[] expecteds = {DataFactory.QUERY_LIMIT, DataFactory.QUERY_TIMEOUT, DataFactory.QUERY_SORT};
    Assert.assertArrayEquals(expecteds, wrapper.getColumnNames());
    Assert.assertEquals(wrapper.get(DataFactory.QUERY_LIMIT), Integer.valueOf(12));
    Assert.assertEquals(wrapper.get(DataFactory.QUERY_TIMEOUT), Integer.valueOf(10));
    Assert.assertEquals(wrapper.get(DataFactory.QUERY_SORT), sortConstraintList);
  }
}
