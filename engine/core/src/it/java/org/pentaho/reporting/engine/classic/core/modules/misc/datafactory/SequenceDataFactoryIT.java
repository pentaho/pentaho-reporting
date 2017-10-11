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
