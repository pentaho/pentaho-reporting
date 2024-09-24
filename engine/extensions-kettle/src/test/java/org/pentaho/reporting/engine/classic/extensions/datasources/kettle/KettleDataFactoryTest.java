/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

public class KettleDataFactoryTest extends DataSourceTestBase {
  private static final String QUERY =
    "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/kettle/row-gen.ktr";
  private static final String STEP = "Formula";

  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY, "query-1.txt", "design-time-query-1.txt" }
  };

  public KettleDataFactoryTest() {
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

  public void testDesignTimeSupport() throws Exception {
    runTestDesignTime( QUERIES_AND_RESULTS );
  }

  protected String getTestDirectory() {
    return "test-src";
  }

  public static void generate() throws Exception {
    final KettleDataFactoryTest test = new KettleDataFactoryTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
    test.runGenerateDesignTime( QUERIES_AND_RESULTS );
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final KettleTransFromFileProducer producer = new KettleTransFromFileProducer( query, STEP );

    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
    kettleDataFactory.setQuery( "default", producer );
    return kettleDataFactory;
  }

  public void testMetaData() throws ReportDataFactoryException {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
    kettleDataFactory.setQuery( "default", new KettleTransFromFileProducer( QUERY, STEP ) );

    final DataFactoryMetaData metaData = kettleDataFactory.getMetaData();
    final Object queryHash = metaData.getQueryHash( kettleDataFactory, "default", new StaticDataRow() );
    assertNotNull( queryHash );

    final KettleDataFactory kettleDataFactory2 = new KettleDataFactory();
    kettleDataFactory2.initialize( new DesignTimeDataFactoryContext() );
    kettleDataFactory2.setQuery( "default", new KettleTransFromFileProducer( QUERY + "2", STEP ) );
    kettleDataFactory2.setQuery( "default2", new KettleTransFromFileProducer( QUERY, STEP ) );

    assertNotEquals( "Physical Query is not the same", queryHash,
      metaData.getQueryHash( kettleDataFactory2, "default", new StaticDataRow() ) );
    assertEquals( "Physical Query is the same", queryHash,
      metaData.getQueryHash( kettleDataFactory2, "default2", new StaticDataRow() ) );
  }

  public void testParameter() throws ReportDataFactoryException {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
    final FormulaParameter[] parameterMappings = {
      FormulaParameter.create( "name", "kettle-name" ),
      FormulaParameter.create( "name2", "k3" ),
      FormulaParameter.create( "name", "k2" )
    };
    final FormulaArgument[] argumentNames = { FormulaArgument.create( "arg0" ) };

    kettleDataFactory
      .setQuery( "default", new KettleTransFromFileProducer( QUERY, STEP, argumentNames, parameterMappings ) );

    final DataFactoryMetaData metaData = kettleDataFactory.getMetaData();
    final String[] fields = metaData.getReferencedFields( kettleDataFactory, "default", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 4, fields.length );
    assertEquals( "arg0", fields[ 0 ] );
    assertEquals( "name", fields[ 1 ] );
    assertEquals( "name2", fields[ 2 ] );
    assertEquals( DataFactory.QUERY_LIMIT, fields[ 3 ] );
  }
}
