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

package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellType;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class ColumnSummaryIT extends TestCase {
  public ColumnSummaryIT() {
  }

  public ColumnSummaryIT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPageBreakOnCT2() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final URL url = getClass().getResource( "empty-ct.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final ReportElement crosstabCell = report.getChildElementByType( CrosstabCellType.INSTANCE );
    // crosstabCell.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.CROSSTAB_DETAIL_MODE,
    // CrosstabDetailMode.first);

    // ModelPrinter.print(DebugReportRunner.layoutPage(report, 0));
    for ( int i = 0; i < 10; i += 1 ) {
      DebugReportRunner.createPDF( report );
    }
  }

  public void testPageBreakOnCT() throws Exception {
    final URL url = getClass().getResource( "agg-error.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final ReportElement crosstabCell = report.getChildElementByType( CrosstabCellType.INSTANCE );
    // report.setAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT, 100);
    // crosstabCell.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.CROSSTAB_DETAIL_MODE,
    // CrosstabDetailMode.first);

    // ModelPrinter.print(DebugReportRunner.layoutPage(report, 0));
    DebugReportRunner.showDialog( report );
  }
}
