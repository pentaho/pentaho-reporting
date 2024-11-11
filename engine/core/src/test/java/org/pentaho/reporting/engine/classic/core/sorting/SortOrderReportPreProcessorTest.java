/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
