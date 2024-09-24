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

package org.pentaho.reporting.engine.classic.samples;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.wizard.RelationalAutoGeneratorPreProcessor;

/**
 * Generates a report in the following scenario:
 * <ol>
 * <li>The report definition file is a .prpt file which will be loaded and parsed
 * <li>The data factory is a simple JDBC data factory using HSQLDB
 * <li>There are no runtime report parameters used
 * </ol>
 */
public class Sample2 extends AbstractReportGenerator {
  private static final String QUERY_NAME = "ReportQuery";

  /**
   * Default constructor for this sample report generator
   */
  public Sample2() { }

  /**
   * Returns the report definition which will be used to generate the report. In this case, the report will be
   * loaded and parsed from a file contained in this package.
   *
   * @return the loaded and parsed report definition to be used in report generation.
   */
  public MasterReport getReportDefinition() {
    final MasterReport report = new MasterReport();
    report.setQuery( QUERY_NAME );
    report.addPreProcessor( new RelationalAutoGeneratorPreProcessor() );
    return report;
  }

  /**
   * Returns the data factory which will be used to generate the data used during report generation. In this example,
   * we will return null since the data factory has been defined in the report definition.
   *
   * @return the data factory used with the report generator
   */
  public DataFactory getDataFactory() {
    final DriverConnectionProvider sampleDriverConnectionProvider = new DriverConnectionProvider();
    sampleDriverConnectionProvider.setDriver( "org.hsqldb.jdbcDriver" );
    sampleDriverConnectionProvider.setUrl( "jdbc:hsqldb:./sql/sampledata" );
    sampleDriverConnectionProvider.setProperty( "user", "sa" );
    sampleDriverConnectionProvider.setProperty( "password", "" );

    final SQLReportDataFactory dataFactory = new SQLReportDataFactory( sampleDriverConnectionProvider );
    dataFactory.setQuery( QUERY_NAME,
        "select CUSTOMERNAME, CITY, STATE, POSTALCODE, COUNTRY from CUSTOMERS order by UPPER(CUSTOMERNAME)" );

    return dataFactory;
  }

  /**
   * Returns the set of runtime report parameters. This sample report does not use report parameters, so the
   * method will return <code>null</code>
   *
   * @return <code>null</code> indicating the report generator does not use any report parameters
   */
  public Map<String, Object> getReportParameters() {
    return null;
  }

  public static void main( String[] args ) throws IOException, ReportProcessingException {
    // Create an output filename
    final File outputFilename = new File( Sample2.class.getSimpleName() + ".html" );

    // Generate the report
    new Sample2().generateReport( AbstractReportGenerator.OutputType.HTML, outputFilename );

    // Output the location of the file
    System.err.println( "Generated the report [" + outputFilename.getAbsolutePath() + "]" );
  }
}
