/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.pensol;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileAdapter;
import org.pentaho.reporting.libraries.base.util.URLEncoder;

import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.pentaho.reporting.libraries.pensol.JCRSolutionFileModel.encodePathForRequest;

/**
 * @author Andrey Khayrutdinov
 */
public class JCRSolutionFileModelTest {

  private static final String TEST_SERVER_URL = "http://localhost:8080/pentaho";

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

  @Test
  public void performsPartialLoadingPrePopulatesChildren() throws Exception {
    RepositoryFileTreeDto child = new RepositoryFileTreeDto();
    child.setFile( new RepositoryFileDto() );
    RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( new RepositoryFileDto() );
    root.setChildren( new ArrayList<>( List.of( child ) ) );

    Client client = mockClient( root );

    JCRSolutionFileModel model = new JCRSolutionFileModel( "", client, true );
    model.refresh();

    RepositoryFileTreeDtoProxy proxy = (RepositoryFileTreeDtoProxy) model.getRoot();
    assertEquals( 1, proxy.getChildren().size() );
  }

  @Test( expected = NullPointerException.class )
  public void testConstructorNullUrlThrows() {
    new JCRSolutionFileModel( null, "user", "pass", 5000, null );
  }

  @Test
  public void testConstructorEmptySessionIdUsesBasicAuth() {
    // sessionId is non-null but empty → short-circuits to the basic-auth branch
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( TEST_SERVER_URL, "user", "pass", 5000, "" );
    assertThat( model, is( instanceOf( JCRSolutionFileModel.class ) ) );
  }

  @Test
  public void testConstructor5ArgWithBasicAuthCreatesModelSuccessfully() {
    // Passes null sessionId → falls through to basic-auth branch
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( TEST_SERVER_URL, "user", "pass", 5000, null );
    assertThat( model, is( instanceOf( JCRSolutionFileModel.class ) ) );
  }

  @Test
  public void testConstructor5ArgWithSessionIdCreatesModelSuccessfully() {
    // Non-empty sessionId → cookie-based auth branch
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( TEST_SERVER_URL, "user", "", 5000, "my-session-123" );
    assertThat( model, is( instanceOf( JCRSolutionFileModel.class ) ) );
  }

  @Test
  public void testSessionIdAuthFilterAddsJSessionIdCookieHeader() throws Exception {
    // Build a session-based model, then invoke the registered request filter directly so the
    // cookie-injecting lambda runs and sets the JSESSIONID cookie header.
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( TEST_SERVER_URL, "user", "", 5000, "SID-123" );
    final java.lang.reflect.Field f = JCRSolutionFileModel.class.getDeclaredField( "client" );
    f.setAccessible( true );
    final Client client = (Client) f.get( model );

    jakarta.ws.rs.client.ClientRequestFilter filter = null;
    for ( final Object inst : client.getConfiguration().getInstances() ) {
      if ( inst instanceof jakarta.ws.rs.client.ClientRequestFilter ) {
        filter = (jakarta.ws.rs.client.ClientRequestFilter) inst;
        break;
      }
    }
    assertNotNull( filter );

    final jakarta.ws.rs.client.ClientRequestContext ctx =
      mock( jakarta.ws.rs.client.ClientRequestContext.class );
    final jakarta.ws.rs.core.MultivaluedMap<String, Object> headers =
      new jakarta.ws.rs.core.MultivaluedHashMap<>();
    when( ctx.getHeaders() ).thenReturn( headers );

    filter.filter( ctx );

    assertEquals( "JSESSIONID=SID-123", headers.getFirst( "Cookie" ) );
  }

  @Test
  public void testConstructor4ArgDelegatesTo5ArgWithNullSessionId() {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( TEST_SERVER_URL, "user", "pass", 5000 );
    assertThat( model, is( instanceOf( JCRSolutionFileModel.class ) ) );
  }

  private Client mockClient( RepositoryFileTreeDto root ) {
    Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.get( RepositoryFileTreeDto.class ) ).thenReturn( root );

    WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );

    Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );
    return client;
  }

  private static RepositoryFileDto dto( String name, boolean folder, boolean hidden ) {
    final RepositoryFileDto f = new RepositoryFileDto();
    f.setName( name );
    f.setFolder( folder );
    f.setHidden( hidden );
    f.setLastModifiedDate( "" );
    return f;
  }

  private static RepositoryFileTreeDto node( RepositoryFileDto file, List<RepositoryFileTreeDto> children ) {
    final RepositoryFileTreeDto t = new RepositoryFileTreeDto();
    t.setFile( file );
    t.setChildren( children );
    return t;
  }

  private static FileName rootName() {
    final FileName fn = mock( FileName.class );
    when( fn.getBaseName() ).thenReturn( "" );
    when( fn.getParent() ).thenReturn( null );
    when( fn.getPath() ).thenReturn( "/" );
    return fn;
  }

  private static FileName childName( String base, String path ) {
    final FileName parent = rootName();
    final FileName fn = mock( FileName.class );
    when( fn.getBaseName() ).thenReturn( base );
    when( fn.getParent() ).thenReturn( parent );
    when( fn.getPath() ).thenReturn( path );
    return fn;
  }

  /**
   * A model rooted at a folder that contains a single file "report.prpt".
   */
  private JCRSolutionFileModel modelWithReport() {
    final RepositoryFileDto rootFile = dto( "", true, false );
    final RepositoryFileDto childFile = dto( "report.prpt", false, false );
    childFile.setDescription( "the description" );
    childFile.setTitle( "Localized Title" );
    childFile.setFileSize( 4242L );
    childFile.setPath( "/report.prpt" );
    childFile.setId( "id-1" );
    final RepositoryFileTreeDto child = node( childFile, null );
    final RepositoryFileTreeDto root = node( rootFile, new ArrayList<>( List.of( child ) ) );

    final JCRSolutionFileModel model = new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    return model;
  }

  private static FileName reportName() {
    return childName( "report.prpt", "/report.prpt" );
  }

  @Test( expected = NullPointerException.class )
  public void setRootNullThrows() {
    new JCRSolutionFileModel( "u", mock( Client.class ), false ).setRoot( null );
  }

  @Test
  public void getRootReturnsAlreadyLoadedRoot() throws Exception {
    final JCRSolutionFileModel model = modelWithReport();
    assertNotNull( model.getRoot() );
  }

  @Test
  public void getRootDoesNotRefreshWhenAlreadySet() throws Exception {

    RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( new RepositoryFileDto() );

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false );

    model.setRoot( root );

    assertSame( root, model.getRoot() );
  }

  @Test
  public void lookupNodeReturnsRootForEmptyPathElement() throws Exception {

    RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( new RepositoryFileDto() );

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false );

    model.setRoot( root );

    RepositoryFileTreeDto result =
      model.lookupNode( new String[] { "" } );

    assertSame( root, result );
  }

  @Test
  public void lookupNodeEmptyFirstSegmentButMorePathDoesNotReturnRootShortcut() throws Exception {
    final RepositoryFileDto rootDto = new RepositoryFileDto();
    rootDto.setFolder( true );
    final RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( rootDto );
    root.setChildren( new ArrayList<>() );

    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );

    final RepositoryFileTreeDto result = model.lookupNode( new String[] { "", "child" } );
    assertNull( result );
  }

  @Test
  public void lookupNodeSingleNonEmptyPathDoesNotUseEmptyRootShortcut() throws Exception {
    final RepositoryFileDto rootDto = new RepositoryFileDto();
    rootDto.setFolder( true );
    final RepositoryFileTreeDto root = new RepositoryFileTreeDto();
    root.setFile( rootDto );
    root.setChildren( new ArrayList<>() );

    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );

    final RepositoryFileTreeDto result = model.lookupNode( new String[] { "child" } );
    assertNull( result );
  }

  @Test
  public void computeFileNamesUnsupportedEncodingFallback() {

    FileName file = mock( FileName.class );

    when( file.getBaseName() )
      .thenReturn( "report.prpt" );

    when( file.getParent() )
      .thenReturn( null );

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false );

    try ( MockedStatic<URLDecoder> decoder =
            mockStatic( URLDecoder.class ) ) {

      decoder.when(
          () -> URLDecoder.decode(
            anyString(),
            eq( "UTF-8" ) ) )
        .thenThrow(
          new UnsupportedEncodingException() );

      String[] result =
        model.computeFileNames( file );

      assertArrayEquals(
        new String[] { "report.prpt" },
        result );
    }
  }

  @Test
  public void existsThrowsWhenChildFileIsNull() {

    RepositoryFileTreeDto badChild =
      new RepositoryFileTreeDto();

    badChild.setFile( null );

    RepositoryFileTreeDto root =
      new RepositoryFileTreeDto();

    RepositoryFileDto rootDto =
      new RepositoryFileDto();

    rootDto.setFolder( true );

    root.setFile( rootDto );
    root.setChildren(
      List.of( badChild ) );

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false );

    model.setRoot( root );

    try {
      model.exists(
        childName(
          "abc",
          "/abc" ) );

      fail();
    } catch ( FileSystemException ex ) {

      assertTrue(
        ex.getMessage().contains(
          "BI-Server returned" ) );
    }
  }

  @Test
  public void getRootRefreshesWhenNull() throws Exception {
    final RepositoryFileTreeDto root = node( dto( "", true, false ), null );
    final JCRSolutionFileModel model = new JCRSolutionFileModel( "", mockClient( root ), false );
    assertEquals( root, model.getRoot() );
  }

  @Test
  public void refreshTimeAndDescriptionEntriesAccessors() {
    final JCRSolutionFileModel model = modelWithReport();
    model.setRefreshTime( 12345L );
    assertEquals( 12345L, model.getRefreshTime() );
    assertNotNull( model.getDescriptionEntries() );
  }

  @Test
  public void versionGettersReturnDefaults() {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( TEST_SERVER_URL, "u", "p", 1000, null );
    assertEquals( "999", model.getMajorVersion() );
    assertEquals( "999", model.getMinorVersion() );
    assertEquals( "999", model.getReleaseVersion() );
    assertEquals( "999", model.getBuildVersion() );
    assertEquals( "999", model.getMilestoneVersion() );
  }

  @Test
  public void isDirectoryReturnsFalseForFile() throws Exception {
    assertFalse( modelWithReport().isDirectory( reportName() ) );
  }

  @Test
  public void isDirectoryReturnsTrueForFolder() throws Exception {
    final JCRSolutionFileModel model = modelWithReport();
    assertTrue( model.isDirectory( rootName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void isDirectoryFileNotFoundThrows() throws Exception {
    modelWithReport().isDirectory( childName( "missing", "/missing" ) );
  }

  @Test( expected = FileSystemException.class )
  public void isDirectoryNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.isDirectory( rootName() );
  }

  @Test
  public void existsReturnsTrueAndFalse() throws Exception {
    final JCRSolutionFileModel model = modelWithReport();
    assertTrue( model.exists( reportName() ) );
    assertFalse( model.exists( childName( "nope", "/nope" ) ) );
  }

  @Test
  public void isVisibleReturnsTrueForVisibleFile() throws Exception {
    assertTrue( modelWithReport().isVisible( reportName() ) );
  }

  @Test
  public void isVisibleReturnsFalseForHiddenFile() throws Exception {
    final RepositoryFileDto rootFile = dto( "", true, false );
    final RepositoryFileDto childFile = dto( "report.prpt", false, true );
    final RepositoryFileTreeDto root =
      node( rootFile, new ArrayList<>( List.of( node( childFile, null ) ) ) );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    assertFalse( model.isVisible( reportName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void isVisibleFileNotFoundThrows() throws Exception {
    modelWithReport().isVisible( childName( "missing", "/missing" ) );
  }

  @Test( expected = FileSystemException.class )
  public void isVisibleNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.isVisible( rootName() );
  }

  @Test
  public void getAndSetDescription() throws Exception {
    final JCRSolutionFileModel model = modelWithReport();
    assertEquals( "the description", model.getDescription( reportName() ) );
    model.setDescription( reportName(), "new desc" );
    assertEquals( "new desc", model.getDescription( reportName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void getDescriptionNotFoundThrows() throws Exception {
    modelWithReport().getDescription( childName( "missing", "/missing" ) );
  }

  @Test( expected = FileSystemException.class )
  public void setDescriptionNotFoundThrows() throws Exception {
    modelWithReport().setDescription( childName( "missing", "/missing" ), "x" );
  }

  @Test( expected = FileSystemException.class )
  public void getDescriptionNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.getDescription( rootName() );
  }

  @Test( expected = FileSystemException.class )
  public void setDescriptionNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.setDescription( rootName(), "x" );
  }

  @Test
  public void getLocalizedNameReturnsTitle() throws Exception {
    assertEquals( "Localized Title", modelWithReport().getLocalizedName( reportName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void getLocalizedNameNotFoundThrows() throws Exception {
    modelWithReport().getLocalizedName( childName( "missing", "/missing" ) );
  }

  @Test( expected = FileSystemException.class )
  public void getLocalizedNameNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.getLocalizedName( rootName() );
  }

  @Test
  public void getContentSizeReturnsFileSize() throws Exception {
    assertEquals( 4242L, modelWithReport().getContentSize( reportName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void getContentSizeNotFoundThrows() throws Exception {
    modelWithReport().getContentSize( childName( "missing", "/missing" ) );
  }

  @Test( expected = FileSystemException.class )
  public void getContentSizeNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.getContentSize( rootName() );
  }

  @Test
  public void getLastModifiedDateEmptyReturnsMinusOne() throws Exception {
    assertEquals( -1L, modelWithReport().getLastModifiedDate( reportName() ) );
  }

  @Test
  public void getLastModifiedDateValidReturnsTime() throws Exception {
    final RepositoryFileDto childFile = dto( "report.prpt", false, false );
    childFile.setLastModifiedDate( "1700000000000" );
    final RepositoryFileTreeDto root =
      node( dto( "", true, false ), new ArrayList<>( List.of( node( childFile, null ) ) ) );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    try ( MockedStatic<RepositoryFileAdapter> rfa = mockStatic( RepositoryFileAdapter.class ) ) {
      rfa.when( () -> RepositoryFileAdapter.unmarshalDate( anyString() ) ).thenReturn( new Date( 5000L ) );
      assertEquals( 5000L, model.getLastModifiedDate( reportName() ) );
    }
  }

  @Test
  public void getLastModifiedDateUnparseableReturnsMinusOne() throws Exception {
    final RepositoryFileDto childFile = dto( "report.prpt", false, false );
    childFile.setLastModifiedDate( "garbage" );
    final RepositoryFileTreeDto root =
      node( dto( "", true, false ), new ArrayList<>( List.of( node( childFile, null ) ) ) );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    try ( MockedStatic<RepositoryFileAdapter> rfa = mockStatic( RepositoryFileAdapter.class ) ) {
      rfa.when( () -> RepositoryFileAdapter.unmarshalDate( anyString() ) ).thenReturn( null );
      assertEquals( -1L, model.getLastModifiedDate( reportName() ) );
    }
  }

  @Test( expected = FileSystemException.class )
  public void getLastModifiedDateNotFoundThrows() throws Exception {
    modelWithReport().getLastModifiedDate( childName( "missing", "/missing" ) );
  }

  @Test( expected = FileSystemException.class )
  public void getLastModifiedDateNullDtoThrows() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.getLastModifiedDate( rootName() );
  }

  @Test
  public void getChildrenReturnsChildList() throws Exception {
    final List<RepositoryFileTreeDto> children = modelWithReport().getChildren( rootName() );
    assertEquals( 1, children.size() );
  }

  @Test
  public void getChildrenNullChildrenReturnsEmpty() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( dto( "", true, false ), null ) );
    assertTrue( model.getChildren( rootName() ).isEmpty() );
  }

  @Test( expected = FileSystemException.class )
  public void getChildrenNotFoundThrows() throws Exception {
    modelWithReport().getChildren( childName( "missing", "/missing" ) );
  }

  @Test
  public void getChildsReturnsEncodedNames() throws Exception {
    final String[] names = modelWithReport().getChilds( rootName() );
    assertArrayEquals( new String[] { "report.prpt" }, names );
  }

  @Test
  public void getChildsSkipsNullElement() throws Exception {
    final List<RepositoryFileTreeDto> children = new ArrayList<>();
    children.add( null );
    children.add( node( dto( "a.prpt", false, false ), null ) );
    final RepositoryFileTreeDto root = node( dto( "", true, false ), children );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    final String[] names = model.getChilds( rootName() );
    assertEquals( 2, names.length );
    assertNull( names[ 0 ] );
    assertEquals( "a.prpt", names[ 1 ] );
  }

  @Test( expected = FileSystemException.class )
  public void getChildsNullDtoThrows() throws Exception {
    final List<RepositoryFileTreeDto> children = new ArrayList<>();
    children.add( node( null, null ) );
    final RepositoryFileTreeDto root = node( dto( "", true, false ), children );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    model.getChilds( rootName() );
  }

  @Test( expected = FileSystemException.class )
  public void findChildByNameNullDtoThrows() throws Exception {
    final List<RepositoryFileTreeDto> children = new ArrayList<>();
    children.add( node( null, null ) );
    final RepositoryFileTreeDto root = node( dto( "", true, false ), children );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );
    // lookup forces findChildByName iteration, which throws on the null-file child
    model.exists( childName( "anything", "/anything" ) );
  }

  @Test
  public void lookupNodeReturnsNullWhenChildrenMissing() throws Exception {
    // a folder with null children -> deeper lookup returns null -> exists == false
    final RepositoryFileDto folderFile = dto( "folder", true, false );
    final RepositoryFileTreeDto folder = node( folderFile, null );
    final RepositoryFileTreeDto root =
      node( dto( "", true, false ), new ArrayList<>( List.of( folder ) ) );
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( root );

    final FileName rootFn = rootName();
    final FileName deep = mock( FileName.class );
    final FileName folderFn = mock( FileName.class );
    when( deep.getBaseName() ).thenReturn( "child" );
    when( deep.getPath() ).thenReturn( "/folder/child" );
    when( deep.getParent() ).thenReturn( folderFn );
    when( folderFn.getBaseName() ).thenReturn( "folder" );
    when( folderFn.getParent() ).thenReturn( rootFn );
    when( folderFn.getPath() ).thenReturn( "/folder" );

    assertFalse( model.exists( deep ) );
  }

  @Test
  public void lookupNodeRefreshesWhenRootNull() throws Exception {
    final RepositoryFileTreeDto root = node( dto( "", true, false ),
      new ArrayList<>( List.of( node( dto( "report.prpt", false, false ), null ) ) ) );
    // root is null on this model -> the first lookup triggers refresh() inside lookupNode
    final JCRSolutionFileModel model = new JCRSolutionFileModel( "", mockClient( root ), false );
    assertTrue( model.exists( reportName() ) );
  }

  @Test
  public void getUrlReturnsFormattedService() throws Exception {
    final String url = modelWithReport().getUrl( reportName() );
    assertNotNull( url );
  }

  @Test( expected = FileSystemException.class )
  public void getUrlNotFoundThrows() throws Exception {
    modelWithReport().getUrl( childName( "missing", "/missing" ) );
  }

  @Test
  public void getParamServiceUrlPrefixesServerUrl() throws Exception {
    final String url = modelWithReport().getParamServiceUrl( reportName() );
    assertNotNull( url );
    assertTrue( url.startsWith( "http://server" ) );
  }

  @Test
  public void getDataReturnsBytes() throws Exception {
    final RepositoryFileDto childFile = dto( "report.prpt", false, false );
    childFile.setPath( "/report.prpt" );
    final RepositoryFileTreeDto root =
      node( dto( "", true, false ), new ArrayList<>( List.of( node( childFile, null ) ) ) );

    final byte[] payload = new byte[] { 1, 2, 3 };
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.get( byte[].class ) ).thenReturn( payload );
    final WebTarget target = mock( WebTarget.class );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );
    final Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );

    final JCRSolutionFileModel model = new JCRSolutionFileModel( "http://server", client, false );
    model.setRoot( root );
    assertArrayEquals( payload, model.getData( reportName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void getDataNotFoundThrows() throws Exception {
    modelWithReport().getData( childName( "missing", "/missing" ) );
  }

  @Test( expected = IllegalStateException.class )
  public void getDataNullDtoThrowsIllegalState() throws Exception {
    final JCRSolutionFileModel model =
      new JCRSolutionFileModel( "http://server", mock( Client.class ), false );
    model.setRoot( node( null, new ArrayList<>() ) );
    model.getData( rootName() );
  }

  @SuppressWarnings( "java:S1874" )
  @Test
  public void getDataFallsBackWhenEncodingFails() throws Exception {
    final RepositoryFileDto childFile = dto( "report.prpt", false, false );
    childFile.setPath( "/report.prpt" );
    final RepositoryFileTreeDto root =
      node( dto( "", true, false ), new ArrayList<>( List.of( node( childFile, null ) ) ) );

    final byte[] payload = new byte[] { 9 };
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.get( byte[].class ) ).thenReturn( payload );
    final WebTarget target = mock( WebTarget.class );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );
    final Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );

    final JCRSolutionFileModel model = new JCRSolutionFileModel( "http://server", client, false );
    model.setRoot( root );

    try ( MockedStatic<URLEncoder> enc = mockStatic( URLEncoder.class ) ) {
      enc.when( () -> URLEncoder.encodeUTF8( anyString() ) ).thenThrow( new RuntimeException( "encode fail" ) );
      assertArrayEquals( payload, model.getData( reportName() ) );
    }
  }

  private JCRSolutionFileModel modelForUpload( int putStatus, RepositoryFileTreeDto refreshTree ) {
    final Response putResponse = mock( Response.class );
    when( putResponse.getStatus() ).thenReturn( putStatus );

    final Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.put( any() ) ).thenReturn( putResponse );
    when( builder.get( RepositoryFileTreeDto.class ) ).thenReturn( refreshTree );

    final WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request() ).thenReturn( builder );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );

    final Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );
    return new JCRSolutionFileModel( "http://server", client, false );
  }

  @Test
  public void setDataOkRefreshes() throws Exception {
    final RepositoryFileTreeDto refreshTree = node( dto( "", true, false ), null );
    final JCRSolutionFileModel model = modelForUpload( 200, refreshTree );
    model.setData( reportName(), new byte[] { 1 } );
    assertEquals( refreshTree, model.getRoot() );
  }

  @Test
  public void setDataUnauthorizedThrowsInvalidCredentials() throws Exception {
    final JCRSolutionFileModel model = modelForUpload( 401, null );
    try {
      model.setData( reportName(), new byte[] { 1 } );
      fail( "expected FileSystemException" );
    } catch ( FileSystemException e ) {
      assertTrue( e.getMessage().contains( "ERROR_INVALID_USERNAME_OR_PASSWORD" ) );
    }
  }

  @Test( expected = FileSystemException.class )
  public void setDataForbiddenThrows() throws Exception {
    modelForUpload( 403, null ).setData( reportName(), new byte[] { 1 } );
  }

  @Test( expected = FileSystemException.class )
  public void setDataMovedTemporarilyThrows() throws Exception {
    modelForUpload( 302, null ).setData( reportName(), new byte[] { 1 } );
  }

  @Test
  public void setDataOtherErrorThrowsFailed() throws Exception {
    final JCRSolutionFileModel model = modelForUpload( 500, null );
    try {
      model.setData( reportName(), new byte[] { 1 } );
      fail( "expected FileSystemException" );
    } catch ( FileSystemException e ) {
      assertTrue( e.getMessage().contains( "ERROR_FAILED" ) );
    }
  }

  private JCRSolutionFileModel modelForCreateFolder( Integer putStatus, boolean putThrows,
                                                     RepositoryFileTreeDto refreshTree ) {
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    if ( putThrows ) {
      when( builder.put( any() ) ).thenThrow( new RuntimeException( "put failed" ) );
    } else {
      final Response response = mock( Response.class );
      when( response.getStatus() ).thenReturn( putStatus );
      when( builder.put( any() ) ).thenReturn( response );
    }
    when( builder.get( RepositoryFileTreeDto.class ) ).thenReturn( refreshTree );

    final WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );
    when( target.request( anyString() ) ).thenReturn( builder );
    when( target.request() ).thenReturn( builder );

    final Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );
    return new JCRSolutionFileModel( "http://server", client, false );
  }

  @Test
  public void createFolderOkRefreshes() throws Exception {
    final RepositoryFileTreeDto refreshTree = node( dto( "", true, false ), null );
    final JCRSolutionFileModel model = modelForCreateFolder( 200, false, refreshTree );
    model.createFolder( childName( "newFolder", "/newFolder" ) );
    assertEquals( refreshTree, model.getRoot() );
  }

  @Test( expected = FileSystemException.class )
  public void createFolderNonOkThrows() throws Exception {
    modelForCreateFolder( 500, false, null ).createFolder( childName( "newFolder", "/newFolder" ) );
  }

  @Test( expected = FileSystemException.class )
  public void createFolderExceptionWrapped() throws Exception {
    modelForCreateFolder( null, true, null ).createFolder( childName( "newFolder", "/newFolder" ) );
  }

  private JCRSolutionFileModel modelForDelete( Integer status, boolean nullResponse,
                                               RepositoryFileTreeDto refreshTree ) {
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    if ( nullResponse ) {
      when( builder.put( any() ) ).thenReturn( null );
    } else {
      final Response response = mock( Response.class );
      when( response.getStatus() ).thenReturn( status );
      when( builder.put( any() ) ).thenReturn( response );
    }
    when( builder.get( RepositoryFileTreeDto.class ) ).thenReturn( refreshTree );

    final WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request() ).thenReturn( builder );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );

    final Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );

    final RepositoryFileDto childFile = dto( "report.prpt", false, false );
    childFile.setId( "id-1" );
    final RepositoryFileTreeDto root =
      node( dto( "", true, false ), new ArrayList<>( List.of( node( childFile, null ) ) ) );
    final JCRSolutionFileModel model = new JCRSolutionFileModel( "http://server", client, false );
    model.setRoot( root );
    return model;
  }

  @Test
  public void deleteOkReturnsTrue() throws Exception {
    final RepositoryFileTreeDto refreshTree = node( dto( "", true, false ), null );
    assertTrue( modelForDelete( 200, false, refreshTree ).delete( reportName() ) );
  }

  @Test( expected = FileSystemException.class )
  public void deleteNonOkThrows() throws Exception {
    modelForDelete( 500, false, null ).delete( reportName() );
  }

  @Test( expected = FileSystemException.class )
  public void deleteNullResponseThrows() throws Exception {
    modelForDelete( null, true, null ).delete( reportName() );
  }

  @Test( expected = FileSystemException.class )
  public void deleteNullNameThrows() throws Exception {
    modelForDelete( 200, false, null ).delete( null );
  }

  @Test( expected = FileSystemException.class )
  public void deleteFileNotFoundThrows() throws Exception {
    modelForDelete( 200, false, null ).delete( childName( "missing", "/missing" ) );
  }

  @Test
  public void deleteWithNullFileDtoThrowsBiServerNullObject() throws Exception {
    final Invocation.Builder builder = mock( Invocation.Builder.class );
    when( builder.put( any() ) ).thenReturn( null );

    final WebTarget target = mock( WebTarget.class );
    when( target.path( anyString() ) ).thenReturn( target );
    when( target.request() ).thenReturn( builder );
    when( target.request( any( MediaType.class ) ) ).thenReturn( builder );

    final Client client = mock( Client.class );
    when( client.target( anyString() ) ).thenReturn( target );

    final RepositoryFileTreeDto child = new RepositoryFileTreeDto();
    child.setFile( null );
    final RepositoryFileTreeDto root = node( dto( "", true, false ),
      new ArrayList<>( List.of( child ) ) );
    final JCRSolutionFileModel model = new JCRSolutionFileModel( "http://server", client, false );
    model.setRoot( root );

    try {
      model.delete( childName( "any", "/any" ) );
      fail( "Expected FileSystemException" );
    } catch ( FileSystemException ex ) {
      assertTrue( ex.getMessage().contains( "BI-Server returned" ) );
    }
  }

  @Test( expected = NullPointerException.class )
  public void testGetFormattedServiceUrlNullServiceThrows() throws Exception {

    JCRSolutionFileModel model = modelWithReport();

    java.lang.reflect.Method m =
      JCRSolutionFileModel.class.getDeclaredMethod(
        "getFormattedServiceUrl",
        String.class,
        FileName.class );

    m.setAccessible( true );

    try {
      m.invoke( model, null, reportName() );
    } catch ( java.lang.reflect.InvocationTargetException e ) {
      throw (Exception) e.getCause();
    }
  }

  @Test( expected = FileSystemException.class )
  public void testLookupNodeRefreshIOExceptionWrapped() throws Exception {

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false ) {

        @Override
        public void refresh() throws IOException {
          throw new IOException( "boom" );
        }
      };

    java.lang.reflect.Method m =
      JCRSolutionFileModel.class.getDeclaredMethod(
        "lookupNode",
        String[].class );

    m.setAccessible( true );

    try {
      m.invoke( model, (Object) new String[] { "abc" } );
    } catch ( java.lang.reflect.InvocationTargetException e ) {
      throw (Exception) e.getCause();
    }
  }

  @Test
  public void testLookupNodeRootPathReturnsRoot() throws Exception {

    RepositoryFileTreeDto root =
      new RepositoryFileTreeDto();

    root.setFile( new RepositoryFileDto() );

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false );

    model.setRoot( root );

    java.lang.reflect.Method m =
      JCRSolutionFileModel.class.getDeclaredMethod(
        "lookupNode",
        String[].class );

    m.setAccessible( true );

    Object result =
      m.invoke( model, (Object) new String[] { "" } );

    assertSame( root, result );
  }

  @Test
  public void testSetDataMultiLevelPathUsesSlashBranch()
    throws Exception {

    FileName root = mock( FileName.class );
    FileName folder = mock( FileName.class );
    FileName file = mock( FileName.class );

    when( file.getBaseName() ).thenReturn( "report.prpt" );
    when( file.getParent() ).thenReturn( folder );

    when( folder.getBaseName() ).thenReturn( "public" );
    when( folder.getParent() ).thenReturn( root );

    when( root.getBaseName() ).thenReturn( "" );
    when( root.getParent() ).thenReturn( null );

    JCRSolutionFileModel model =
      org.mockito.Mockito.spy(
        modelForUpload(
          HttpStatus.SC_OK,
          node( dto( "", true, false ), null ) ) );

    model.setData( file, new byte[] { 1 } );

    verify( model ).refresh();
  }

  @Test
  public void testSetDataRefreshIOExceptionIgnored() throws Exception {

    RepositoryFileTreeDto refreshTree =
      node( dto( "", true, false ), null );

    JCRSolutionFileModel model =
      modelForUpload( 200, refreshTree );

    JCRSolutionFileModel spy =
      org.mockito.Mockito.spy( model );

    org.mockito.Mockito.doThrow(
        new IOException( "refresh failed" ) )
      .when( spy )
      .refresh();

    spy.setData( reportName(), new byte[] { 1 } );

    verify( spy ).refresh();
  }

  @Test( expected = FileSystemException.class )
  public void testDeleteNullRepositoryFileThrows()
    throws Exception {

    RepositoryFileTreeDto child =
      new RepositoryFileTreeDto();

    child.setFile( null );

    RepositoryFileTreeDto root =
      node(
        dto( "", true, false ),
        List.of( child ) );

    JCRSolutionFileModel model =
      new JCRSolutionFileModel(
        "http://server",
        mock( Client.class ),
        false );

    model.setRoot( root );

    model.delete( rootName() );
  }
}
