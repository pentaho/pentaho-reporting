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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataRow;

public class SQLParameterLookupParserTest {

  private static final boolean IS_EXPAND_ARRAY = true;

  private SQLParameterLookupParser parser;

  @Before
  public void setUp() {
    parser = new SQLParameterLookupParser( IS_EXPAND_ARRAY );
  }

  @Test
  public void testCreating() {
    assertThat( parser.isExpandArray(), is( equalTo( IS_EXPAND_ARRAY ) ) );
    assertThat( parser.getFields(), is( emptyArray() ) );
    assertThat( parser.getMarkerChar(), is( equalTo( '$' ) ) );
    assertThat( parser.getOpeningBraceChar(), is( equalTo( '{' ) ) );
    assertThat( parser.getClosingBraceChar(), is( equalTo( '}' ) ) );
  }

  @Test
  public void testLookupVariable() {
    String result = parser.lookupVariable( "test_name" );
    assertThat( result, is( equalTo( "?" ) ) );
    assertThat( parser.getFields(), is( arrayContaining( "test_name" ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testHandleVariableLookupWithoutParams() {
    parser.handleVariableLookup( new StringBuilder(), null, "column_name" );
  }

  @Test
  public void testHandleVariableLookupStringParam() {
    DataRow parameters = mock( DataRow.class );
    doReturn( "tets_value" ).when( parameters ).get( "string_column_name" );
    StringBuilder result = new StringBuilder();
    parser.handleVariableLookup( result, parameters, "string_column_name" );
    assertThat( result.toString(), is( equalTo( "?" ) ) );
  }

  @Test
  public void testHandleVariableLookupArrayParam() {
    DataRow parameters = mock( DataRow.class );
    doReturn( new Object[] { "val_0", 10, "test_val" } ).when( parameters ).get( "column_name" );
    StringBuilder result = new StringBuilder();
    parser.handleVariableLookup( result, parameters, "column_name" );
    assertThat( result.toString(), is( equalTo( "?,?,?" ) ) );
  }
}
