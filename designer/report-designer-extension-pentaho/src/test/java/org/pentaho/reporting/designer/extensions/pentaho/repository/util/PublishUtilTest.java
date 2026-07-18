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


package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.libraries.pensol.PentahoSolutionsFileSystemConfigBuilder;
import org.pentaho.reporting.libraries.pensol.PublishRestUtil;

public class PublishUtilTest {

  @Test
  public void testIsAuthenticationErrorNullExceptionReturnsFalse() {
    assertFalse( PublishUtil.isAuthenticationError( null ) );
  }

  @Test
  public void testIsAuthenticationErrorNoMessageReturnsFalse() {
    assertFalse( PublishUtil.isAuthenticationError( new RuntimeException( (String) null ) ) );
  }

  @Test
  public void testIsAuthenticationErrorInvalidUsernameOrPasswordMsgReturnsTrue() {
    assertTrue( PublishUtil.isAuthenticationError(
        new RuntimeException( "Invalid username or password" ) ) );
  }

  @Test
  public void testIsAuthenticationErrorUnauthorizedMsgReturnsTrue() {
    assertTrue( PublishUtil.isAuthenticationError(
        new RuntimeException( "401 Unauthorized" ) ) );
  }

  @Test
  public void testIsAuthenticationErrorStatusCode401MsgReturnsTrue() {
    assertTrue( PublishUtil.isAuthenticationError(
        new RuntimeException( "Server returned status code 401" ) ) );
  }

  @Test
  public void testIsAuthenticationErrorStatusCode403MsgReturnsTrue() {
    assertTrue( PublishUtil.isAuthenticationError(
        new RuntimeException( "Server returned status code 403" ) ) );
  }

  @Test
  public void testIsAuthenticationErrorCaseInsensitiveReturnsTrue() {
    assertTrue( PublishUtil.isAuthenticationError(
        new RuntimeException( "INVALID USERNAME OR PASSWORD" ) ) );
  }

  @Test
  public void testIsAuthenticationErrorUnrelatedMessageReturnsFalse() {
    assertFalse( PublishUtil.isAuthenticationError(
        new RuntimeException( "Connection timed out" ) ) );
  }

  @Test
  public void testIsAuthenticationErrorMatchInCauseReturnsTrue() {
    final RuntimeException cause = new RuntimeException( "Invalid username or password" );
    final RuntimeException wrapper = new RuntimeException( "Wrapped", cause );
    assertTrue( PublishUtil.isAuthenticationError( wrapper ) );
  }

  @Test
  public void testIsAuthenticationErrorMatchInGrandcauseReturnsTrue() {
    final RuntimeException root = new RuntimeException( "status code 403" );
    final RuntimeException mid = new RuntimeException( "Mid", root );
    final RuntimeException top = new RuntimeException( "Top", mid );
    assertTrue( PublishUtil.isAuthenticationError( top ) );
  }

  @Test
  public void testIsAuthenticationErrorNoMatchInChainReturnsFalse() {
    final RuntimeException cause = new RuntimeException( "File not found" );
    final RuntimeException wrapper = new RuntimeException( "Outer error", cause );
    assertFalse( PublishUtil.isAuthenticationError( wrapper ) );
  }

  @Test
  public void testIsAuthenticationErrorCircularCauseReturnsFalse() {
    final RuntimeException ex = new RuntimeException( "something" ) {
      @Override
      public synchronized Throwable getCause() {
        return this; // circular reference
      }
    };
    assertFalse( PublishUtil.isAuthenticationError( ex ) );
  }

  @Test
  public void testAcceptFilterNullFiltersReturnsTrue() {
    assertTrue( PublishUtil.acceptFilter( null, "a.prpt" ) );
  }

  @Test
  public void testAcceptFilterEmptyFiltersReturnsTrue() {
    assertTrue( PublishUtil.acceptFilter( new String[ 0 ], "a.prpt" ) );
  }

  @Test
  public void testAcceptFilterMatchReturnsTrue() {
    assertTrue( PublishUtil.acceptFilter( new String[] { ".prpt", ".xml" }, "report.prpt" ) );
  }

  @Test
  public void testAcceptFilterNoMatchReturnsFalse() {
    assertFalse( PublishUtil.acceptFilter( new String[] { ".prpt" }, "report.txt" ) );
  }

  @Test( expected = NullPointerException.class )
  public void testNormalizeUrlNullThrows() {
    PublishUtil.normalizeURL( null, PublishUtil.SERVER_VERSION_SUGAR );
  }

  @Test
  public void testNormalizeUrlSugarHttp() {
    assertEquals( "jcr-solution:http://host:8080/pentaho",
        PublishUtil.normalizeURL( "http://host:8080/pentaho", PublishUtil.SERVER_VERSION_SUGAR ) );
  }

  @Test
  public void testNormalizeUrlSugarHttps() {
    assertEquals( "jcr-solution:https://host:8080/pentaho",
        PublishUtil.normalizeURL( "https://host:8080/pentaho", PublishUtil.SERVER_VERSION_SUGAR ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testNormalizeUrlSugarInvalidThrows() {
    PublishUtil.normalizeURL( "ftp://host", PublishUtil.SERVER_VERSION_SUGAR );
  }

  @Test
  public void testNormalizeUrlLegacyHttp() {
    assertEquals( "web-solution:http://host/pentaho",
        PublishUtil.normalizeURL( "http://host/pentaho", PublishUtil.SERVER_VERSION_LEGACY ) );
  }

  @Test
  public void testNormalizeUrlLegacyHttps() {
    assertEquals( "web-solution:https://host/pentaho",
        PublishUtil.normalizeURL( "https://host/pentaho", PublishUtil.SERVER_VERSION_LEGACY ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testNormalizeUrlLegacyInvalidThrows() {
    PublishUtil.normalizeURL( "ftp://host", PublishUtil.SERVER_VERSION_LEGACY );
  }

  @Test
  public void testValidateNameValid() {
    assertTrue( PublishUtil.validateName( "report" ) );
  }

  @Test
  public void testValidateNameEmptyFalse() {
    assertFalse( PublishUtil.validateName( "" ) );
  }

  @Test
  public void testValidateNameLeadingTrailingSpaceFalse() {
    assertFalse( PublishUtil.validateName( " report " ) );
  }

  @Test
  public void testValidateNameReservedCharFalse() {
    assertFalse( PublishUtil.validateName( "a/b" ) );
  }

  @Test
  public void testValidateNameSingleDotFalse() {
    assertFalse( PublishUtil.validateName( "." ) );
  }

  @Test
  public void testValidateNameDoubleDotFalse() {
    assertFalse( PublishUtil.validateName( ".." ) );
  }

  @Test
  public void testSetReservedCharsAndGetPattern() {
    final String original = PublishUtil.getReservedCharsDisplay();
    try {
      PublishUtil.setReservedChars( "#@" );
      assertNotNull( PublishUtil.getPattern() );
      assertTrue( PublishUtil.getPattern().matcher( "a#b" ).matches() );
      assertFalse( PublishUtil.validateName( "a@b" ) );
    } finally {
      PublishUtil.setReservedChars( "/\\\t\r\n" );
      PublishUtil.setReservedCharsDisplay( original );
    }
  }

  @Test
  public void testReservedCharsDisplayAccessors() {
    final String original = PublishUtil.getReservedCharsDisplay();
    try {
      PublishUtil.setReservedCharsDisplay( "X, Y" );
      assertEquals( "X, Y", PublishUtil.getReservedCharsDisplay() );
    } finally {
      PublishUtil.setReservedCharsDisplay( original );
    }
  }

  @Test
  public void testGetTimeoutFromOption() {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    when( loginData.getOption( "timeout" ) ).thenReturn( "45" );
    assertEquals( 45, PublishUtil.getTimeout( loginData ) );
  }

  @Test
  public void testGetTimeoutDefaultWhenMissing() {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    when( loginData.getOption( "timeout" ) ).thenReturn( null );
    // falls back to WorkspaceSettings default — just ensure it does not throw and returns a value
    assertTrue( PublishUtil.getTimeout( loginData ) >= 0 );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateVFSConnectionNullManagerThrows() throws Exception {
    PublishUtil.createVFSConnection( (FileSystemManager) null, mock( AuthenticationData.class ) );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateVFSConnectionNullLoginDataThrows() throws Exception {
    PublishUtil.createVFSConnection( mock( FileSystemManager.class ), null );
  }

  private AuthenticationData mockLoginData( int version, String sessionId ) {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    when( loginData.getOption( PublishUtil.SERVER_VERSION ) ).thenReturn( String.valueOf( version ) );
    when( loginData.getOption( "timeout" ) ).thenReturn( "30" );
    when( loginData.getOption( "sessionId" ) ).thenReturn( sessionId );
    when( loginData.getUrl() ).thenReturn( "http://host:8080/pentaho" );
    when( loginData.getUsername() ).thenReturn( "user" );
    when( loginData.getPassword() ).thenReturn( "pass" );
    return loginData;
  }

  @Test
  public void testCreateVFSConnectionWithoutSessionId() throws Exception {
    final FileObject expected = mock( FileObject.class );
    final FileSystemManager fsm = mock( FileSystemManager.class );
    when( fsm.resolveFile( anyString(), any() ) ).thenReturn( expected );
    assertSame( expected,
        PublishUtil.createVFSConnection( fsm, mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, null ) ) );
  }

  @Test
  public void testCreateVFSConnectionWithSessionId() throws Exception {
    final FileObject expected = mock( FileObject.class );
    final FileSystemManager fsm = mock( FileSystemManager.class );
    when( fsm.resolveFile( anyString(), any() ) ).thenReturn( expected );
    assertSame( expected,
        PublishUtil.createVFSConnection( fsm, mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, "sid-1" ) ) );
  }

  @Test
  public void testCreateVFSConnectionSingleArgUsesVfsManager() throws Exception {
    final FileObject expected = mock( FileObject.class );
    final FileSystemManager fsm = mock( FileSystemManager.class );
    when( fsm.resolveFile( anyString(), any() ) ).thenReturn( expected );
    try ( MockedStatic<VFS> vfs = mockStatic( VFS.class ) ) {
      vfs.when( VFS::getManager ).thenReturn( fsm );
      assertSame( expected, PublishUtil.createVFSConnection( mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, null ) ) );
    }
  }

  @Test
  public void testCreateBundleDataReturnsBytes() throws Exception {
    final MasterReport report = mock( MasterReport.class );
    try ( MockedStatic<BundleWriter> bw = mockStatic( BundleWriter.class ) ) {
      bw.when( () -> BundleWriter.writeReportToZipStream( eq( report ), any() ) ).thenAnswer( inv -> null );
      assertNotNull( PublishUtil.createBundleData( report ) );
    }
  }

  @Test( expected = BundleWriterException.class )
  public void testCreateBundleDataWrapsIOException() throws Exception {
    final MasterReport report = mock( MasterReport.class );
    try ( MockedStatic<BundleWriter> bw = mockStatic( BundleWriter.class ) ) {
      bw.when( () -> BundleWriter.writeReportToZipStream( eq( report ), any() ) )
        .thenThrow( new IOException( "write fail" ) );
      PublishUtil.createBundleData( report );
    }
  }

  @Test( expected = Exception.class )
  public void testLoadReportNullDataThrows() throws Exception {
    final Method m = PublishUtil.class.getDeclaredMethod( "loadReport", byte[].class, String.class );
    m.setAccessible( true );
    try {
      m.invoke( null, null, "f.prpt" );
    } catch ( java.lang.reflect.InvocationTargetException e ) {
      throw (Exception) e.getCause();
    }
  }

  @Test
  public void testLoadReportValidSetsAttribute() throws Exception {
    final Method m = PublishUtil.class.getDeclaredMethod( "loadReport", byte[].class, String.class );
    m.setAccessible( true );
    final MasterReport report = mock( MasterReport.class );
    try ( MockedStatic<OpenReportAction> ora = mockStatic( OpenReportAction.class ) ) {
      ora.when( () -> OpenReportAction.loadReport( any( byte[].class ), any() ) ).thenReturn( report );
      final Object result = m.invoke( null, new byte[] { 1, 2 }, "f.prpt" );
      assertSame( report, result );
    }
  }

  @Test
  public void testPublishSugarUsesRestUtil() throws Exception {
    final AuthenticationData loginData = mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, "sid" );
    try ( MockedConstruction<PublishRestUtil> pru = mockConstruction( PublishRestUtil.class,
        ( m, c ) -> when( m.publishFile( anyString(), any(), any() ) ).thenReturn( 200 ) ) ) {
      assertEquals( 200, PublishUtil.publish( new byte[] { 1 }, "/f.prpt", loginData, new Properties() ) );
    }
  }

  @Test
  public void testPublishSugarNullPropertiesDefaults() throws Exception {
    final AuthenticationData loginData = mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, null );
    try ( MockedConstruction<PublishRestUtil> pru = mockConstruction( PublishRestUtil.class,
        ( m, c ) -> when( m.publishFile( anyString(), any(), any() ) ).thenReturn( 201 ) ) ) {
      assertEquals( 201, PublishUtil.publish( new byte[] { 1 }, "/f.prpt", loginData, null ) );
    }
  }

  @Test
  public void testCreateVFSConnectionSetsSessionIdWhenPresent() throws Exception {

    FileObject expected = mock( FileObject.class );
    FileSystemManager fsm = mock( FileSystemManager.class );

    when( fsm.resolveFile( anyString(), any( FileSystemOptions.class ) ) )
      .thenReturn( expected );

    AuthenticationData loginData =
      mockLoginData(
        PublishUtil.SERVER_VERSION_SUGAR,
        "SESSION123" );

    try ( MockedConstruction<PentahoSolutionsFileSystemConfigBuilder> mocked =
            mockConstruction(
              PentahoSolutionsFileSystemConfigBuilder.class ) ) {

      PublishUtil.createVFSConnection(
        fsm,
        loginData );

      PentahoSolutionsFileSystemConfigBuilder builder =
        mocked.constructed().get( 0 );

      verify( builder )
        .setSessionId(
          any( FileSystemOptions.class ),
          eq( "SESSION123" ) );
    }
  }

  @Test
  public void testCreateVFSConnectionDoesNotSetSessionIdWhenEmpty()
    throws Exception {

    FileObject expected = mock( FileObject.class );
    FileSystemManager fsm = mock( FileSystemManager.class );

    when( fsm.resolveFile( anyString(), any( FileSystemOptions.class ) ) )
      .thenReturn( expected );

    AuthenticationData loginData =
      mockLoginData(
        PublishUtil.SERVER_VERSION_SUGAR,
        "" );

    try ( MockedConstruction<PentahoSolutionsFileSystemConfigBuilder> mocked =
            mockConstruction(
              PentahoSolutionsFileSystemConfigBuilder.class ) ) {

      PublishUtil.createVFSConnection(
        fsm,
        loginData );

      PentahoSolutionsFileSystemConfigBuilder builder =
        mocked.constructed().get( 0 );

      verify( builder, never() )
        .setSessionId(
          any( FileSystemOptions.class ),
          anyString() );
    }
  }

  @SuppressWarnings("java:S1874")
  @Test
  public void testPublishDeprecatedWrapperDelegates() throws Exception {
    final AuthenticationData loginData = mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, null );
    try ( MockedConstruction<PublishRestUtil> pru = mockConstruction( PublishRestUtil.class,
        ( m, c ) -> when( m.publishFile( anyString(), any(), any() ) ).thenReturn( 200 ) ) ) {
      assertEquals( 200, PublishUtil.publish( new byte[] { 1 }, "/f.prpt", loginData ) );
    }
  }

  @Test
  public void testPublishLegacyUsesVfs() throws Exception {
    final AuthenticationData loginData = mockLoginData( PublishUtil.SERVER_VERSION_LEGACY, null );

    final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    final FileContent content = mock( FileContent.class );
    when( content.getOutputStream( false ) ).thenReturn( out );
    final FileObject object = mock( FileObject.class );
    when( object.getContent() ).thenReturn( content );
    final FileObject connection = mock( FileObject.class );
    when( connection.resolveFile( anyString() ) ).thenReturn( object );
    final FileSystemManager fsm = mock( FileSystemManager.class );
    when( fsm.resolveFile( anyString(), any() ) ).thenReturn( connection );

    try ( MockedStatic<VFS> vfs = mockStatic( VFS.class ) ) {
      vfs.when( VFS::getManager ).thenReturn( fsm );
      assertEquals( 200, PublishUtil.publish( new byte[] { 5, 6 }, "/f.prpt", loginData, new Properties() ) );
    }
    assertArrayEquals( new byte[] { 5, 6 }, out.toByteArray() );
  }

  @Test( expected = IOException.class )
  public void testOpenReportEmptyPathThrows() throws Exception {
    PublishUtil.openReport( mock( ReportDesignerContext.class ), mock( AuthenticationData.class ), "" );
  }

  @Test( expected = FileNotFoundException.class )
  public void testOpenReportMissingFileThrows() throws Exception {
    final AuthenticationData loginData = mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, null );
    final FileObject object = mock( FileObject.class );
    when( object.exists() ).thenReturn( false );
    final FileObject connection = mock( FileObject.class );
    when( connection.resolveFile( anyString() ) ).thenReturn( object );
    final FileSystemManager fsm = mock( FileSystemManager.class );
    when( fsm.resolveFile( anyString(), any() ) ).thenReturn( connection );

    try ( MockedStatic<VFS> vfs = mockStatic( VFS.class ) ) {
      vfs.when( VFS::getManager ).thenReturn( fsm );
      PublishUtil.openReport( mock( ReportDesignerContext.class ), loginData, "/missing.prpt" );
    }
  }

  @Test
  public void testOpenReportSuccess() throws Exception {
    final AuthenticationData loginData = mockLoginData( PublishUtil.SERVER_VERSION_SUGAR, null );

    final byte[] payload = new byte[] { 1, 2, 3, 4 };
    final FileContent content = mock( FileContent.class );
    when( content.getInputStream() ).thenReturn( new ByteArrayInputStream( payload ) );
    when( content.getSize() ).thenReturn( (long) payload.length );
    final FileObject object = mock( FileObject.class );
    when( object.exists() ).thenReturn( true );
    when( object.getContent() ).thenReturn( content );
    final FileObject connection = mock( FileObject.class );
    when( connection.resolveFile( anyString() ) ).thenReturn( object );
    final FileSystemManager fsm = mock( FileSystemManager.class );
    when( fsm.resolveFile( anyString(), any() ) ).thenReturn( connection );

    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final ReportRenderContext renderContext = mock( ReportRenderContext.class );
    when( context.addMasterReport( any() ) ).thenReturn( 0 );
    when( context.getReportRenderContext( 0 ) ).thenReturn( renderContext );

    try ( MockedStatic<VFS> vfs = mockStatic( VFS.class );
          MockedStatic<OpenReportAction> ora = mockStatic( OpenReportAction.class ) ) {
      vfs.when( VFS::getManager ).thenReturn( fsm );
      ora.when( () -> OpenReportAction.loadReport( any( byte[].class ), any() ) )
          .thenReturn( mock( MasterReport.class ) );
      assertSame( renderContext, PublishUtil.openReport( context, loginData, "/report.prpt" ) );
    }
  }

  @Test( expected = IOException.class )
  public void testLaunchReportOnServerEmptyPathThrows() throws Exception {
    PublishUtil.launchReportOnServer( "http://host", "" );
  }

  @Test
  public void testLaunchReportOnServerOpensUrl() throws Exception {
    final org.pentaho.reporting.libraries.base.config.Configuration config =
        mock( org.pentaho.reporting.libraries.base.config.Configuration.class );
    when( config.getConfigProperty( anyString() ) ).thenReturn( "/viewer?path={0}" );
    final org.pentaho.reporting.designer.core.ReportDesignerBoot boot =
        mock( org.pentaho.reporting.designer.core.ReportDesignerBoot.class );
    when( boot.getGlobalConfig() ).thenReturn( config );

    try ( MockedStatic<org.pentaho.reporting.designer.core.ReportDesignerBoot> rdb =
              mockStatic( org.pentaho.reporting.designer.core.ReportDesignerBoot.class );
          MockedStatic<ExternalToolLauncher> etl = mockStatic( ExternalToolLauncher.class ) ) {
      rdb.when( org.pentaho.reporting.designer.core.ReportDesignerBoot::getInstance ).thenReturn( boot );
      PublishUtil.launchReportOnServer( "http://host", "/public/report.prpt" );
      etl.verify( () -> ExternalToolLauncher.openURL( anyString() ) );
    }
  }
}
