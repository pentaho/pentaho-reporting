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


package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;

import java.util.Locale;

/**
 * Todo: Document me!
 * <p/>
 * Date: 25.01.11 Time: 15:59
 *
 * @author Thomas Morgner.
 */
public class ParameterParsingTest extends TestCase {
  public ParameterParsingTest() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameter() {
    final StaticDataRow dataRow = new StaticDataRow( new String[] { "test", "testN" }, new Object[] { "tes{[t", 100 } );
    AbstractMDXDataFactory.MDXCompiler compiler = new AbstractMDXDataFactory.MDXCompiler( dataRow, Locale.US );
    final String tQuery = compiler.translateAndLookup( "SELECT ${test,string} AS ${testN,integer}, ${test}" );
    assertEquals( "SELECT \"tes{[t\" AS 100, tes{[t", tQuery );
  }
}
