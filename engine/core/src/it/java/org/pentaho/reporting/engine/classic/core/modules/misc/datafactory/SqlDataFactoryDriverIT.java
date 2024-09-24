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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

public class SqlDataFactoryDriverIT extends DataSourceTestBase {
  static final String[][] QUERIES_AND_RESULTS = new String[][] { { "SELECT * FROM Customers", "query-1.txt" } };

  public SqlDataFactoryDriverIT() {
  }

  public SqlDataFactoryDriverIT( final String s ) {
    super( s );
  }

  public void testMetaDataDrc() {
    final DriverConnectionProvider drc = new DriverConnectionProvider();
    drc.setDriver( "org.hsqldb.jdbcDriver" );
    drc.setUrl( "jdbc:hsqldb:mem:SampleData" );
    drc.setProperty( "user", "sa" );
    drc.setProperty( "password", "" );
    final SQLReportDataFactory sqlReportDataFactory = new SQLReportDataFactory( drc );
    final DataFactoryMetaData metaData = sqlReportDataFactory.getMetaData();
    assertNull( "No name property set, so display-name must be null", metaData
        .getDisplayConnectionName( sqlReportDataFactory ) );
    drc.setProperty( "::pentaho-reporting::name", "test" );
    assertEquals( "Name property set, so display name must be test", "test", metaData
        .getDisplayConnectionName( sqlReportDataFactory ) );
    sqlReportDataFactory.setQuery( "test", "SELECT * FROM TABLE" );

    assertNotNull( "QueryHash must exist", metaData.getQueryHash( sqlReportDataFactory, "test", new StaticDataRow() ) );

    final SQLReportDataFactory sqlReportDataFactory2 = new SQLReportDataFactory( drc );
    sqlReportDataFactory2.setQuery( "test", "SELECT * FROM TABLE2" );

    assertNotEquals( "Physical Queries do not match, so query hash must be different", metaData.getQueryHash(
        sqlReportDataFactory, "test", new StaticDataRow() ), ( metaData.getQueryHash( sqlReportDataFactory2, "test",
        new StaticDataRow() ) ) );

    sqlReportDataFactory2.setQuery( "test2", "SELECT * FROM TABLE" );
    final Object qh1 = metaData.getQueryHash( sqlReportDataFactory, "test", new StaticDataRow() );
    final Object qh2 = metaData.getQueryHash( sqlReportDataFactory2, "test2", new StaticDataRow() );
    assertEquals( "Physical Queries match, so queries are considered the same", qh1, qh2 );

    final DriverConnectionProvider drc2 = new DriverConnectionProvider();
    drc.setDriver( "org.hsqldb.jdbcDriver" );
    drc.setUrl( "jdbc:hsqldb:mem:SampleData2" );
    drc.setProperty( "user", "sa" );
    drc.setProperty( "password", "" );
    final SQLReportDataFactory sqlReportDataFactory3 = new SQLReportDataFactory( drc2 );
    sqlReportDataFactory3.setQuery( "test", "SELECT * FROM TABLE2" );
    assertNotEquals( "Connections do not match, so query hash must be different", metaData.getQueryHash(
        sqlReportDataFactory, "test", new StaticDataRow() ), ( metaData.getQueryHash( sqlReportDataFactory3, "test",
        new StaticDataRow() ) ) );

    sqlReportDataFactory3.setQuery( "test2", "SELECT * FROM TABLE" );
    assertNotEquals( "Connections do not match, so queries are considered the same", metaData.getQueryHash(
        sqlReportDataFactory, "test", new StaticDataRow() ), metaData.getQueryHash( sqlReportDataFactory3, "test2",
        new StaticDataRow() ) );
  }

  public void testParameterMetadata() throws ReportDataFactoryException {
    final DriverConnectionProvider drc = new DriverConnectionProvider();
    drc.setDriver( "org.hsqldb.jdbcDriver" );
    drc.setUrl( "jdbc:hsqldb:mem:SampleData" );
    drc.setProperty( "user", "sa" );
    drc.setProperty( "password", "" );
    final SQLReportDataFactory sqlReportDataFactory = new SQLReportDataFactory( drc );
    initializeDataFactory( sqlReportDataFactory );
    final DataFactoryMetaData metaData = sqlReportDataFactory.getMetaData();
    sqlReportDataFactory.setQuery( "test", "SELECT * FROM TABLE WHERE p=${x}" );
    String[] fields = metaData.getReferencedFields( sqlReportDataFactory, "test", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 2, fields.length );
    assertEquals( "x", fields[0] );
    assertEquals( DataFactory.QUERY_LIMIT, fields[1] );

    sqlReportDataFactory.setQuery( "test2", "SELECT * FROM TABLE WHERE p=${x} OR p=${y} OR p=${x}" );
    fields = metaData.getReferencedFields( sqlReportDataFactory, "test2", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 3, fields.length );
    assertEquals( "x", fields[0] );
    assertEquals( "y", fields[1] );
    assertEquals( DataFactory.QUERY_LIMIT, fields[2] );

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

  protected DataFactory createDataFactory( final String query ) {
    final DriverConnectionProvider drc = new DriverConnectionProvider();
    drc.setDriver( "org.hsqldb.jdbcDriver" );
    drc.setUrl( "jdbc:hsqldb:mem:SampleData" );
    drc.setProperty( "user", "sa" );
    drc.setProperty( "password", "" );
    final SQLReportDataFactory sqlReportDataFactory = new SQLReportDataFactory( drc );
    sqlReportDataFactory.setQuery( "default", query );
    return sqlReportDataFactory;
  }
}
