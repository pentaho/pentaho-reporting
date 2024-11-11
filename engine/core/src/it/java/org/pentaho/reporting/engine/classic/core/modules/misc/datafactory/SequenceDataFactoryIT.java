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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.NumberSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.PerformanceTestSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import java.math.BigDecimal;

public class SequenceDataFactoryIT extends DataSourceTestBase {
  public static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { "number_sequence", "sequence-datafactory-number_sequence" + "-results.txt" },
    { "perf_sequence", "sequence-datafactory-perf_sequence" + "-results.txt" } };

  public SequenceDataFactoryIT() {
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    if ( "number_sequence".equals( query ) ) {
      final NumberSequence numberSequence = new NumberSequence();
      numberSequence.setParameter( "limit", 1234 );
      numberSequence.setParameter( "step", new BigDecimal( "1.2" ) );
      numberSequence.setParameter( "start", new BigDecimal( "4.5" ) );
      numberSequence.setParameter( "ascending", Boolean.FALSE );

      final SequenceDataFactory sdf = new SequenceDataFactory();
      sdf.addSequence( "default", numberSequence );
      return sdf;
    } else {
      final PerformanceTestSequence performanceSequence = new PerformanceTestSequence();
      performanceSequence.setParameter( "limit", 4567 );
      performanceSequence.setParameter( "seed", 1234l );

      final SequenceDataFactory sdf = new SequenceDataFactory();
      sdf.addSequence( "default", performanceSequence );
      return sdf;
    }
  }

  public void testSaveAndLoad() throws Exception {
    runSaveAndLoad( QUERIES_AND_RESULTS );
  }

  public void testDerive() throws Exception {
    runDerive( QUERIES_AND_RESULTS );
  }

  public void testSerialize() throws Exception {
    runSerialize( QUERIES_AND_RESULTS );
  }

  public void testQuery() throws Exception {
    runTest( QUERIES_AND_RESULTS );
  }

  public void testSequenceDataSourceInsulation() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0001.prpt" );
    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SequenceDataFactory sequenceDf = (SequenceDataFactory) dataFactory.getReference( 0 );
    PerformanceTestSequence sequence = (PerformanceTestSequence) sequenceDf.getSequence( "Query 1" );
    sequence.setParameter( "limit", 20000 );

    MasterReport reportV = DebugReportRunner.parseGoldenSampleReport( "Prd-4606-0001.prpt" );
    CompoundDataFactory dataFactoryV = (CompoundDataFactory) reportV.getDataFactory();
    SequenceDataFactory sequenceDfV = (SequenceDataFactory) dataFactoryV.getReference( 0 );
    PerformanceTestSequence sequenceV = (PerformanceTestSequence) sequenceDfV.getSequence( "Query 1" );

    assertEquals( 10, sequenceV.getParameter( "limit" ) );
  }
}
