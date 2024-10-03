///*
// * This program is free software; you can redistribute it and/or modify it under the
// *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
// *  Foundation.
// *
// *  You should have received a copy of the GNU Lesser General Public License along with this
// *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
// *  or from the Free Software Foundation, Inc.,
// *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
// *
// *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// *  See the GNU Lesser General Public License for more details.
// *
// *  Copyright (c) 2006 - 2024 Hitachi Vantara.  All rights reserved.
// */
//
//package org.pentaho.reporting.libraries.pensol;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.client.Invocation;
//import org.junit.Before;
//import org.junit.Test;
//import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
//import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
//
//import javax.ws.rs.core.MediaType;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.instanceOf;
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertThat;
//import static org.mockito.Mockito.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.any;
//import static org.mockito.ArgumentMatchers.eq;
//
///**
// * @author Andrey Khayrutdinov
// */
//public class RepositoryFileTreeDtoProxyTest {
//
//  private RepositoryFileTreeDto dto;
//  private Client client;
//  private RepositoryFileTreeDtoProxy proxy;
//
//  @Before
//  public void setUp() throws Exception {
//    dto = new RepositoryFileTreeDto();
//    RepositoryFileDto file = new RepositoryFileDto();
//    file.setFolder( true );
//    file.setPath( "/" );
//    dto.setFile( file );
//
//    client = mock( Client.class );
//
//    proxy = new RepositoryFileTreeDtoProxy( dto, client, "" );
//  }
//
//
//  @Test
//  public void ignoresNonFolderFile() throws Exception {
//    dto.getFile().setFolder( false );
//    dto.setChildren( null );
//    proxy = new RepositoryFileTreeDtoProxy( dto, client, "" );
//    assertNotNull( "Proxy should replace null with empty list to avoid useless request sending", proxy.getChildren() );
//  }
//
//  @Test
//  public void doesNotLoadChildren_WhenDtoHasNonNullList() throws Exception {
//    dto.setChildren( Collections.<RepositoryFileTreeDto>emptyList() );
//    proxy.getChildren();
//    verify( client, never() ).target( anyString() );
//  }
//
//  @Test
//  public void loadsChildren_WhenDtoHasNullList() throws Exception {
//    final String childFileName = "filename";
//    RepositoryFileTreeDto child = new RepositoryFileTreeDto();
//    child.setFile( new RepositoryFileDto() );
//    child.getFile().setName( childFileName );
//
//    RepositoryFileTreeDto returned = prepareReturnedDto( child );
//
//    WebTarget target = mockWebTarget( returned );
//    when( client.target( anyString() ) ).thenReturn( target );
//
//    List<RepositoryFileTreeDto> children = proxy.getChildren();
//    assertEquals( "Should have exactly 1 child", 1, children.size() );
//    assertEquals( childFileName, children.get( 0 ).getFile().getName() );
//  }
//
//  @Test
//  public void replacesNullWithEmptyList_WhenResponseHasNoChildren() throws Exception {
//    RepositoryFileTreeDto returned = prepareReturnedDto( null );
//
//    WebTarget target = mockWebTarget( returned );
//    when( client.target( anyString() ) ).thenReturn( target );
//
//    List<RepositoryFileTreeDto> children = proxy.getChildren();
//    assertTrue( children.isEmpty() );
//  }
//
//  @Test
//  public void proxiesChildren() throws Exception {
//    RepositoryFileTreeDto child = new RepositoryFileTreeDto();
//    child.setFile( new RepositoryFileDto() );
//
//    RepositoryFileTreeDto returned = prepareReturnedDto( child );
//
//    WebTarget target = mockWebTarget( returned );
//    when( client.target( anyString() ) ).thenReturn( target );
//
//    List<RepositoryFileTreeDto> children = proxy.getChildren();
//    assertEquals( "Should have exactly 1 child", 1, children.size() );
//    assertThat( "Returned children should be also proxied",
//      children.get( 0 ), is( instanceOf( RepositoryFileTreeDtoProxy.class ) ) );
//  }
//
//
//  private RepositoryFileTreeDto prepareReturnedDto( RepositoryFileTreeDto... children ) {
//    RepositoryFileTreeDto returned = new RepositoryFileTreeDto();
//    returned.setFile( dto.getFile() );
//    returned.setChildren( ( children == null ) ? null : Arrays.asList( children ) );
//    return returned;
//  }
//
//  private WebTarget mockWebTarget( RepositoryFileTreeDto returned ) {
//    Invocation.Builder builder = mock( Invocation.Builder.class );
//    when( builder.get( eq(RepositoryFileTreeDto.class) ) ).thenReturn( returned );
//
//    WebTarget target = mock( WebTarget.class );
//    when( target.path( anyString() ) ).thenReturn( target );
//    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );
//    return target;
//  }
//}