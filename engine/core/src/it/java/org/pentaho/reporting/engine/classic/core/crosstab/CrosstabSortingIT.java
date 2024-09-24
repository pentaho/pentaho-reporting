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

package org.pentaho.reporting.engine.classic.core.crosstab;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.engine.classic.core.sorting.SortOrderReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CrosstabSortingIT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSorting() throws ResourceException {
    URL url = getClass().getResource( "crosstab-sorting.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    report.setAutoSort( true );
    SortOrderReportPreProcessor p = new SortOrderReportPreProcessor();
    List<SortConstraint> sortConstraints = p.computeSortConstraints( report.getReportFooter().getSubReport( 0 ) );
    List<SortConstraint> expectedSortConstraints = new ArrayList<SortConstraint>();
    expectedSortConstraints.add( new SortConstraint( "year", true ) );
    expectedSortConstraints.add( new SortConstraint( "month", true ) );
    Assert.assertEquals( expectedSortConstraints, sortConstraints );
    DebugReportRunner.execGraphics2D( report );
  }
}
