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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import java.util.Locale;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;

public class ParameterParsingTest extends TestCase
{
  public ParameterParsingTest()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameter()
  {
    final StaticDataRow dataRow = new StaticDataRow(new String[]{"test", "testN"}, new Object[]{"tes{[t", 100});
    AbstractMDXDataFactory.MDXCompiler compiler = new AbstractMDXDataFactory.MDXCompiler(dataRow, Locale.US);
    assertEquals("SELECT \"tes{[t\" AS 100, tes{[t", compiler.translateAndLookup("SELECT ${test,string} AS ${testN,integer}, ${test}"));
  }
}
