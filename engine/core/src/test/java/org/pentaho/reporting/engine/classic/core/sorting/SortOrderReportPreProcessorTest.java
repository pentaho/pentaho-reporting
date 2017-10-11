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

package org.pentaho.reporting.engine.classic.core.sorting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.RelationalReportBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.base.PreProcessorTestBase;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;

import java.util.List;

public class SortOrderReportPreProcessorTest extends PreProcessorTestBase {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  protected ReportPreProcessor create() {
    return new SortOrderReportPreProcessor();
  }

  @Test
  public void testSortOrderCalculationRelational() throws ReportProcessingException {
    DesignTimeDataSchemaModel model = new DesignTimeDataSchemaModel( new MasterReport() );
    RelationalReportBuilder builder = new RelationalReportBuilder( model );
    builder.addGroup( "Group-A" );
    builder.addGroup( "Group-B" );
    builder.addGroup( "Group-C" );

    MasterReport report = builder.createReport();
    report.setAutoSort( Boolean.TRUE );
    ReportPreProcessor reportPreProcessor = create();
    MasterReport materialized = materializePreData( report, reportPreProcessor );
    Object attribute =
        materialized
            .getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMPUTED_SORT_CONSTRAINTS );
    Assert.assertTrue( attribute instanceof List );
    List<SortConstraint> sc = (List<SortConstraint>) attribute;
    Assert.assertEquals( 3, sc.size() );
    Assert.assertEquals( new SortConstraint( "Group-A", true ), sc.get( 0 ) );
    Assert.assertEquals( new SortConstraint( "Group-B", true ), sc.get( 1 ) );
    Assert.assertEquals( new SortConstraint( "Group-C", true ), sc.get( 2 ) );
  }

  @Test
  public void testSortOrderCalculationCrosstab() throws ReportProcessingException {
    ContextAwareDataSchemaModel model = new DesignTimeDataSchemaModel( new MasterReport() );
    CrosstabBuilder builder = new CrosstabBuilder( model );
    builder.addDetails( "Details", null );
    builder.addRowDimension( "Row-A" );
    builder.addRowDimension( "Row-B" );
    builder.addOtherDimension( "Other-A" );
    builder.addOtherDimension( "Other-B" );
    builder.addColumnDimension( "Col-A" );
    builder.addColumnDimension( "Col-B" );

    MasterReport report = builder.createReport();
    report.setAutoSort( Boolean.TRUE );
    ReportPreProcessor reportPreProcessor = create();
    MasterReport materialized = materializePreData( report, reportPreProcessor );
    Object attribute =
        materialized
            .getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMPUTED_SORT_CONSTRAINTS );
    Assert.assertTrue( attribute instanceof List );
    List<SortConstraint> sc = (List<SortConstraint>) attribute;
    Assert.assertEquals( 6, sc.size() );
    Assert.assertEquals( new SortConstraint( "Other-A", true ), sc.get( 0 ) );
    Assert.assertEquals( new SortConstraint( "Other-B", true ), sc.get( 1 ) );
    Assert.assertEquals( new SortConstraint( "Row-A", true ), sc.get( 2 ) );
    Assert.assertEquals( new SortConstraint( "Row-B", true ), sc.get( 3 ) );
    Assert.assertEquals( new SortConstraint( "Col-A", true ), sc.get( 4 ) );
    Assert.assertEquals( new SortConstraint( "Col-B", true ), sc.get( 5 ) );
  }
}
