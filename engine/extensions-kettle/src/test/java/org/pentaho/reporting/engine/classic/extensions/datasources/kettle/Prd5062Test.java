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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

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
