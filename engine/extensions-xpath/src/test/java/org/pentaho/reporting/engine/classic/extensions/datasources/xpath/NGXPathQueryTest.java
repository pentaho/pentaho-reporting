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

package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

public class NGXPathQueryTest extends DataSourceTestBase {
  public static final String QUERY_1 = "/*/*";

  private static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY_1, "query-ng-results.txt" },
  };


  public NGXPathQueryTest() {
  }

  public NGXPathQueryTest( final String s ) {
    super( s );
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final XPathDataFactory dataFactory = new XPathDataFactory();
    dataFactory
      .setXqueryDataFile( "src/test/resources/org/pentaho/reporting/engine/classic/extensions/datasources/xpath/customer.xml" );
    initializeDataFactory( dataFactory );
    dataFactory.setQuery( "default", query, false );
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

  public static void main( final String[] args ) throws Exception {
    final NGXPathQueryTest test = new NGXPathQueryTest();
    test.setUp();
    test.runGenerate( QUERIES_AND_RESULTS );
  }
}
