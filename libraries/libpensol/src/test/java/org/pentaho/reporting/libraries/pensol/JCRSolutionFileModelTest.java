/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.pensol;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileTreeDto;
import org.pentaho.reporting.libraries.base.util.URLEncoder;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pentaho.reporting.libraries.pensol.JCRSolutionFileModel.encodePathForRequest;

/**
 * @author Andrey Khayrutdinov
 */
public class JCRSolutionFileModelTest {

  @Test
  public void encodePathForRequest_LettersAndDigits() {
    String encoded = encodePathForRequest( "/qwerty/123" );
    assertEquals( ":qwerty:123", encoded );
  }

  @Test
  public void encodePathForRequest_Spaces() {
    String encoded = encodePathForRequest( "/qwe rty/123" );
    assertEquals( ":qwe%20rty:123", encoded );
  }

  @Test
  public void encodePathForRequest_NonAsciiChars() {
    String encoded = encodePathForRequest( "/фыв апр" );
    String expected = ":" + URLEncoder.encodeUTF8( "фыв" ) + "%20" + URLEncoder.encodeUTF8( "апр" );
    assertEquals( expected, encoded );
  }


  @Test
  public void performsPartialLoading_WhenFlagIsSet() throws Exception {
    RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( new RepositoryFileDto() );

    Client client = mockClient( root );

    JCRSolutionFileModel model = new JCRSolutionFileModel( "", client, true );
    model.refresh();

    RepositoryFileTreeDto dto = model.getRoot();
    assertThat( dto, is( instanceOf( RepositoryFileTreeDtoProxy.class ) ) );

    RepositoryFileTreeDtoProxy proxy = (RepositoryFileTreeDtoProxy) dto;
    assertEquals( root, proxy.getRealObject() );
  }

  @Test
  public void performsFullLoading_WhenFlagIsCleared() throws Exception {
    RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( new RepositoryFileDto() );

    Client client = mockClient( root );

    JCRSolutionFileModel model = new JCRSolutionFileModel( "", client, false );
    model.refresh();

    RepositoryFileTreeDto dto = model.getRoot();
    assertThat( dto, is( instanceOf( RepositoryFileTreeDto.class ) ) );
  }

  private Client mockClient( RepositoryFileTreeDto root ) {
    WebResource.Builder builder = mock( WebResource.Builder.class );
    when( builder.get( RepositoryFileTreeDto.class ) ).thenReturn( root );

    WebResource resource = mock( WebResource.class );
    when( resource.path( anyString() ) ).thenReturn( resource );
    when( resource.accept( any( MediaType[].class ) ) ).thenReturn( builder );

    Client client = mock( Client.class );
    when( client.resource( anyString() ) ).thenReturn( resource );
    return client;
  }
}