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
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;

public class BandedMDXTableModelTest extends DataSourceTestBase
{

  private static final String QUERY_BY_HIERARCHY = "SELECT {[Measures].[Quantity]} ON COLUMNS, TOPCOUNT(NONEMPTYCROSSJOIN([Markets].[Country].MEMBERS,[Markets.City].[City].MEMBERS), 5) ON ROWS FROM [SteelWheelsSales]";

  protected DataFactory createDataFactory(final String query) throws ReportDataFactoryException
  {
    final BandedMDXDataFactory mdxDataFactory = new BandedMDXDataFactory();
    mdxDataFactory.setCubeFileProvider(new DefaultCubeFileProvider
        ("test/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels_hierarchy.mondrian.xml"));
    mdxDataFactory.setDataSourceProvider(new JndiDataSourceProvider("SampleData"));
    mdxDataFactory.setQuery("default", query, null, null);
    initializeDataFactory(mdxDataFactory);
    return mdxDataFactory;
  }

  public void testQuery() throws Exception
  {
    String[][] QUERIES_AND_RESULTS = new String[][]{
        {QUERY_BY_HIERARCHY, "steelwheels_hierarchy_result.txt"}
    };
    runTest(QUERIES_AND_RESULTS);
  }
}
