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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.NumberSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.PerformanceTestSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.PrinterNamesSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.Sequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class SequenceDataSourceWriteIT extends TestCase {
  public SequenceDataSourceWriteIT() {
  }

  public SequenceDataSourceWriteIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testWriteAndLoad() throws IOException, ContentIOException, BundleWriterException, ResourceException {

    final NumberSequence numberSequence = new NumberSequence();
    numberSequence.setParameter( "limit", 1234 );
    numberSequence.setParameter( "step", new BigDecimal( "1.2" ) );
    numberSequence.setParameter( "start", new BigDecimal( "4.5" ) );
    numberSequence.setParameter( "ascending", Boolean.FALSE );

    final PrinterNamesSequence printerSequence = new PrinterNamesSequence();

    final PerformanceTestSequence performanceSequence = new PerformanceTestSequence();
    performanceSequence.setParameter( "limit", 4567 );
    performanceSequence.setParameter( "seed", 1234l );

    final PerformanceTestSequence performanceSequence2 = new PerformanceTestSequence();

    final SequenceDataFactory sdf = new SequenceDataFactory();
    sdf.addSequence( "one", numberSequence );
    sdf.addSequence( "two", printerSequence );
    sdf.addSequence( "three", performanceSequence );
    sdf.addSequence( "four", performanceSequence2 );

    final MasterReport report = new MasterReport();
    report.setDataFactory( sdf );

    final ByteArrayOutputStream reportDefOutputStream = new ByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( report, reportDefOutputStream );

    final byte[] reportDefBits = reportDefOutputStream.toByteArray();
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( reportDefBits, MasterReport.class );
    final MasterReport report2 = (MasterReport) directly.getResource();

    final DataFactory dataFactory = report2.getDataFactory();
    assertTrue( dataFactory instanceof SequenceDataFactory );
    final SequenceDataFactory fac2 = (SequenceDataFactory) dataFactory;

    final String[] queryNames = fac2.getQueryNames();
    assertTrue( ObjectUtilities.equalArray( queryNames, sdf.getQueryNames() ) );
    for ( int i = 0; i < queryNames.length; i++ ) {
      final String queryName = queryNames[i];
      assertEqual( sdf.getSequence( queryName ), fac2.getSequence( queryName ) );
    }
  }

  private void assertEqual( final Sequence s1, final Sequence s2 ) {
    assertEquals( s1.getClass(), s2.getClass() );

    final SequenceDescription sd = s1.getSequenceDescription();
    final int pc = sd.getParameterCount();
    for ( int i = 0; i < pc; i++ ) {
      final String name = sd.getParameterName( i );
      assertEquals( "Failed at " + name, s1.getParameter( name ), s2.getParameter( name ) );
    }
  }
}
