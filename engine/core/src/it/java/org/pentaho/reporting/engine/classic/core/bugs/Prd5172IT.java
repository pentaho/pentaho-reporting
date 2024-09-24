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

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd5172IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testBandedSubReportIsInline() throws Exception {
    SubReport subReport = new SubReport();
    subReport.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );

    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( subReport );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }

  @Test
  public void testInlineSubReportIsInline() throws Exception {
    SubReport subReport = new SubReport();
    subReport.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );

    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( subReport );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }

  @Test
  public void testMasterReportInline() throws Exception {
    MasterReport report = new MasterReport();
    report.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }
}
