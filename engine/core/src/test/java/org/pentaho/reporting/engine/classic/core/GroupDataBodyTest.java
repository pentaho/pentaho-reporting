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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;

public class GroupDataBodyTest {

  private GroupDataBody body;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    body = new GroupDataBody();
  }

  @Test
  public void testCreation() {
    assertThat( body.getElementType(), is( instanceOf( GroupDataBodyType.class ) ) );
    assertThat( body.getNoDataBand(), is( notNullValue() ) );
    assertThat( (GroupDataBody) body.getNoDataBand().getParentSection(), is( equalTo( body ) ) );
    assertThat( body.getItemBand(), is( notNullValue() ) );
    assertThat( (GroupDataBody) body.getItemBand().getParentSection(), is( equalTo( body ) ) );
    assertThat( body.getDetailsHeader(), is( notNullValue() ) );
    assertThat( (GroupDataBody) body.getDetailsHeader().getParentSection(), is( equalTo( body ) ) );
    assertThat( body.getDetailsFooter(), is( notNullValue() ) );
    assertThat( (GroupDataBody) body.getDetailsFooter().getParentSection(), is( equalTo( body ) ) );
  }

  @Test
  public void testGetGroup() {
    assertThat( body.getGroup(), is( nullValue() ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetNoDataBandException() {
    body.setNoDataBand( null );
  }

  @Test
  public void testSetNoDataBand() {
    NoDataBand noDataBand = mock( NoDataBand.class );
    body.setNoDataBand( noDataBand );
    assertThat( body.getNoDataBand(), is( equalTo( noDataBand ) ) );
    assertNull( (GroupDataBody) body.getNoDataBand().getParentSection() );

    NoDataBand band = new NoDataBand();
    band.setParent( body );
    body.setNoDataBand( band );
    assertThat( body.getNoDataBand(), is( equalTo( noDataBand ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetItemBandException() {
    body.setItemBand( null );
  }

  @Test
  public void testSetItemBand() {
    ItemBand itemBand = mock( ItemBand.class );
    body.setItemBand( itemBand );
    assertThat( body.getItemBand(), is( equalTo( itemBand ) ) );
    assertNull( (GroupDataBody) body.getItemBand().getParentSection() );

    ItemBand band = new ItemBand();
    band.setParent( body );
    body.setItemBand( band );
    assertThat( body.getItemBand(), is( equalTo( itemBand ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetDetailsHeaderException() {
    body.setDetailsHeader( null );
  }

  @Test
  public void testSetDetailsHeader() {
    DetailsHeader detailsHeader = mock( DetailsHeader.class );
    body.setDetailsHeader( detailsHeader );
    assertThat( body.getDetailsHeader(), is( equalTo( detailsHeader ) ) );
    assertNull( (GroupDataBody) body.getDetailsHeader().getParentSection() );

    DetailsHeader header = new DetailsHeader();
    header.setParent( body );
    body.setDetailsHeader( header );
    assertThat( body.getDetailsHeader(), is( equalTo( detailsHeader ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetDetailsFooterException() {
    body.setDetailsFooter( null );
  }

  @Test
  public void testSetDetailsFooter() {
    DetailsFooter detailsFooter = mock( DetailsFooter.class );
    body.setDetailsFooter( detailsFooter );
    assertThat( body.getDetailsFooter(), is( equalTo( detailsFooter ) ) );
    assertNull( (GroupDataBody) body.getDetailsFooter().getParentSection() );

    DetailsFooter footer = new DetailsFooter();
    footer.setParent( body );
    body.setDetailsFooter( footer );
    assertThat( body.getDetailsFooter(), is( equalTo( detailsFooter ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testRemoveElementException() {
    body.removeElement( null );
  }

  @Test
  public void testRemoveElement() {
    ItemBand itemBand = mock( ItemBand.class );
    NoDataBand band = mock( NoDataBand.class );
    DetailsHeader header = mock( DetailsHeader.class );
    DetailsFooter footer = mock( DetailsFooter.class );

    body.setItemBand( itemBand );
    body.setNoDataBand( band );
    body.setDetailsHeader( header );
    body.setDetailsFooter( footer );

    body.removeElement( itemBand );
    assertThat( itemBand.getParentSection(), is( nullValue() ) );
    assertThat( body.getItemBand(), is( not( equalTo( itemBand ) ) ) );
    assertThat( (GroupDataBody) body.getItemBand().getParentSection(), is( equalTo( body ) ) );

    body.removeElement( band );
    assertThat( band.getParentSection(), is( nullValue() ) );
    assertThat( body.getNoDataBand(), is( not( equalTo( band ) ) ) );
    assertThat( (GroupDataBody) body.getNoDataBand().getParentSection(), is( equalTo( body ) ) );

    body.removeElement( header );
    assertThat( header.getParentSection(), is( nullValue() ) );
    assertThat( body.getDetailsHeader(), is( not( equalTo( header ) ) ) );
    assertThat( (GroupDataBody) body.getDetailsHeader().getParentSection(), is( equalTo( body ) ) );

    body.removeElement( footer );
    assertThat( footer.getParentSection(), is( nullValue() ) );
    assertThat( body.getDetailsFooter(), is( not( equalTo( footer ) ) ) );
    assertThat( (GroupDataBody) body.getDetailsFooter().getParentSection(), is( equalTo( body ) ) );
  }

  @Test
  public void testGetElementCount() {
    assertThat( body.getElementCount(), is( equalTo( 4 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetElementException() {
    body.getElement( 4 );
  }

  @Test
  public void testGetElement() {
    assertThat( body.getElement( 0 ), is( instanceOf( DetailsHeader.class ) ) );
    assertThat( body.getElement( 1 ), is( instanceOf( ItemBand.class ) ) );
    assertThat( body.getElement( 2 ), is( instanceOf( NoDataBand.class ) ) );
    assertThat( body.getElement( 3 ), is( instanceOf( DetailsFooter.class ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testSetElementAtException() {
    body.setElementAt( 4, mock( Element.class ) );
  }

  @Test
  public void testSetElementAt() {
    ItemBand itemBand = mock( ItemBand.class );
    NoDataBand band = mock( NoDataBand.class );
    DetailsHeader header = mock( DetailsHeader.class );
    DetailsFooter footer = mock( DetailsFooter.class );

    body.setElementAt( 0, header );
    body.setElementAt( 1, itemBand );
    body.setElementAt( 2, band );
    body.setElementAt( 3, footer );

    assertThat( body.getDetailsHeader(), is( equalTo( header ) ) );
    assertThat( body.getItemBand(), is( equalTo( itemBand ) ) );
    assertThat( body.getNoDataBand(), is( equalTo( band ) ) );
    assertThat( body.getDetailsFooter(), is( equalTo( footer ) ) );
  }

  @Test
  public void testClone() {
    GroupDataBody result = body.clone();
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( not( equalTo( body ) ) ) );
  }
}
