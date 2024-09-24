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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLParameterLookupParser;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

public class SQLParameterLookupParserTest extends TestCase {
  public SQLParameterLookupParserTest() {
  }

  public SQLParameterLookupParserTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testNoOpParsing() {
    final String query =
        "SELECT\n" + "     `wayne$`.`Created`,\n" + "     `wayne$`.`Severity`,\n" + "     `wayne$`.`Status`\n"
            + "FROM\n" + "     `wayne$`";

    final SQLParameterLookupParser parser = new SQLParameterLookupParser( false );
    final String translatedQuery = parser.translateAndLookup( query, new ReportParameterValues() );
    assertEquals( query, translatedQuery );
  }

  public void testReplacementParsing() {
    final String query = "SELECT * FROM TABLE WHERE x = ${parameter}";
    final String expected = "SELECT * FROM TABLE WHERE x = ?";

    final SQLParameterLookupParser parser = new SQLParameterLookupParser( false );
    final ReportParameterValues parameters = new ReportParameterValues();
    parameters.put( "parameter", "value" );
    final String translatedQuery = parser.translateAndLookup( query, parameters );
    assertEquals( expected, translatedQuery );
  }
}
