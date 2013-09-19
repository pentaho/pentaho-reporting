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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableCutList;

public class TableCutListTest extends TestCase
{
  public TableCutListTest()
  {
  }

  public TableCutListTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPlainAdd()
  {
    final TableCutList list = new TableCutList(10, true);
    list.put(5000, Boolean.TRUE);
    list.put(15000, Boolean.TRUE);
    list.put(-85000, Boolean.TRUE);
    list.put(-5000, Boolean.TRUE);
    list.put(-15000, Boolean.TRUE);
    list.put(-25000, Boolean.TRUE);
    list.put(-35000, Boolean.TRUE);
    list.put(-45000, Boolean.TRUE);
    list.put(-55000, Boolean.TRUE);
    list.put(-65000, Boolean.TRUE);
    list.put(-75000, Boolean.TRUE);
    list.put(25000, Boolean.TRUE);
    list.put(35000, Boolean.TRUE);

    list.get(-95000);
    list.get(4900);
    list.get(5000);
    list.get(0);
    list.get(25000);
  }
}
