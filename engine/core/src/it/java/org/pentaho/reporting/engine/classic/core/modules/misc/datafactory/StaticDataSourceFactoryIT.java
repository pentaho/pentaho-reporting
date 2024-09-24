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
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Creation-Date: Jan 18, 2007, 5:48:56 PM
 *
 * @author Thomas Morgner
 */
public class StaticDataSourceFactoryIT extends DataSourceTestBase {
  public StaticDataSourceFactoryIT() {
  }

  public StaticDataSourceFactoryIT( String string ) {
    super( string );
  }

  private static final String[][] QUERIES_AND_RESULTS =
      new String[][] {
        { StaticDataSourceFactoryIT.class.getName() + "#createSimpleTableModel", "static-datafactory-1.txt" },
        { StaticDataSourceFactoryIT.class.getName() + "#createSimpleTableModel()", "static-datafactory-2.txt" },
        { StaticDataSourceFactoryIT.class.getName() + "#createParametrizedTableModel(parameter2,parameter1)",
          "static-datafactory-3.txt" },

        { StaticDataSourceFactoryTestSupport.class.getName() + "#createSimpleTableModel", "static-datafactory-4.txt" },
        { StaticDataSourceFactoryTestSupport.class.getName() + "#createSimpleTableModel()", "static-datafactory-5.txt" },
        { StaticDataSourceFactoryTestSupport.class.getName() + "#createParametrizedTableModel(parameter2,parameter1)",
          "static-datafactory-6.txt" },

        { StaticDataSourceFactoryTestSupport.class.getName() + "()", "static-datafactory-7.txt" },
        { StaticDataSourceFactoryTestSupport.class.getName() + "()#createSimpleTableModel", "static-datafactory-8.txt" },
        { StaticDataSourceFactoryTestSupport.class.getName() + "()#createSimpleTableModel()",
          "static-datafactory-9.txt" },
        {
          StaticDataSourceFactoryTestSupport.class.getName() + "()#createParametrizedTableModel(parameter2,parameter1)",
          "static-datafactory-10.txt" },

        { StaticDataSourceFactoryTestSupport.class.getName() + "(parameter1,parameter2)", "static-datafactory-11.txt" },
        { StaticDataSourceFactoryTestSupport.class.getName() + "(parameter1,parameter2)#createSimpleTableModel",
          "static-datafactory-12.txt" },
        { StaticDataSourceFactoryTestSupport.class.getName() + "(parameter1,parameter2)#createSimpleTableModel()",
          "static-datafactory-13.txt" },
        {
          StaticDataSourceFactoryTestSupport.class.getName()
          + "(parameter1,parameter2)#createParametrizedTableModel(parameter2,parameter1)",
          "static-datafactory-14.txt" }, };

  protected DataFactory createDataFactory( final String query ) {
    final NamedStaticDataFactory dataFactory = new NamedStaticDataFactory();
    dataFactory.setQuery( "default", query );
    return dataFactory;
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

  public static void main( String[] args ) throws Exception {
    final StaticDataSourceFactoryIT test = new StaticDataSourceFactoryIT();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }

  protected DataRow getParameterForNextTest() {
    return new StaticDataRow( new String[] { "parameter1", "parameter2" }, new Object[] { "test", new Integer( 5 ) } );
  }

  public void testMetaData() {
    final NamedStaticDataFactory sqlReportDataFactory = new NamedStaticDataFactory();
    final DataFactoryMetaData metaData = sqlReportDataFactory.getMetaData();
    sqlReportDataFactory.setQuery( "test", StaticDataSourceFactoryTestSupport.class.getName()
        + "()#createSimpleTableModel" );

    assertNotNull( "QueryHash must exist", metaData.getQueryHash( sqlReportDataFactory, "test", new StaticDataRow() ) );

    final NamedStaticDataFactory sqlReportDataFactory2 = new NamedStaticDataFactory();
    sqlReportDataFactory2.setQuery( "test", StaticDataSourceFactoryTestSupport.class.getName()
        + "()#createSimpleTableModel()" );

    assertNotEquals( "Physical Queries do not match, so query hash must be different", metaData.getQueryHash(
        sqlReportDataFactory, "test", new StaticDataRow() ), ( metaData.getQueryHash( sqlReportDataFactory2, "test",
        new StaticDataRow() ) ) );

    sqlReportDataFactory2.setQuery( "test2", StaticDataSourceFactoryTestSupport.class.getName()
        + "()#createSimpleTableModel" );
    final Object qh1 = metaData.getQueryHash( sqlReportDataFactory, "test", new StaticDataRow() );
    final Object qh2 = metaData.getQueryHash( sqlReportDataFactory2, "test2", new StaticDataRow() );
    assertEquals( "Physical Queries match, so queries are considered the same", qh1, qh2 );
  }

  public void testParameterMetadata() {
    final NamedStaticDataFactory sqlReportDataFactory = new NamedStaticDataFactory();
    final DataFactoryMetaData metaData = sqlReportDataFactory.getMetaData();
    sqlReportDataFactory.setQuery( "test", StaticDataSourceFactoryTestSupport.class.getName()
        + "(parameter1,parameter2)#createParametrizedTableModel(parameter2,parameter1,parameter3)" );
    final String[] fields = metaData.getReferencedFields( sqlReportDataFactory, "test", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 3, fields.length );
    assertEquals( "parameter1", fields[0] );
    assertEquals( "parameter2", fields[1] );
    assertEquals( "parameter3", fields[2] );
  }

  /**
   * @noinspection UnusedDeclaration
   */
  public static TableModel createParametrizedTableModel( int i1, String s1 ) {
    assertEquals( "Passing primitive parameters failed", 5, i1 );
    assertEquals( "Passing object parameters failed", "test", s1 );
    return new DefaultTableModel();
  }

  /**
   * @noinspection UnusedDeclaration
   */
  public static TableModel createSimpleTableModel() {
    return new DefaultTableModel();
  }

}
