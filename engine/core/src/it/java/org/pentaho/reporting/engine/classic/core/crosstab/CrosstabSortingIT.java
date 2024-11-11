/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
