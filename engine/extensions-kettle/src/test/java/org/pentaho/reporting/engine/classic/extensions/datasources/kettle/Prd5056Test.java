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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

public class Prd5056Test {
  
  private static final Log logger = LogFactory.getLog( Prd5056Test.class );
  
  private static final String QUERY =
    "test-src/org/pentaho/reporting/engine/classic/extensions/datasources/kettle/Prd-5056.ktr";
  
  private static final String STEP = "Abort";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test(expected = ReportDataFactoryException.class)
  public void testFailWithError() throws Exception {
      final KettleDataFactory kettleDataFactory = new KettleDataFactory();
      kettleDataFactory.setQuery( "test", new KettleTransFromFileProducer( QUERY, STEP ) );
      kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
      kettleDataFactory.queryData( "test", new ReportParameterValues() );
  }

  @Test
  public void testLoadSave() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    KettleTransFromFileProducer value = new KettleTransFromFileProducer( QUERY, STEP );
    Assert.assertTrue( value.isStopOnError() );

    kettleDataFactory.setQuery( "test", value );

    final KettleDataFactory e2 = (KettleDataFactory) DataSourceTestBase.loadAndSaveOnReport( kettleDataFactory );
    KettleTransFromFileProducer test = (KettleTransFromFileProducer) e2.getQuery( "test" );
    Assert.assertTrue( test.isStopOnError() );
  }

  @Test
  public void testLoadSaveFalse() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    KettleTransFromFileProducer value = new KettleTransFromFileProducer( QUERY, STEP );
    value.setStopOnError( false );
    kettleDataFactory.setQuery( "test", value );

    final KettleDataFactory e2 = (KettleDataFactory) DataSourceTestBase.loadAndSaveOnReport( kettleDataFactory );
    KettleTransFromFileProducer test = (KettleTransFromFileProducer) e2.getQuery( "test" );
    Assert.assertFalse( test.isStopOnError() );
  }
}
