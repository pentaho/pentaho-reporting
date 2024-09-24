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
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;

public class RelationalGroupTest {

  private RelationalGroup group;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    group = new RelationalGroup();
    assertThat( group.getElementType(), is( instanceOf( RelationalGroupType.class ) ) );
    assertThat( group.getHeader(), is( notNullValue() ) );
    assertThat( group.getFooter(), is( notNullValue() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetHeaderException() {
    group.setHeader( null );
  }

  @Test
  public void testSetHeader() {
    GroupHeader header = new GroupHeader();
    GroupHeader prevHeader = group.getHeader();

    header.setParent( group );
    group.setHeader( header );
    assertThat( group.getHeader(), is( equalTo( prevHeader ) ) );

    header.setParent( null );
    group.setHeader( header );
    assertThat( group.getHeader(), is( equalTo( header ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetFooterException() {
    group.setFooter( null );
  }

  @Test
  public void testSetFooter() {
    GroupFooter footer = new GroupFooter();
    GroupFooter prevFooter = group.getFooter();

    footer.setParent( group );
    group.setFooter( footer );
    assertThat( group.getFooter(), is( equalTo( prevFooter ) ) );

    footer.setParent( null );
    group.setFooter( footer );
    assertThat( group.getFooter(), is( equalTo( footer ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetFieldsException() {
    group.setFields( null );
  }

  @Test
  public void testSetFields() {
    List<String> fields = new ArrayList<String>();
    fields.add( "test_field" );
    group.setFields( fields );
    assertThat( (String[]) group.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS ),
        is( equalTo( new String[] { "test_field" } ) ) );
    assertThat( group.getFields(), is( equalTo( fields ) ) );
  }

  @Test
  public void testClearFields() {
    List<String> fields = new ArrayList<String>();
    fields.add( "test_field" );
    group.setFields( fields );
    group.clearFields();
    assertThat( (String[]) group.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS ),
        is( equalTo( new String[] { } ) ) );
  }

  @Test
  public void testGetElementCount() {
    assertThat( group.getElementCount(), is( equalTo( 3 ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetAddFieldException() {
    group.addField( null );
  }

  @Test
  public void testAddField() {
    group.addField( "test_field" );
    assertThat( (String[]) group.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS ),
        is( equalTo( new String[] { "test_field" } ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetFieldsArrayException() {
    group.setFieldsArray( null );
  }

  @Test
  public void testSetFieldsArray() {
    String[] fields = new String[] { "test_field" };
    group.setFieldsArray( fields );
    assertThat( (String[]) group.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS ),
        is( equalTo( new String[] { "test_field" } ) ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBodyException() {
    group.setBody( mock( GroupBody.class ) );
  }

  @Test
  public void testSetBody() {
    GroupDataBody body = mock( GroupDataBody.class );
    group.setBody( body );
    assertThat( group.getBody(), is( instanceOf( GroupDataBody.class ) ) );
    assertThat( (GroupDataBody) group.getBody(), is( equalTo( body ) ) );
  }

  @Test
  public void testEquals() {
    assertThat( group.equals( group ), is( equalTo( true ) ) );
    assertThat( group.equals( mock( GroupDataBody.class ) ), is( equalTo( false ) ) );

    RelationalGroup nextGroup = new RelationalGroup();
    assertThat( group.equals( nextGroup ), is( equalTo( true ) ) );
    nextGroup.addField( "test_field" );
    assertThat( group.equals( nextGroup ), is( equalTo( false ) ) );
  }

  @Test
  public void testIsGroupChange() {
    DataRow dataRow = mock( DataRow.class );
    group.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, "str" );
    assertThat( group.isGroupChange( dataRow ), is( equalTo( false ) ) );

    group.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, new String[] { } );
    assertThat( group.isGroupChange( dataRow ), is( equalTo( false ) ) );

    group.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, new String[] { "field_0",
        null, "field_1" } );
    doReturn( false ).when( dataRow ).isChanged( "field_0" );
    doReturn( true ).when( dataRow ).isChanged( "field_1" );
    assertThat( group.isGroupChange( dataRow ), is( equalTo( true ) ) );
  }

  @Test
  public void testClone() {
    RelationalGroup result = group.clone();
    assertThat( result, is( not( sameInstance( group ) ) ) );
    assertThat( result.getHeader(), is( not( sameInstance( group.getHeader() ) ) ) );
    assertThat( result.getFooter(), is( not( sameInstance( group.getFooter() ) ) ) );
    assertThat( (RelationalGroup) result.getHeader().getParentSection(), is( equalTo( result ) ) );
    assertThat( (RelationalGroup) result.getFooter().getParentSection(), is( equalTo( result ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testRemoveElementException() {
    group.removeElement( null );
  }

  @Test
  public void testRemoveElement() {
    GroupHeader header = group.getHeader();
    GroupFooter footer = group.getFooter();

    group.removeElement( mock( Element.class ) );
    assertThat( group.getFooter(), is( equalTo( footer ) ) );
    assertThat( group.getHeader(), is( equalTo( header ) ) );

    group.removeElement( footer );
    assertThat( group.getFooter(), is( not( equalTo( footer ) ) ) );
    assertThat( group.getHeader(), is( equalTo( header ) ) );

    group.removeElement( header );
    assertThat( group.getFooter(), is( not( equalTo( footer ) ) ) );
    assertThat( group.getHeader(), is( not( equalTo( header ) ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetElementException() {
    group.getElement( 3 );
  }

  @Test
  public void testGetElement() {
    Element header = group.getElement( 0 );
    assertThat( header, is( instanceOf( GroupHeader.class ) ) );

    Element body = group.getElement( 1 );
    assertThat( body, is( instanceOf( GroupBody.class ) ) );

    Element footer = group.getElement( 2 );
    assertThat( footer, is( instanceOf( GroupFooter.class ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testSetElementAtException() {
    group.setElementAt( 3, mock( Element.class ) );
  }

  @Test
  public void testSetElementAt() {
    GroupHeader header = mock( GroupHeader.class );
    SubGroupBody body = mock( SubGroupBody.class );
    GroupFooter footer = mock( GroupFooter.class );

    group.setElementAt( 0, header );
    group.setElementAt( 1, body );
    group.setElementAt( 2, footer );

    assertThat( group.getHeader(), is( equalTo( header ) ) );
    assertThat( (SubGroupBody) group.getBody(), is( equalTo( body ) ) );
    assertThat( group.getFooter(), is( equalTo( footer ) ) );
  }

  @Test
  public void testGetSortingConstraint() {
    group.addField( "field_value" );
    List<SortConstraint> result = group.getSortingConstraint();
    assertThat( result, is( notNullValue() ) );
    assertThat( result.size(), is( equalTo( 1 ) ) );
    assertThat( result.get( 0 ).getField(), is( equalTo( "field_value" ) ) );
  }
}
