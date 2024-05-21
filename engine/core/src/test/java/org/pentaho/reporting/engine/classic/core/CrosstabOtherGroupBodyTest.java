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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupBodyType;

public class CrosstabOtherGroupBodyTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreationHeader() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    assertThat( body.getElementType(), is( instanceOf( CrosstabOtherGroupBodyType.class ) ) );
    assertThat( body.getGroup(), is( instanceOf( CrosstabOtherGroup.class ) ) );
    assertThat( (CrosstabOtherGroupBody) body.getGroup().getParentSection(), is( equalTo( body ) ) );

    CrosstabOtherGroup group = mock( CrosstabOtherGroup.class );
    body = new CrosstabOtherGroupBody( group );
    assertThat( body.getElementType(), is( instanceOf( CrosstabOtherGroupBodyType.class ) ) );
    assertThat( body.getGroup(), is( equalTo( group ) ) );
    assertNull( (CrosstabOtherGroupBody) body.getGroup().getParentSection() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetGroupException() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.setGroup( null );
  }

  @Test
  public void testSetGroup() {
    CrosstabOtherGroup group = mock( CrosstabOtherGroup.class );
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.setGroup( group );
    assertThat( body.getGroup(), is( equalTo( group ) ) );
    assertNull( (CrosstabOtherGroupBody) body.getGroup().getParentSection() );

    CrosstabOtherGroup nextGroup = new CrosstabOtherGroup();
    nextGroup.setParent( body );
    body.setGroup( nextGroup );
    assertThat( body.getGroup(), is( equalTo( group ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testRemoveElementException() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.removeElement( null );
  }

  @Test
  public void testRemoveElement() {
    CrosstabOtherGroup group = mock( CrosstabOtherGroup.class );
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.setGroup( group );

    body.removeElement( group );
    assertThat( group.getParentSection(), is( nullValue() ) );
    assertThat( body.getGroup(), is( not( equalTo( group ) ) ) );
    assertThat( (CrosstabOtherGroupBody) body.getGroup().getParentSection(), is( equalTo( body ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testSetElementAtException() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.setElementAt( 1, mock( Element.class ) );
  }

  @Test
  public void testSetElementAt() {
    CrosstabOtherGroup group = mock( CrosstabOtherGroup.class );
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.setElementAt( 0, group );
    assertThat( body.getGroup(), is( equalTo( group ) ) );
  }

  @Test
  public void testGetElementCount() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    assertThat( body.getElementCount(), is( equalTo( 1 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetElementException() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    body.getElement( 1 );
  }

  @Test
  public void testGetElement() {
    CrosstabOtherGroup group = mock( CrosstabOtherGroup.class );
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody( group );
    assertThat( (CrosstabOtherGroup) body.getElement( 0 ), is( equalTo( group ) ) );
  }

  @Test
  public void testClone() {
    CrosstabOtherGroupBody body = new CrosstabOtherGroupBody();
    CrosstabOtherGroupBody result = body.clone();
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( not( equalTo( body ) ) ) );
    assertThat( result.getGroup(), is( not( equalTo( body.getGroup() ) ) ) );
  }
}
