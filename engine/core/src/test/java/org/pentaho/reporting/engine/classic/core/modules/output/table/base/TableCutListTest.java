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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TableCutListTest {

  @Test( expected = IllegalArgumentException.class )
  public void testCreationException() {
    new TableCutList( 0, true );
  }

  @Test( expected = NullPointerException.class )
  public void testPutException() {
    TableCutList list = new TableCutList( 5, true );
    list.put( 5000, null );
  }

  @Test
  public void testPlainAdd() {
    TableCutList list = new TableCutList( 10, true );
    assertThat( list.put( 5000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( 15000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -85000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -5000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -15000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -25000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -35000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -45000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -55000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -65000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( -75000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( 25000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( 35000, Boolean.TRUE ), is( equalTo( true ) ) );
    assertThat( list.put( 35000, Boolean.TRUE ), is( equalTo( false ) ) );

    assertThat( list.get( -95000 ), is( nullValue() ) );
    assertThat( list.get( 35000 ), is( equalTo( true ) ) );
  }

  @Test
  public void testClone() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    TableCutList result = list.clone();
    assertThat( result, is( not( equalTo( list ) ) ) );
    assertThat( result.isEnableQuickLookup(), is( equalTo( list.isEnableQuickLookup() ) ) );
    assertThat( result.size(), is( equalTo( 2 ) ) );
    assertThat( result.get( 10 ), is( equalTo( true ) ) );
    assertThat( result.get( 20 ), is( equalTo( true ) ) );
  }

  @Test
  public void testClear() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    list.clear();
    assertThat( list.size(), is( equalTo( 0 ) ) );
    assertThat( list.get( 10 ), is( equalTo( null ) ) );
    assertThat( list.get( 20 ), is( equalTo( null ) ) );
  }

  @Test
  public void testRemove() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    assertThat( list.remove( 10 ), is( equalTo( true ) ) );
    assertThat( list.get( 10 ), is( equalTo( null ) ) );
    assertThat( list.remove( 2 ), is( equalTo( false ) ) );
  }

  @Test
  public void testGetPrevious() {
    TableCutList list = new TableCutList( 2, true );
    assertThat( list.getPrevious( 10 ), is( nullValue() ) );
    list.put( 10, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    assertThat( list.getPrevious( 10 ), is( nullValue() ) );
    assertThat( list.getPrevious( 20 ), is( equalTo( true ) ) );
    assertThat( list.getPrevious( 30 ), is( equalTo( true ) ) );
  }

  @Test
  public void testContainsKey() {
    TableCutList list = new TableCutList( 2, true );
    assertThat( list.containsKey( 10 ), is( equalTo( false ) ) );
    list.put( 10, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    assertThat( list.containsKey( 10 ), is( equalTo( true ) ) );
    assertThat( list.containsKey( 30 ), is( equalTo( false ) ) );
  }

  @Test
  public void testFindKeyPosition() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    assertThat( list.findKeyPosition( 10, Boolean.TRUE ), is( equalTo( 0 ) ) );
    assertThat( list.findKeyPosition( 20, Boolean.TRUE ), is( equalTo( 1 ) ) );
    assertThat( list.findKeyPosition( 30, Boolean.TRUE ), is( equalTo( 1 ) ) );
    assertThat( list.findKeyPosition( 5, Boolean.TRUE ), is( equalTo( 0 ) ) );
    assertThat( list.findKeyPosition( 5, Boolean.FALSE ), is( equalTo( -1 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetKeyAtLargerSize() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.getKeyAt( 2 );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetKeyAtNegativePosition() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.getKeyAt( -1 );
  }

  @Test
  public void testGetKeyAt() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    assertThat( list.getKeyAt( 0 ), is( equalTo( 10l ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetValueAtLargerSize() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.getValueAt( 2 );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetValueAtNegativePosition() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.getValueAt( -1 );
  }

  @Test
  public void testGetValueAt() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    assertThat( list.getValueAt( 0 ), is( equalTo( true ) ) );
  }

  @Test
  public void testFindKey() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    assertThat( list.findKey( 10, Boolean.TRUE ), is( equalTo( 10l ) ) );
  }

  @Test
  public void testRemoveAll() {
    TableCutList list = new TableCutList( 2, true );
    list.put( 10, Boolean.TRUE );
    list.put( 15, Boolean.TRUE );
    list.put( 20, Boolean.TRUE );
    list.removeAll( new long[] { 10l }, 0 );
    assertThat( list.size(), is( equalTo( 3 ) ) );

    list.removeAll( new long[] { 10l, 20l }, 2 );
    assertThat( list.size(), is( equalTo( 1 ) ) );

    list.removeAll( new long[] { 15l }, 1 );
    assertThat( list.size(), is( equalTo( 0 ) ) );
  }
}
