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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.ReportWritingUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Prd5062Test {
  private static final String QUERY =
    "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/kettle/row-gen.ktr";;
  private static final String STEP = "Formula";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testQueryNameEmpty() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.setQuery( "", new KettleTransFromFileProducer( QUERY, STEP ) );

    MasterReport report = new MasterReport();
    report.setDataFactory( kettleDataFactory );

    MasterReport r2 = ReportWritingUtil.saveAndLoad( report );
    assertTrue( r2.getDataFactory() instanceof KettleDataFactory );
    KettleDataFactory kdf = (KettleDataFactory) r2.getDataFactory();
    KettleTransformationProducer query = kdf.getQuery( "" );
    assertNotNull( query );
    assertNotNull( query.getTransformationFile() );
    assertNotNull( query.getStepName() );
  }

  @Test
  public void testStepNameNull() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.setQuery( "default", new KettleTransFromFileProducer( QUERY, null ) );

    MasterReport report = new MasterReport();
    report.setDataFactory( kettleDataFactory );

    MasterReport r2 = ReportWritingUtil.saveAndLoad( report );
    assertTrue( r2.getDataFactory() instanceof KettleDataFactory );
    KettleDataFactory kdf = (KettleDataFactory) r2.getDataFactory();
    KettleTransformationProducer query = kdf.getQuery( "default" );
    assertNotNull( query );
    assertNotNull( query.getTransformationFile() );
    assertNull( query.getStepName() );
  }

  @Test
  public void testStepNameEmpty() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.setQuery( "default", new KettleTransFromFileProducer( QUERY, "" ) );

    MasterReport report = new MasterReport();
    report.setDataFactory( kettleDataFactory );

    MasterReport r2 = ReportWritingUtil.saveAndLoad( report );
    assertTrue( r2.getDataFactory() instanceof KettleDataFactory );
    KettleDataFactory kdf = (KettleDataFactory) r2.getDataFactory();
    KettleTransformationProducer query = kdf.getQuery( "default" );
    assertNotNull( query );
    assertNotNull( query.getTransformationFile() );
    assertEquals( "", query.getStepName() );
  }

  @Test
  public void testTransformationNull() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.setQuery( "default", new KettleTransFromFileProducer( null, STEP ) );

    MasterReport report = new MasterReport();
    report.setDataFactory( kettleDataFactory );

    MasterReport r2 = ReportWritingUtil.saveAndLoad( report );
    assertTrue( r2.getDataFactory() instanceof KettleDataFactory );
    KettleDataFactory kdf = (KettleDataFactory) r2.getDataFactory();
    KettleTransformationProducer query = kdf.getQuery( "default" );
    assertNotNull( query );
    assertNull( query.getTransformationFile() );
    assertNotNull( query.getStepName() );
  }

  @Test
  public void testTransformationNameEmpty() throws Exception {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.setQuery( "default", new KettleTransFromFileProducer( null, STEP ) );

    MasterReport report = new MasterReport();
    report.setDataFactory( kettleDataFactory );

    MasterReport r2 = ReportWritingUtil.saveAndLoad( report );
    assertTrue( r2.getDataFactory() instanceof KettleDataFactory );
    KettleDataFactory kdf = (KettleDataFactory) r2.getDataFactory();
    KettleTransformationProducer query = kdf.getQuery( "default" );
    assertNotNull( query );
    assertNull( query.getTransformationFile() );
    assertNotNull( query.getStepName() );
  }


}
