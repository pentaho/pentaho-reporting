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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class Prd3828Test extends TestCase
{
  private class SimpleBackend extends CdaQueryBackend
  {
    private String url;
    private String paramUrl;

    public TypedTableModel fetchData(final DataRow dataRow,
                                     final String method,
                                     final Map<String, String> extraParameter)
        throws ReportDataFactoryException
    {
      if (METHOD_LIST_PARAMETERS.equals(method))
      {
        paramUrl = createURL(method, extraParameter);
        final TypedTableModel typedTableModel = new TypedTableModel();
        typedTableModel.addColumn(CdaQueryBackend.PARAM_NAME, String.class);
        typedTableModel.addColumn(CdaQueryBackend.PARAM_TYPE, String.class);
        typedTableModel.addColumn(CdaQueryBackend.PARAM_DEFAULT_VALUE, String.class);
        typedTableModel.addColumn(CdaQueryBackend.PARAM_PATTERN, String.class);

        typedTableModel.addRow("P1", "String", "DefaultString", null);
        typedTableModel.addRow("P2", "Integer", "10", null);
        typedTableModel.addRow("P3", "Date", "2010-12-30", "yyyy-MM-dd");
        typedTableModel.addRow("P4", "StringArray", "A;B;C", null);
        return typedTableModel;
      }
      url = createURL(method, extraParameter);
      return new TypedTableModel();
    }

    public String getParamUrl()
    {
      return paramUrl;
    }

    public String getUrl()
    {
      return url;
    }
  }

  public Prd3828Test()
  {
  }

  protected void setUp() throws Exception
  {
  }

  public void testDefaultUrl() throws ReportDataFactoryException
  {
    final SimpleBackend simpleBackend = new SimpleBackend();

    final CdaDataFactory dataFactory = new CdaDataFactory();
    dataFactory.setBackend(simpleBackend);
    dataFactory.setBaseUrl("http://localhost:12345/testcase");
    dataFactory.setFile("testcase.cda");
    dataFactory.setSolution("testsolution");
    dataFactory.setPath("testpath");
    dataFactory.setUsername("joe");
    dataFactory.setPassword("password");
    dataFactory.setQueryEntry("testQuery", new CdaQueryEntry("myQuery", "cdaId"));

    dataFactory.initialize(new DesignTimeDataFactoryContext());
    dataFactory.queryData("testQuery", new StaticDataRow());

    assertEquals("http://localhost:12345/testcase/content/cda/listParameters?outputType=xml&solution=testsolution&path=testpath&file=testcase.cda&dataAccessId=cdaId", simpleBackend.getParamUrl());
    assertEquals("http://localhost:12345/testcase/content/cda/doQuery?outputType=xml&solution=testsolution&path=testpath&file=testcase.cda&paramP4=A%3BB%3BC&dataAccessId=cdaId&paramP3=2010-12-30&paramP1=DefaultString&paramP2=10", simpleBackend.getUrl());
  }

  public void testFilledUrl() throws ReportDataFactoryException
  {
    final SimpleBackend simpleBackend = new SimpleBackend();

    final CdaDataFactory dataFactory = new CdaDataFactory();
    dataFactory.setBackend(simpleBackend);
    dataFactory.setBaseUrl("http://localhost:12345/testcase");
    dataFactory.setFile("testcase.cda");
    dataFactory.setSolution("testsolution");
    dataFactory.setPath("testpath");
    dataFactory.setUsername("joe");
    dataFactory.setPassword("password");
    dataFactory.setQueryEntry("testQuery", new CdaQueryEntry("myQuery", "cdaId"));

    dataFactory.initialize(new DesignTimeDataFactoryContext());
    dataFactory.queryData("testQuery", new StaticDataRow
        (new String[]{"P1", "P2", "P3", "P4"},
            new Object[]{"x", 10, new Date(1000000000000l), new String[]{"x","y","z"} }));

    assertEquals("http://localhost:12345/testcase/content/cda/listParameters?outputType=xml&solution=testsolution&path=testpath&file=testcase.cda&dataAccessId=cdaId", simpleBackend.getParamUrl());
    assertEquals("http://localhost:12345/testcase/content/cda/doQuery?outputType=xml&solution=testsolution&path=testpath&file=testcase.cda&paramP4=x%3By%3Bz&dataAccessId=cdaId&paramP3=2001-09-09&paramP1=x&paramP2=10", simpleBackend.getUrl());
  }
}
