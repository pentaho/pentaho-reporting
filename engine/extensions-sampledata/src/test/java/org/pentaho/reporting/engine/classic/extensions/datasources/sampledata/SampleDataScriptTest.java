/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.sampledata;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the shipped sample data scripts. The built-in jdbc:hsqldb:mem:SampleData (the JNDI "SampleData" connection)
 * is filled from them by {@link SampleDataModuleInitializer}, while Report Designer's "SampleData (Local)" connection
 * and the SDK samples open them as native HSQLDB databases, so they must work both ways.
 */
@RunWith( Parameterized.class )
public class SampleDataScriptTest {

  private static final Pattern INSERT_TABLE = Pattern.compile( "^INSERT INTO ((?:\\w+\\.)?\\w+)\\s*VALUES", Pattern.CASE_INSENSITIVE );

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Parameter()
  public String name;

  @Parameter( 1 )
  public File script;

  @Parameter( 2 )
  public boolean openedNatively;

  @Parameters( name = "{0}" )
  public static Collection<Object[]> scripts() {
    return Arrays.asList( new Object[][] {
      { "module default (SampleDataLocation)",
        new File( "src/main/resources/org/pentaho/reporting/engine/classic/extensions/datasources/sampledata/sampledata.script" ),
        false },
      { "prd-ce assembly",
        new File( "../../assemblies/prd-ce/src/main/resources/resource/resources/sampledata/sampledata.script" ), true },
      { "sdk samples", new File( "../../assemblies/sdk/src/main/resources/samples/sql/sampledata.script" ), true },
      { "extensions-mondrian", new File( "../extensions-mondrian/src/main/resources/sql/sampledata.script" ), false } } );
  }

  @Test
  public void testScriptLoadsIntoMemoryDatabase() throws Exception {
    assertTrue( "Script not found: " + script.getAbsolutePath(), script.isFile() );
    String url = "jdbc:hsqldb:mem:sampledatatest_" + System.nanoTime();

    try ( Connection connection = DriverManager.getConnection( url, "sa", "" ) ) {
      connection.setAutoCommit( false );
      // The loader runs the script as one batch, so any failing statement fails here
      try ( BufferedReader reader = Files.newBufferedReader( script.toPath(), StandardCharsets.UTF_8 ) ) {
        SampleDataModuleInitializer.executeScript( connection, reader );
      }
      connection.commit();

      // The JNDI connections log in as pentaho_user (SampleData) and pentaho_admin (SampleDataAdmin)
      for ( String jndiUser : new String[] { "pentaho_user", "pentaho_admin" } ) {
        try ( Connection user = DriverManager.getConnection( url, jndiUser, "password" ) ) {
          assertRowCountsMatchInserts( user );
        }
      }
      try ( Statement statement = connection.createStatement() ) {
        statement.execute( "SHUTDOWN" );
      }
    }
  }

  @Test
  public void testScriptOpensAsNativeDatabaseRepeatedly() throws Exception {
    Assume.assumeTrue( name + " is only loaded by SampleDataModuleInitializer", openedNatively );
    File dir = tempFolder.newFolder();
    File properties = new File( script.getParentFile(), "sampledata.properties" );
    Files.copy( script.toPath(), new File( dir, "sampledata.script" ).toPath() );
    Files.copy( properties.toPath(), new File( dir, "sampledata.properties" ).toPath() );
    String url = "jdbc:hsqldb:file:" + new File( dir, "sampledata" ).getAbsolutePath() + ";ifexists=true";

    // HSQLDB rewrites the script when it closes the database, so the second open reads HSQLDB's own output
    for ( int open = 1; open <= 2; open++ ) {
      try ( Connection connection = DriverManager.getConnection( url, "pentaho_user", "password" ) ) {
        assertRowCountsMatchInserts( connection );
        try ( Statement statement = connection.createStatement() ) {
          statement.execute( "SHUTDOWN" );
        }
      }
    }
  }

  private void assertRowCountsMatchInserts( Connection connection ) throws Exception {
    Map<String, Integer> expected = new TreeMap<>();
    for ( String line : Files.readAllLines( script.toPath(), StandardCharsets.UTF_8 ) ) {
      Matcher matcher = INSERT_TABLE.matcher( line.trim() );
      if ( matcher.find() ) {
        expected.merge( matcher.group( 1 ).toUpperCase(), 1, Integer::sum );
      }
    }
    assertFalse( "No INSERT statements in " + script, expected.isEmpty() );
    try ( Statement statement = connection.createStatement() ) {
      for ( Map.Entry<String, Integer> table : expected.entrySet() ) {
        try ( ResultSet rs = statement.executeQuery( "SELECT COUNT(*) FROM " + table.getKey() ) ) {
          assertTrue( rs.next() );
          assertEquals( "Rows in " + table.getKey() + " from " + script.getName(), table.getValue().intValue(), rs.getInt( 1 ) );
        }
      }
    }
  }
}
