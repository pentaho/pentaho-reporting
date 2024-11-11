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


package org.pentaho.reporting.engine.classic.extensions.datasources.sampledata;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SampleDataModuleInitializer implements ModuleInitializer {
  private static final Log logger = LogFactory.getLog( SampleDataModuleInitializer.class );

  public SampleDataModuleInitializer() {
  }

  /**
   * Performs the initalization of the module.
   *
   * @throws ModuleInitializeException if an error occurs which prevents the module from being usable.
   */
  public void performInit() throws ModuleInitializeException {
    try {
      Driver driver = ObjectUtilities.loadAndInstantiate
        ( "org.hsqldb.jdbcDriver", SampleDataModuleInitializer.class, Driver.class );
      populateDatabase( driver );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Failed to load the HSQL-DB driver", e );
    }

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/datasources/sampledata/meta-datafactory.xml" );

  }

  private void populateDatabase( Driver driver )
    throws SQLException, IOException {
    Properties p = new Properties();
    p.setProperty( "user", "sa" );
    p.setProperty( "password", "" );
    final Connection connection = driver.connect( "jdbc:hsqldb:mem:SampleData", p );
    connection.setAutoCommit( false );
    try {
      final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
      final String location = config.getConfigProperty
        ( "org.pentaho.reporting.engine.classic.extensions.datasources.sampledata.SampleDataLocation" );
      final InputStream in = SampleDataModule.class.getResourceAsStream( location );
      if ( in == null ) {
        logger.warn( "Invalid database init-script specified. Sample database will be empty. [" + location + "]" );
        return;
      }
      final InputStreamReader inReader = new InputStreamReader( in );
      final BufferedReader bin = new BufferedReader( inReader );
      try {
        final Statement statement = connection.createStatement();
        try {
          String line;
          while ( ( line = bin.readLine() ) != null ) {
            if ( line.startsWith( "CREATE SCHEMA " ) ||
              line.startsWith( "CREATE USER SA " ) ||
              line.startsWith( "GRANT DBA TO SA" ) ) {
              // ignore the error, HSQL sucks
            } else {
              statement.addBatch( StringEscapeUtils.unescapeJava( line ) );
            }
          }
          statement.executeBatch();
        } finally {
          statement.close();
        }
      } finally {
        bin.close();
      }

      connection.commit();
    } finally {
      connection.close();
    }
  }
}
