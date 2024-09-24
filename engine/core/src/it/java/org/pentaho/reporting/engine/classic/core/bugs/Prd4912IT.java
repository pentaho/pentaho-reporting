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

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.testsupport.ReportWritingUtil;

public class Prd4912IT {
  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test( expected = BundleWriterException.class )
  public void testReportAPI() throws Exception {
    MasterReport report = new MasterReport();
    // by all means, this is illegal, root-level band should not appear directly inside other root-level bands
    report.getReportHeader().addElement( new PageHeader() );

    MasterReport elements = ReportWritingUtil.saveAndLoad( report );
    Assert.assertTrue( elements.getReportHeader().getElement( 0 ) instanceof Band );
    Assert.assertFalse( elements.getReportHeader().getElement( 0 ) instanceof RootLevelBand );
  }

  @Test( expected = BundleWriterException.class )
  public void testReportAPI2() throws Exception {
    MasterReport report = new MasterReport();
    // by all means, this is illegal, root-level band should not appear directly inside other root-level bands
    report.getReportHeader().addElement( new MasterReport() );

    MasterReport elements = ReportWritingUtil.saveAndLoad( report );
    Assert.assertTrue( elements.getReportHeader().getElement( 0 ) instanceof Band );
    Assert.assertFalse( elements.getReportHeader().getElement( 0 ) instanceof RootLevelBand );
  }
}
