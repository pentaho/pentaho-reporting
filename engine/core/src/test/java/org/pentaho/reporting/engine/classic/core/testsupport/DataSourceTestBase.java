/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryDesignTimeSupport;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableModelInfo;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public abstract class DataSourceTestBase extends TestCase {
  public DataSourceTestBase() {
  }

  public DataSourceTestBase( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  protected void runSerialize( final String[][] queriesAndResults ) throws Exception {
    if ( queriesAndResults.length == 0 ) {
      return;
    }

    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String query = queriesAndResults[i][0];
      final String resultFile = queriesAndResults[i][1];

      DebugLog.log( "Executing query " + query );

      final DataFactory dataFactory = createDataFactory( query );

      final ByteArrayOutputStream bo = new ByteArrayOutputStream();
      final ObjectOutputStream out = new ObjectOutputStream( bo );
      out.writeObject( dataFactory );

      final ObjectInputStream oin = new ObjectInputStream( new ByteArrayInputStream( bo.toByteArray() ) );
      final DataFactory e2 = (DataFactory) oin.readObject();
      assertNotNull( e2 ); // cannot assert equals, as this is not implemented ...
      initializeDataFactory( e2 );
      final String queryResult = performQueryTest( e2 );
      compareLineByLine( resultFile, queryResult );
    }
  }

  protected void runDerive( final String[][] queriesAndResults ) throws Exception {
    if ( queriesAndResults.length == 0 ) {
      return;
    }

    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String query = queriesAndResults[i][0];
      final String resultFile = queriesAndResults[i][1];
      final DataFactory dataFactory = createDataFactory( query );

      final DataFactory e2 = dataFactory.derive();
      assertNotNull( e2 ); // cannot assert equals, as this is not implemented ...
      initializeDataFactory( e2 );
      final String queryResult = performQueryTest( e2 );
      compareLineByLine( resultFile, queryResult );
    }
  }

  protected void runSaveAndLoadForSubReports( final String[][] queriesAndResults ) throws Exception {
    if ( queriesAndResults.length == 0 ) {
      return;
    }

    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String query = queriesAndResults[i][0];
      final String resultFile = queriesAndResults[i][1];
      final DataFactory dataFactory = createDataFactory( query );

      SubReport subReport = new SubReport();
      subReport.setDataFactory( dataFactory );

      final MasterReport report = new MasterReport();
      report.getReportHeader().addSubReport( subReport );

      final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
      BundleWriter.writeReportToZipStream( report, bout );
      final ResourceManager mgr = new ResourceManager();
      mgr.registerDefaults();

      final Resource resource = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
      final MasterReport r2 = (MasterReport) resource.getResource();
      final SubReport sr2 = r2.getReportHeader().getSubReport( 0 );
      final DataFactory e2 = sr2.getDataFactory();
      assertNotNull( e2 ); // cannot assert equals, as this is not implemented ...
      initializeDataFactory( e2 );
      final String queryResult = performQueryTest( e2 );
      compareLineByLine( resultFile, queryResult );
    }
  }

  protected void runSaveAndLoad( final String[][] queriesAndResults ) throws Exception {
    if ( queriesAndResults.length == 0 ) {
      return;
    }

    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String query = queriesAndResults[i][0];
      final String resultFile = queriesAndResults[i][1];
      final DataFactory dataFactory = createDataFactory( query );

      final DataFactory e2 = loadAndSaveOnReport( dataFactory );

      assertNotNull( e2 ); // cannot assert equals, as this is not implemented ...
      initializeDataFactory( e2 );
      final String queryResult = performQueryTest( e2 );
      compareLineByLine( resultFile, queryResult );
    }
  }

  public static DataFactory loadAndSaveOnReport( final DataFactory dataFactory ) throws IOException,
    BundleWriterException, ContentIOException, ResourceException {
    final MasterReport report = new MasterReport();
    report.setDataFactory( dataFactory );

    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( report, bout );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final Resource resource = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
    final MasterReport r2 = (MasterReport) resource.getResource();
    return r2.getDataFactory();
  }

  protected void runTest( final String[][] queriesAndResults ) throws Exception {
    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String query = queriesAndResults[i][0];
      final String resultFile = queriesAndResults[i][1];
      final DataFactory dataFactory = createDataFactory( query );
      initializeDataFactory( dataFactory );
      final String queryResult = performQueryTest( dataFactory );
      compareLineByLine( resultFile, queryResult );
    }
  }

  public void runGenerate( final String[][] queriesAndResults ) throws Exception {
    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String query = queriesAndResults[i][0];
      final String resultFile = queriesAndResults[i][1];
      final DataFactory dataFactory = createDataFactory( query );
      initializeDataFactory( dataFactory );
      generate( dataFactory, resultFile );
    }
  }

  protected void runTestDesignTime( final String[][] queriesAndResults ) throws Exception {
    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String[] queriesAndResult = queriesAndResults[i];
      final String query = queriesAndResult[0];
      final String resultFile = queriesAndResult[2];
      final DataFactoryDesignTimeSupport dataFactory = (DataFactoryDesignTimeSupport) createDataFactory( query );
      initializeDataFactory( dataFactory );
      final String queryResult = performDesignTimeTest( dataFactory );
      compareLineByLine( resultFile, queryResult );
    }
  }

  public void runGenerateDesignTime( final String[][] queriesAndResults ) throws Exception {
    for ( int i = 0; i < queriesAndResults.length; i++ ) {
      final String[] queriesAndResult = queriesAndResults[i];
      final String query = queriesAndResult[0];
      final String resultFile = queriesAndResult[2];
      final DataFactoryDesignTimeSupport dataFactory = (DataFactoryDesignTimeSupport) createDataFactory( query );
      initializeDataFactory( dataFactory );
      generateDesignTime( dataFactory, resultFile );
    }
  }

  protected void generate( final DataFactory dataFactory, final String resultFile ) throws ReportDataFactoryException,
    SQLException, IOException {
    final String queryResult = performQueryTest( dataFactory );

    final String packageName = getClass().getPackage().getName();
    final String pathName = packageName.replace( ".", "/" );
    final File file = new File( getTestDirectory() + "/" + pathName + "/" + resultFile );
    System.out.println( "Generating test result: " + file.getAbsolutePath() );
    final FileOutputStream fout = new FileOutputStream( file );
    try {
      fout.write( queryResult.getBytes( "UTF-8" ) );
    } finally {
      fout.close();
    }
  }

  protected void generateDesignTime( final DataFactoryDesignTimeSupport dataFactory, final String resultFile )
    throws ReportDataFactoryException, SQLException, IOException {
    final String queryResult = performDesignTimeTest( dataFactory );

    final String packageName = getClass().getPackage().getName();
    final String pathName = packageName.replace( ".", "/" );
    final File file = new File( getTestDirectory() + "/" + pathName + "/" + resultFile );
    System.out.println( "Generating test result: " + file.getAbsolutePath() );
    final FileOutputStream fout = new FileOutputStream( file );
    try {
      fout.write( queryResult.getBytes( "UTF-8" ) );
    } finally {
      fout.close();
    }
  }

  protected String getTestDirectory() {
    return "test";
  }

  protected void initializeDataFactory( final DataFactory dataFactory ) throws ReportDataFactoryException {
    dataFactory.initialize( new DesignTimeDataFactoryContext() );
  }

  protected abstract DataFactory createDataFactory( final String query ) throws ReportDataFactoryException;

  protected void compareLineByLine( final String sourceFile, final String resultText ) throws IOException {
    final BufferedReader resultReader = new BufferedReader( new StringReader( resultText ) );
    final InputStream stream = ObjectUtilities.getResourceRelativeAsStream( sourceFile, getClass() );
    if ( stream == null ) {
      throw new NullPointerException( "Cannot locate resource '" + sourceFile + "' with context " + getClass() );
    }
    final BufferedReader compareReader = new BufferedReader( new InputStreamReader( stream, "UTF-8" ) );
    try {
      int line = 1;
      String lineResult = resultReader.readLine();
      String lineSource = compareReader.readLine();
      while ( lineResult != null && lineSource != null ) {
        assertEquals( "Failure in line " + line + " (file: " + sourceFile + ")", lineSource, lineResult );
        line += 1;
        lineResult = resultReader.readLine();
        lineSource = compareReader.readLine();
      }

      assertNull( "Extra lines encountered in live-result " + line + " (file: " + sourceFile + ")", lineResult );
      assertNull( "Extra lines encountered in recorded result " + line + " (file: " + sourceFile + ")", lineSource );
    } finally {
      resultReader.close();
      compareReader.close();
    }
  }

  protected String getLogicalQueryForNextTest() {
    return "default";
  }

  protected String performQueryTest( final DataFactory dataFactory ) throws SQLException, ReportDataFactoryException {
    final ByteArrayOutputStream sw = new ByteArrayOutputStream();
    try {
      final PrintStream ps = new PrintStream( sw, true, "UTF-8" );
      final TableModel tableModel = dataFactory.queryData( getLogicalQueryForNextTest(), getParameterForNextTest() );
      generateCompareText( ps, tableModel );
      if ( tableModel instanceof CloseableTableModel ) {
        final CloseableTableModel ctm = (CloseableTableModel) tableModel;
        ctm.close();
      }
      return sw.toString( "UTF-8" );
    } catch ( UnsupportedEncodingException e ) {
      throw new ReportDataFactoryException( "If UTF-8 is not supported, we are in trouble." );
    } finally {
      dataFactory.close();
    }
  }

  protected String performDesignTimeTest( final DataFactoryDesignTimeSupport dataFactory ) throws SQLException,
    ReportDataFactoryException {
    final ByteArrayOutputStream sw = new ByteArrayOutputStream();
    try {
      final PrintStream ps = new PrintStream( sw, true, "UTF-8" );
      final TableModel tableModel =
          dataFactory.queryDesignTimeStructure( getLogicalQueryForNextTest(), getParameterForNextTest() );
      generateCompareText( ps, tableModel );
      if ( tableModel instanceof CloseableTableModel ) {
        final CloseableTableModel ctm = (CloseableTableModel) tableModel;
        ctm.close();
      }
      return sw.toString( "UTF-8" );
    } catch ( UnsupportedEncodingException e ) {
      throw new ReportDataFactoryException( "If UTF-8 is not supported, we are in trouble." );
    } finally {
      dataFactory.close();
    }
  }

  protected void generateCompareText( final PrintStream ps, final TableModel tableModel ) {
    TableModelInfo.printTableModel( tableModel, ps );
    TableModelInfo.printTableMetaData( tableModel, ps );
    TableModelInfo.printTableCellAttributes( tableModel, ps );
    TableModelInfo.printTableModelContents( tableModel, ps );
  }

  protected DataRow getParameterForNextTest() {
    return new ParameterDataRow();
  }

  public static void assertNotEquals( final String message, final Object o1, final Object o2 ) {
    if ( o1 == o2 ) {
      fail( message );
    }
    if ( o1 != null && o2 == null ) {
      return;
    }
    if ( o1 == null ) {
      return;
    }
    if ( o1.equals( o2 ) ) {
      fail( message );
    }
  }

}
