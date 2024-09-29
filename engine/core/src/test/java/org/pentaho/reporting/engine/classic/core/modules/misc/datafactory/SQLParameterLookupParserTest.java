/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
