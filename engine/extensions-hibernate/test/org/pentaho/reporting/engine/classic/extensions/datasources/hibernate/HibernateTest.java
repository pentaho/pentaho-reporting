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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.SQLException;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableModelInfo;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class HibernateTest extends TestCase
{
  public static final String QUERY_1 = "";
  public HibernateTest()
  {
  }

  public HibernateTest(final String s)
  {
    super(s);
  }


  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

/*
  public void testQuery8() throws Exception
  {
    final ByteArrayOutputStream sw = new ByteArrayOutputStream();
    final PrintStream ps = new PrintStream(sw);
    performQueryTest(QUERY_1, ps);
    compareLineByLine("query1-results.txt", sw.toString());
  }
*/
  private void compareLineByLine(final String sourceFile, final String resultText) throws IOException
  {
    final BufferedReader resultReader = new BufferedReader(new StringReader(resultText));
    final BufferedReader compareReader = new BufferedReader(new InputStreamReader
        (ObjectUtilities.getResourceRelativeAsStream(sourceFile, HibernateTest.class)));
    try
    {
      int line = 1;
      String lineResult = resultReader.readLine();
      String lineSource = compareReader.readLine();
      while (lineResult != null && lineSource != null)
      {
        assertEquals("Failure in line " + line, lineResult, lineSource);
        line += 1;
        lineResult = resultReader.readLine();
        lineSource = compareReader.readLine();
      }

      assertNull("Extra lines encountered in live-result " + line, lineResult);
      assertNull("Extra lines encountered in recorded result " + line, lineSource);
    }
    finally
    {
      resultReader.close();
      compareReader.close();
    }
  }

  private void performQueryTest(final String query,
                                final PrintStream out) throws SQLException, ReportDataFactoryException
  {
    final HQLDataFactory mondrianDataFactory = new HQLDataFactory(new DefaultSessionProvider());

    try
    {
      mondrianDataFactory.setQuery("default", query);
      final CloseableTableModel tableModel = (CloseableTableModel) mondrianDataFactory.queryData("default",
          new ParameterDataRow());
      try
      {
        TableModelInfo.printTableModel(tableModel, out);
        TableModelInfo.printTableModelContents(tableModel, out);
      }
      finally
      {
        tableModel.close();
      }
    }
    finally
    {

      mondrianDataFactory.close();
    }
  }

  public void testClone()
  {
    final HQLDataFactory mondrianDataFactory = new HQLDataFactory(new DefaultSessionProvider());
    mondrianDataFactory.derive();    
  }
}
