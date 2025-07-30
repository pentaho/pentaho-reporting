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
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import org.junit.Test;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
import org.pentaho.reporting.libraries.base.util.URLEncoder;

import jakarta.ws.rs.core.MediaType;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
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
    Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.get( eq(RepositoryFileTreeDto.class) ) ).thenReturn( root );

    WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request( any( MediaType.class) ) ).thenReturn( builder );

    Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );
    return client;
  }
}