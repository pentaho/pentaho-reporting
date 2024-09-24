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

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;

public class CrosstabOtherGroupTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreatingCrosstabOtherGroup() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    assertThat( crosstab.getElementType(), is( instanceOf( CrosstabOtherGroupType.class ) ) );
    assertThat( crosstab.getHeader(), is( notNullValue() ) );
    assertThat( crosstab.getFooter(), is( notNullValue() ) );
    assertThat( crosstab.getBody(), is( instanceOf( CrosstabRowGroupBody.class ) ) );

    CrosstabOtherGroupBody body = mock( CrosstabOtherGroupBody.class );
    crosstab = new CrosstabOtherGroup( body );
    assertThat( crosstab.getElementType(), is( instanceOf( CrosstabOtherGroupType.class ) ) );
    assertThat( crosstab.getHeader(), is( notNullValue() ) );
    assertThat( crosstab.getFooter(), is( notNullValue() ) );
    assertThat( crosstab.getBody(), is( instanceOf( CrosstabOtherGroupBody.class ) ) );
    assertThat( (CrosstabOtherGroupBody) crosstab.getBody(), is( equalTo( body ) ) );

    CrosstabCellBody cellBody = mock( CrosstabCellBody.class );
    crosstab = new CrosstabOtherGroup( cellBody );
    assertThat( crosstab.getElementType(), is( instanceOf( CrosstabOtherGroupType.class ) ) );
    assertThat( crosstab.getHeader(), is( notNullValue() ) );
    assertThat( crosstab.getFooter(), is( notNullValue() ) );
    assertThat( crosstab.getBody(), is( instanceOf( CrosstabCellBody.class ) ) );
    assertThat( (CrosstabCellBody) crosstab.getBody(), is( equalTo( cellBody ) ) );

    CrosstabColumnGroupBody columnBody = mock( CrosstabColumnGroupBody.class );
    crosstab = new CrosstabOtherGroup( columnBody );
    assertThat( crosstab.getElementType(), is( instanceOf( CrosstabOtherGroupType.class ) ) );
    assertThat( crosstab.getHeader(), is( notNullValue() ) );
    assertThat( crosstab.getFooter(), is( notNullValue() ) );
    assertThat( crosstab.getBody(), is( instanceOf( CrosstabColumnGroupBody.class ) ) );
    assertThat( (CrosstabColumnGroupBody) crosstab.getBody(), is( equalTo( columnBody ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetHeaderException() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.setHeader( null );
  }

  @Test
  public void testSetHeader() {
    GroupHeader header = new GroupHeader();
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    GroupHeader prevHeader = crosstab.getHeader();

    header.setParent( crosstab );
    crosstab.setHeader( header );
    assertThat( crosstab.getHeader(), is( equalTo( prevHeader ) ) );

    header.setParent( null );
    crosstab.setHeader( header );
    assertThat( crosstab.getHeader(), is( equalTo( header ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetFooterException() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.setFooter( null );
  }

  @Test
  public void testSetFooter() {
    GroupFooter footer = new GroupFooter();
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    GroupFooter prevFooter = crosstab.getFooter();

    footer.setParent( crosstab );
    crosstab.setFooter( footer );
    assertThat( crosstab.getFooter(), is( equalTo( prevFooter ) ) );

    footer.setParent( null );
    crosstab.setFooter( footer );
    assertThat( crosstab.getFooter(), is( equalTo( footer ) ) );
  }

  @Test
  public void testClone() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    CrosstabOtherGroup result = crosstab.clone();
    assertThat( result, is( not( equalTo( crosstab ) ) ) );
    assertThat( result.getHeader(), is( not( equalTo( crosstab.getHeader() ) ) ) );
    assertThat( result.getFooter(), is( not( equalTo( crosstab.getFooter() ) ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testRemoveElementException() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.removeElement( null );
  }

  @Test
  public void testRemoveElement() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    GroupHeader header = crosstab.getHeader();
    GroupFooter footer = crosstab.getFooter();

    crosstab.removeElement( mock( Element.class ) );
    assertThat( crosstab.getFooter(), is( equalTo( footer ) ) );
    assertThat( crosstab.getHeader(), is( equalTo( header ) ) );

    crosstab.removeElement( footer );
    assertThat( crosstab.getFooter(), is( not( equalTo( footer ) ) ) );
    assertThat( crosstab.getHeader(), is( equalTo( header ) ) );

    crosstab.removeElement( header );
    assertThat( crosstab.getFooter(), is( not( equalTo( footer ) ) ) );
    assertThat( crosstab.getHeader(), is( not( equalTo( header ) ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetElementException() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.getElement( 4 );
  }

  @Test
  public void testGetElement() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    Element header = crosstab.getElement( 0 );
    assertThat( header, is( instanceOf( GroupHeader.class ) ) );

    Element body = crosstab.getElement( 1 );
    assertThat( body, is( instanceOf( GroupBody.class ) ) );

    Element footer = crosstab.getElement( 2 );
    assertThat( footer, is( instanceOf( GroupFooter.class ) ) );
  }

  @Test
  public void testGetElementCount() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    assertThat( crosstab.getElementCount(), is( equalTo( 3 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testSetElementAtException() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.setElementAt( 4, mock( Element.class ) );
  }

  @Test
  public void testSetElementAt() {
    GroupHeader header = mock( GroupHeader.class );
    CrosstabRowGroupBody body = mock( CrosstabRowGroupBody.class );
    GroupFooter footer = mock( GroupFooter.class );

    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.setElementAt( 0, header );
    crosstab.setElementAt( 1, body );
    crosstab.setElementAt( 2, footer );

    assertThat( crosstab.getHeader(), is( equalTo( header ) ) );
    assertThat( (CrosstabRowGroupBody) crosstab.getBody(), is( equalTo( body ) ) );
    assertThat( crosstab.getFooter(), is( equalTo( footer ) ) );
  }

  @Test
  public void testGetField() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    String result = crosstab.getField();
    assertThat( result, is( nullValue() ) );

    crosstab.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, "field_value" );
    result = crosstab.getField();
    assertThat( result, is( equalTo( "field_value" ) ) );
  }

  @Test
  public void testSetField() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.setField( "field_value" );
    String result = crosstab.getField();
    result = (String) crosstab.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    assertThat( result, is( equalTo( "field_value" ) ) );
  }

  @Test
  public void testIsGroupChange() {
    DataRow dataRow = mock( DataRow.class );
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    boolean result = crosstab.isGroupChange( dataRow );
    assertThat( result, is( equalTo( false ) ) );

    crosstab.setField( "field_value" );
    doReturn( false ).when( dataRow ).isChanged( "field_value" );
    result = crosstab.isGroupChange( dataRow );
    assertThat( result, is( equalTo( false ) ) );

    doReturn( true ).when( dataRow ).isChanged( "field_value" );
    result = crosstab.isGroupChange( dataRow );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetSortingConstraint() {
    CrosstabOtherGroup crosstab = new CrosstabOtherGroup();
    crosstab.setField( "field_value" );
    List<SortConstraint> result = crosstab.getSortingConstraint();
    assertThat( result, is( notNullValue() ) );
    assertThat( result.size(), is( equalTo( 1 ) ) );
    assertThat( result.get( 0 ).getField(), is( equalTo( "field_value" ) ) );
  }
}
