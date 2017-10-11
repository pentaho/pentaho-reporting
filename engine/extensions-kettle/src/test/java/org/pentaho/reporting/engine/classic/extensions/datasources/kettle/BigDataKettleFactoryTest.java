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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class BigDataKettleFactoryTest extends DataSourceTestBase {
  private static final String QUERY =
    "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/kettle/row-gen.ktr";;
  private static final String STEP = "Formula";

  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY, "query-1.txt" }
  };

  private static final String DEFAULT = "default";

  private static final String DEFAULT_2 = "default2";

  public BigDataKettleFactoryTest() {
  }

  public void testSaveAndLoad() throws Exception {
    runSaveAndLoad( QUERIES_AND_RESULTS );
  }

  public void testSaveAndLoadForSubReports() throws Exception {
    runSaveAndLoadForSubReports( QUERIES_AND_RESULTS );
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

  protected String getTestDirectory() {
    return "test-src";
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    try {
      URL res = getClass().getResource( "embedded-row-gen.ktr" );
      assertNotNull( res );

      ResourceManager mgr = new ResourceManager();
      ResourceKey key = mgr.createKey( res );
      final byte[] resource = mgr.load( key ).getResource( mgr );
      final EmbeddedKettleTransformationProducer producer =
        new EmbeddedKettleTransformationProducer( new FormulaArgument[ 0 ], new FormulaParameter[ 0 ], "dummy-id",
          resource );

      final KettleDataFactory kettleDataFactory = new KettleDataFactory();
      kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
      kettleDataFactory.setQuery( DEFAULT, producer );
      return kettleDataFactory;
    } catch ( ResourceException re ) {
      throw new ReportDataFactoryException( "Failed to load raw-data", re );
    }
  }

  public void testMetaData() throws ReportDataFactoryException {
    final KettleDataFactory kettleDataFactory = new KettleDataFactory();
    kettleDataFactory.initialize( new DesignTimeDataFactoryContext() );
    kettleDataFactory.setQuery( DEFAULT,
      new KettleTransFromFileProducer( QUERY, STEP, new FormulaArgument[ 0 ], new FormulaParameter[ 0 ] ) );

    final DataFactoryMetaData metaData = kettleDataFactory.getMetaData();
    final Object queryHash = metaData.getQueryHash( kettleDataFactory, "default", new StaticDataRow() );
    assertNotNull( queryHash );

    final KettleDataFactory kettleDataFactory2 = new KettleDataFactory();
    kettleDataFactory2.initialize( new DesignTimeDataFactoryContext() );
    kettleDataFactory2.setQuery( DEFAULT,
      new KettleTransFromFileProducer( QUERY + "2", STEP, new FormulaArgument[ 0 ], new FormulaParameter[ 0 ] ) );
    kettleDataFactory2.setQuery(DEFAULT_2,
      new KettleTransFromFileProducer( QUERY, STEP, new FormulaArgument[ 0 ], new FormulaParameter[ 0 ] ) );

    assertNotEquals( "Physical Query is not the same", queryHash,
      metaData.getQueryHash( kettleDataFactory2, DEFAULT, new StaticDataRow() ) );
    assertEquals( "Physical Query is the same", queryHash,
      metaData.getQueryHash( kettleDataFactory2, DEFAULT_2, new StaticDataRow() ) );
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
    kettleDataFactory.setQuery( "default",
      new KettleTransFromFileProducer( QUERY, STEP, argumentNames, parameterMappings ) );

    final DataFactoryMetaData metaData = kettleDataFactory.getMetaData();
    final String[] fields = metaData.getReferencedFields( kettleDataFactory, "default", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 4, fields.length );
    assertEquals( "arg0", fields[ 0 ] );
    assertEquals( "name", fields[ 1 ] );
    assertEquals( "name2", fields[ 2 ] );
    assertEquals( DataFactory.QUERY_LIMIT, fields[ 3 ] );
  }

  public void testEmptyQueriesAreHomogeneous() {
    KettleDataFactory kettleDataFactory = new KettleDataFactory();
    assertTrue(kettleDataFactory.queriesAreHomogeneous());
  }

  public void testEmptyQueriesAreHomogeneousWhereProducerIsKettleTransFromFileProducer() {
    KettleDataFactory kettleDataFactory = new KettleDataFactory();

    kettleDataFactory.setQuery( "default",
        new KettleTransFromFileProducer( QUERY, STEP, new FormulaArgument[ 0 ], new FormulaParameter[ 0 ] ) );
    assertFalse(kettleDataFactory.queriesAreHomogeneous());
  }

  public void testEmptyQueriesAreHomogeneousWhereProducersAreEmbeddedKettleTransformationProducer() throws Exception
  {
    KettleDataFactory kettleDataFactory = new KettleDataFactory();

    URL res = getClass().getResource( "embedded-row-gen.ktr" );
    assertNotNull( res );

    ResourceManager mgr = new ResourceManager();
    ResourceKey key = mgr.createKey( res );
    final byte[] resource = mgr.load( key ).getResource( mgr );

    EmbeddedKettleTransformationProducer producer =
        new EmbeddedKettleTransformationProducer( new FormulaArgument[ 0 ], new FormulaParameter[ 0 ], "dummy-id",
            resource );
    kettleDataFactory.setQuery(DEFAULT, producer);
    kettleDataFactory.setQuery(DEFAULT_2, producer);



    assertTrue(kettleDataFactory.queriesAreHomogeneous());
  }

}
