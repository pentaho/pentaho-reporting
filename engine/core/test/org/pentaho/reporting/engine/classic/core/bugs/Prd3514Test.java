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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.states.CascadingDataFactory;

public class Prd3514Test extends TestCase
{
  public Prd3514Test()
  {
  }

  public Prd3514Test(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCascadingAndCaching() throws ReportDataFactoryException
  {
    final CompoundDataFactory cdf = new CompoundDataFactory();
    cdf.add(new TableDataFactory("static", new DefaultTableModel()));

    final CachingDataFactory caDf = new CachingDataFactory(cdf, true);

    final CompoundDataFactory ccdf = new CascadingDataFactory();
    ccdf.add(caDf);
    ccdf.add(new StaticDataFactory());
    
    assertTrue(ccdf.isQueryExecutable("static", new StaticDataRow()));
    assertNotNull(ccdf.queryData("static", new StaticDataRow()));
  }
}
