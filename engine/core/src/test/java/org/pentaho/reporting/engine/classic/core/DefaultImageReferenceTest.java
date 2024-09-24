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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.awt.Image;
import java.io.IOException;

import org.junit.Test;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

public class DefaultImageReferenceTest {

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutParent() {
    DefaultImageReference parent = null;
    new DefaultImageReference( parent );
  }

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutImage() throws IOException {
    Image img = null;
    new DefaultImageReference( img );
  }

  @Test( expected = NullPointerException.class )
  public void testCreationWithoutImageResource() throws ResourceException {
    Resource imageResource = null;
    new DefaultImageReference( imageResource );
  }

  @Test
  public void testDefaultCreation() {
    DefaultImageReference parent = mock( DefaultImageReference.class );
    DefaultImageReference imageRef = new DefaultImageReference( parent );

    assertThat( imageRef.getImage(), is( nullValue() ) );
    assertThat( imageRef.getIdentity(), is( nullValue() ) );
    assertThat( imageRef.getResourceKey(), is( nullValue() ) );
    assertThat( imageRef.getImageHeight(), is( equalTo( 0 ) ) );
    assertThat( imageRef.getImageWidth(), is( equalTo( 0 ) ) );
    assertThat( imageRef.getName(), is( nullValue() ) );
    assertThat( imageRef.getScaleX(), is( equalTo( 1.0f ) ) );
    assertThat( imageRef.getScaleY(), is( equalTo( 1.0f ) ) );
    assertThat( imageRef.getSourceURL(), is( nullValue() ) );
    assertThat( imageRef.getSourceURLString(), is( nullValue() ) );
    assertThat( imageRef.isIdentifiable(), is( equalTo( false ) ) );
    assertThat( imageRef.isLoadable(), is( equalTo( false ) ) );
  }

  @Test( expected = ResourceException.class )
  public void testDefaultCreationWithImageResource() throws ResourceException {
    Resource imageResource = mock( Resource.class );
    doReturn( null ).when( imageResource ).getResource();
    new DefaultImageReference( imageResource );
  }

  @Test
  public void testCreationWithParams() {
    DefaultImageReference imageRef = new DefaultImageReference( 300, 500 );

    assertThat( imageRef.getImage(), is( nullValue() ) );
    assertThat( imageRef.getIdentity(), is( nullValue() ) );
    assertThat( imageRef.getResourceKey(), is( nullValue() ) );
    assertThat( imageRef.getImageHeight(), is( equalTo( 500 ) ) );
    assertThat( imageRef.getImageWidth(), is( equalTo( 300 ) ) );
    assertThat( imageRef.getName(), is( nullValue() ) );
    assertThat( imageRef.getScaleX(), is( equalTo( 1.0f ) ) );
    assertThat( imageRef.getScaleY(), is( equalTo( 1.0f ) ) );
    assertThat( imageRef.getSourceURL(), is( nullValue() ) );
    assertThat( imageRef.getSourceURLString(), is( nullValue() ) );
    assertThat( imageRef.isIdentifiable(), is( equalTo( false ) ) );
    assertThat( imageRef.isLoadable(), is( equalTo( false ) ) );

    imageRef.setScale( 2.0f, 1.5f );
    assertThat( imageRef.getScaleX(), is( equalTo( 2.0f ) ) );
    assertThat( imageRef.getScaleY(), is( equalTo( 1.5f ) ) );
  }

  @Test
  public void testEquals() {
    DefaultImageReference imageRef = new DefaultImageReference( 300, 500 );
    assertThat( imageRef.equals( null ), is( equalTo( false ) ) );
    assertThat( imageRef.equals( "str" ), is( equalTo( false ) ) );
    assertThat( imageRef.equals( new DefaultImageReference( 301, 501 ) ), is( equalTo( false ) ) );
    assertThat( imageRef.equals( new DefaultImageReference( 300, 501 ) ), is( equalTo( false ) ) );
    assertThat( imageRef.equals( new DefaultImageReference( 300, 500 ) ), is( equalTo( true ) ) );
    imageRef.setScale( 2.0f, 1.0f );
    assertThat( imageRef.equals( new DefaultImageReference( 300, 500 ) ), is( equalTo( false ) ) );
    imageRef.setScale( 1.0f, 1.5f );
    assertThat( imageRef.equals( new DefaultImageReference( 300, 500 ) ), is( equalTo( false ) ) );
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    DefaultImageReference imageRef = new DefaultImageReference( 300, 500 );
    Object result = imageRef.clone();
    assertThat( result, is( instanceOf( DefaultImageReference.class ) ) );
    assertThat( (DefaultImageReference) result, is( not( sameInstance( imageRef ) ) ) );
    assertThat( (DefaultImageReference) result, is( equalTo( imageRef ) ) );
  }
}
