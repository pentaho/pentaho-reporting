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

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class StaticDataRowTest {

  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutStaticDataRowParam() {
    StaticDataRow dataRow = null;
    new StaticDataRow( dataRow );
  }

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutDataRowParam() {
    DataRow dataRow = null;
    new StaticDataRow( dataRow );
  }

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutNames() {
    new StaticDataRow( null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutValues() {
    new StaticDataRow( new String[]{ }, null );
  }

  @Test
  public void testCreation() {
    StaticDataRow dataRow = new StaticDataRow();
    assertThat( dataRow.getColumnNames(), is( equalTo( new String[]{ } ) ) );

    StaticDataRow staticDataRowParam = mock( StaticDataRow.class );
    dataRow = new StaticDataRow( staticDataRowParam );
    assertThat( dataRow.getColumnNames(), is( equalTo( new String[]{ } ) ) );

    DataRow dataRowParam = mock( DataRow.class );
    doReturn( new String[]{ "test_name" } ).when( dataRowParam ).getColumnNames();
    doReturn( "test_val" ).when( dataRowParam ).get( "test_name" );
    dataRow = new StaticDataRow( dataRowParam );
    assertThat( dataRow.getColumnNames(), is( equalTo( new String[]{ "test_name" } ) ) );
    assertThat( (String) dataRow.get( "test_name" ), is( equalTo( "test_val" ) ) );

    String[] names = new String[]{ "name_0", "name_1" };
    Object[] values = new Object[]{ "value_0" };
    dataRow = new StaticDataRow( names, values );
    assertThat( dataRow.getColumnNames(), is( equalTo( new String[]{ "name_0" } ) ) );
    assertThat( (String) dataRow.get( "name_0" ), is( equalTo( "value_0" ) ) );

    Map<String, Object> parameterValues = new HashMap<String, Object>();
    parameterValues.put( "name_0", "value_0" );
    dataRow = new StaticDataRow( parameterValues );
    assertThat( dataRow.getColumnNames(), is( equalTo( new String[]{ "name_0" } ) ) );
    assertThat( (String) dataRow.get( "name_0" ), is( equalTo( "value_0" ) ) );
  }

  @Test
  public void testUpdateData() {
    String[] names = new String[]{ "name_0" };
    Object[] values = new Object[]{ "value_0" };
    StaticDataRow dataRow = new StaticDataRow( names, values );
    Object[] newValues = new Object[]{ "new_value_0" };
    dataRow.updateData( newValues );
    assertThat( dataRow.getColumnNames(), is( equalTo( new String[]{ "name_0" } ) ) );
    assertThat( (String) dataRow.get( "name_0" ), is( equalTo( "new_value_0" ) ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testUpdateDataException() {
    String[] names = new String[]{ "name_0" };
    Object[] values = new Object[]{ "value_0" };
    StaticDataRow dataRow = new StaticDataRow( names, values );
    Object[] newValues = new Object[]{ "new_value_0", "new_value_1" };
    dataRow.updateData( newValues );
  }

  @Test
  public void testIsChanged() {
    StaticDataRow dataRow = new StaticDataRow();
    assertThat( dataRow.isChanged( "test" ), is( equalTo( false ) ) );
  }

  @Test
  public void testEquals() {
    StaticDataRow dataRow = new StaticDataRow();
    assertThat( dataRow.equals( dataRow ), is( equalTo( true ) ) );
    assertThat( dataRow.equals( "incorrect" ), is( equalTo( false ) ) );

    String[] names = new String[]{ "name_0" };
    Object[] values = new Object[]{ "value_0" };
    StaticDataRow newDataRow = new StaticDataRow( names, values );
    assertThat( dataRow.equals( newDataRow ), is( equalTo( false ) ) );

    dataRow.setData( names, new Object[]{ "test_val" } );
    assertThat( dataRow.equals( newDataRow ), is( equalTo( false ) ) );

    dataRow.setData( names, values );
    assertThat( dataRow.equals( newDataRow ), is( equalTo( true ) ) );
  }
}
