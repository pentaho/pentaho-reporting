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

package org.pentaho.reporting.engine.classic.core.crosstab;

import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class BrokeCellsTest extends TestCase
{
  public BrokeCellsTest()
  {
  }

  public BrokeCellsTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testTest() throws Exception
  {
    final URL url = getClass().getResource("cells-broken.prpt");
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res = manager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) res.getResource();

    final ReportElement[] cells = report.getChildElementsByType(CrosstabCellType.INSTANCE);
    for (int i = 0; i < cells.length; i++)
    {
      final CrosstabCell cell = (CrosstabCell) cells[i];
//      cell.getElement(0).getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, -100f);
    }

    final LogicalPageBox box = DebugReportRunner.layoutPage(report, 0);
    ModelPrinter.INSTANCE.print(box);

//    DebugReportRunner.showDialog(report);
  }
}
