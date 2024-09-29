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


package org.pentaho.reporting.engine.classic.testcases;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;
import org.pentaho.reporting.engine.classic.core.testsupport.base.MetaDataValidationTestBase;

import java.util.ArrayList;
import java.util.List;

public class ReportPreProcessorMetaDataValidationTest extends MetaDataValidationTestBase<ReportPreProcessorMetaData> {
  public ReportPreProcessorMetaDataValidationTest() {
  }

  @Test
  public void testMetaData() {
    ReportPreProcessorMetaData[] m = ReportPreProcessorRegistry.getInstance().getAllReportPreProcessorMetaDatas();
    List list = super.performTest( m );
    Assert.assertEquals( new ArrayList(), list );
  }

  protected void performTestOnElement( final ReportPreProcessorMetaData metaData ) {
    final String typeName = metaData.getName();
    logger.debug( "Processing " + typeName );

    try {
      final Object type = metaData.create();
    } catch ( Exception e ) {
      Assert.fail( "metadata creation failed" );

    }

    validate( metaData );

    for ( ReportPreProcessorPropertyMetaData propertyDescription : metaData.getPropertyDescriptions() ) {
      validate( propertyDescription );
    }
  }
}
