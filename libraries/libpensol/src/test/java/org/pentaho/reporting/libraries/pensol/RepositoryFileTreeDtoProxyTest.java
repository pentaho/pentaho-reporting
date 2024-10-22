/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.pensol;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;

import jakarta.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author Andrey Khayrutdinov
 */
public class RepositoryFileTreeDtoProxyTest {

  private RepositoryFileTreeDto dto;
  private Client client;
  private RepositoryFileTreeDtoProxy proxy;

  @Before
  public void setUp() throws Exception {
    dto = new RepositoryFileTreeDto();
    RepositoryFileDto file = new RepositoryFileDto();
    file.setFolder( true );
    file.setPath( "/" );
    dto.setFile( file );

    client = mock( Client.class );

    proxy = new RepositoryFileTreeDtoProxy( dto, client, "" );
  }


  @Test
  public void ignoresNonFolderFile() throws Exception {
    dto.getFile().setFolder( false );
    dto.setChildren( null );
    proxy = new RepositoryFileTreeDtoProxy( dto, client, "" );
    assertNotNull( "Proxy should replace null with empty list to avoid useless request sending", proxy.getChildren() );
  }

  @Test
  public void doesNotLoadChildren_WhenDtoHasNonNullList() throws Exception {
    dto.setChildren( Collections.<RepositoryFileTreeDto>emptyList() );
    proxy.getChildren();
    verify( client, never() ).target( anyString() );
  }

  @Test
  public void loadsChildren_WhenDtoHasNullList() throws Exception {
    final String childFileName = "filename";
    RepositoryFileTreeDto child = new RepositoryFileTreeDto();
    child.setFile( new RepositoryFileDto() );
    child.getFile().setName( childFileName );

    RepositoryFileTreeDto returned = prepareReturnedDto( child );

    WebTarget target = mockWebTarget( returned );
    when( client.target( anyString() ) ).thenReturn( target );

    List<RepositoryFileTreeDto> children = proxy.getChildren();
    assertEquals( "Should have exactly 1 child", 1, children.size() );
    assertEquals( childFileName, children.get( 0 ).getFile().getName() );
  }

  @Test
  public void replacesNullWithEmptyList_WhenResponseHasNoChildren() throws Exception {
    RepositoryFileTreeDto returned = prepareReturnedDto( null );

    WebTarget target = mockWebTarget( returned );
    when( client.target( anyString() ) ).thenReturn( target );

    List<RepositoryFileTreeDto> children = proxy.getChildren();
    assertTrue( children.isEmpty() );
  }

  @Test
  public void proxiesChildren() throws Exception {
    RepositoryFileTreeDto child = new RepositoryFileTreeDto();
    child.setFile( new RepositoryFileDto() );

    RepositoryFileTreeDto returned = prepareReturnedDto( child );

    WebTarget target = mockWebTarget( returned );
    when( client.target( anyString() ) ).thenReturn( target );

    List<RepositoryFileTreeDto> children = proxy.getChildren();
    assertEquals( "Should have exactly 1 child", 1, children.size() );
    assertThat( "Returned children should be also proxied",
      children.get( 0 ), is( instanceOf( RepositoryFileTreeDtoProxy.class ) ) );
  }


  private RepositoryFileTreeDto prepareReturnedDto( RepositoryFileTreeDto... children ) {
    RepositoryFileTreeDto returned = new RepositoryFileTreeDto();
    returned.setFile( dto.getFile() );
    returned.setChildren( ( children == null ) ? null : Arrays.asList( children ) );
    return returned;
  }

  private WebTarget mockWebTarget( RepositoryFileTreeDto returned ) {
    Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.get( eq(RepositoryFileTreeDto.class) ) ).thenReturn( returned );

    WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );
    return target;
  }
}