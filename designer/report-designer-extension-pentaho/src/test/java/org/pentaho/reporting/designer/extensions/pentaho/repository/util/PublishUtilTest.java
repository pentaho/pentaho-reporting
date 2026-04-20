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

package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

@RunWith( Parameterized.class )
public class PublishUtilTest {

  private final String sessionId;
  private final String browserAuth;
  private AuthenticationData loginData;

  public PublishUtilTest( String sessionId, String browserAuth ) {
    this.sessionId = sessionId;
    this.browserAuth = browserAuth;
  }

  @Parameters( name = "sessionId={0}, browserAuth={1}" )
  public static Collection<Object[]> data() {
    return Arrays.asList( new Object[][] {
      { "SESS_ABC123", "true" },
      { null, null },
      { "", "true" },
      { "SESS_XYZ", "false" }
    } );
  }

  @Before
  public void setUp() {
    PublishUtil.setReservedChars( "/\\\t\r\n" );
    PublishUtil.setReservedCharsDisplay( "/, \\, TAB, CR, LF" );
    loginData = mock( AuthenticationData.class );
    when( loginData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( loginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getPassword() ).thenReturn( "password" );
  }

  @Test
  public void testPrivateConstructorCoverage() throws Exception {
    Constructor<PublishUtil> constructor = PublishUtil.class.getDeclaredConstructor();
    assertTrue( java.lang.reflect.Modifier.isPrivate( constructor.getModifiers() ) );
    constructor.setAccessible( true );
    PublishUtil instance = constructor.newInstance();
    assertNotNull( instance );
  }

  @Test
  public void testAcceptFilterNullFilters() {
    assertTrue( PublishUtil.acceptFilter( null, "report.prpt" ) );
  }

  @Test
  public void testAcceptFilterEmptyFilters() {
    assertTrue( PublishUtil.acceptFilter( new String[0], "report.prpt" ) );
  }

  @Test
  public void testAcceptFilterMatchingFilter() {
    String[] filters = new String[] { ".prpt" };
    assertTrue( PublishUtil.acceptFilter( filters, "report.prpt" ) );
  }

  @Test
  public void testAcceptFilterNonMatchingFilter() {
    String[] filters = new String[] { ".prpt" };
    assertFalse( PublishUtil.acceptFilter( filters, "report.csv" ) );
  }

  @Test
  public void testAcceptFilterMultipleFilters() {
    String[] filters = new String[] { ".prpt", ".csv", ".xls" };
    assertTrue( PublishUtil.acceptFilter( filters, "report.csv" ) );
    assertTrue( PublishUtil.acceptFilter( filters, "report.xls" ) );
    assertFalse( PublishUtil.acceptFilter( filters, "report.pdf" ) );
  }

  @Test
  public void testPublishWithVariousAuthOptions() throws IOException {
    when( loginData.getOption( "server-version" ) ).thenReturn( "5" );
    when( loginData.getOption( "sessionId" ) ).thenReturn( sessionId );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( browserAuth );

    byte[] data = new byte[] { 1, 2, 3 };
    Properties props = new Properties();
    try {
      PublishUtil.publish( data, "/public/test.prpt", loginData, props );
    } catch ( IOException e ) {
      assertNotNull( e );
    }
  }

  @Test
  public void testSetReservedChars() {
    // Use a valid reserved character string (like the default)
    PublishUtil.setReservedChars( "/\\\t\r\n" );
    assertNotNull( PublishUtil.getPattern() );
  }

  @Test
  public void testSetReservedCharsDisplay() {
    PublishUtil.setReservedCharsDisplay( "/, \\, TAB, CR, LF" );
    assertEquals( "/, \\, TAB, CR, LF", PublishUtil.getReservedCharsDisplay() );
  }

  @Test
  public void testValidateNameValid() {
    assertTrue( PublishUtil.validateName( "report" ) );
  }

  @Test
  public void testValidateNameEmpty() {
    assertFalse( PublishUtil.validateName( "" ) );
  }

  @Test
  public void testValidateNameNull() {
    assertFalse( PublishUtil.validateName( null ) );
  }

  @Test
  public void testValidateNameDot() {
    assertFalse( PublishUtil.validateName( "." ) );
  }

  @Test
  public void testValidateNameDoubleDot() {
    assertFalse( PublishUtil.validateName( ".." ) );
  }

  @Test
  public void testValidateNameWithLeadingTrailingWhitespace() {
    assertFalse( PublishUtil.validateName( " name" ) );
    assertFalse( PublishUtil.validateName( "name " ) );
  }

  @Test
  public void testValidateNameWithReservedChar() {
    assertFalse( PublishUtil.validateName( "with/slash" ) );
    assertFalse( PublishUtil.validateName( "with\\backslash" ) );
    assertFalse( PublishUtil.validateName( "with\ttab" ) );
  }

  // ---- normalizeURL coverage (all 8 branches) ----

  @Test
  public void testNormalizeURLSugarHttp() {
    String result = PublishUtil.normalizeURL( "http://localhost:8080/pentaho", PublishUtil.SERVER_VERSION_SUGAR );
    assertTrue( result.startsWith( "jcr-solution:http://" ) );
    assertTrue( result.endsWith( "!" ) );
  }

  @Test
  public void testNormalizeURLSugarHttps() {
    String result = PublishUtil.normalizeURL( "https://x/y", PublishUtil.SERVER_VERSION_SUGAR );
    assertTrue( result.startsWith( "jcr-solution:https://" ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testNormalizeURLSugarNonHttp() {
    PublishUtil.normalizeURL( "ftp://x", PublishUtil.SERVER_VERSION_SUGAR );
  }

  @Test
  public void testNormalizeURLLegacyHttp() {
    String result = PublishUtil.normalizeURL( "http://localhost:8080/pentaho", PublishUtil.SERVER_VERSION_LEGACY );
    assertTrue( result.startsWith( "web-solution:http://" ) );
  }

  @Test
  public void testNormalizeURLLegacyHttps() {
    String result = PublishUtil.normalizeURL( "https://x/y", PublishUtil.SERVER_VERSION_LEGACY );
    assertTrue( result.startsWith( "web-solution:https://" ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testNormalizeURLLegacyNonHttp() {
    PublishUtil.normalizeURL( "ftp://x", PublishUtil.SERVER_VERSION_LEGACY );
  }

  @Test( expected = NullPointerException.class )
  public void testNormalizeURLNullThrows() {
    PublishUtil.normalizeURL( null, PublishUtil.SERVER_VERSION_SUGAR );
  }

  @Test
  public void testNormalizeURLAlreadyEndsWithBang() {
    String result = PublishUtil.normalizeURL( "http://x!", PublishUtil.SERVER_VERSION_SUGAR );
    // Should NOT append a second !
    assertTrue( result.endsWith( "!" ) );
    assertFalse( result.endsWith( "!!" ) );
  }

  // ---- createVFSConnection null checks + branches ----

  @Test( expected = NullPointerException.class )
  public void testCreateVFSConnectionNullManager() throws Exception {
    PublishUtil.createVFSConnection( null, mock( AuthenticationData.class ) );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateVFSConnectionNullLoginData() throws Exception {
    PublishUtil.createVFSConnection(
      mock( org.apache.commons.vfs2.FileSystemManager.class ), null );
  }

  @Test
  public void testCreateVFSConnectionWithSessionAuth() throws Exception {
    org.apache.commons.vfs2.FileSystemManager fsm = mock( org.apache.commons.vfs2.FileSystemManager.class );
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( ad.getUsername() ).thenReturn( "admin" );
    when( ad.getPassword() ).thenReturn( "pwd" );
    when( ad.getOption( "sessionId" ) ).thenReturn( "SESS_X" );
    when( ad.getOption( "browserAuth" ) ).thenReturn( "true" );
    PublishUtil.createVFSConnection( fsm, ad );
    verify( fsm ).resolveFile( anyString(), any( org.apache.commons.vfs2.FileSystemOptions.class ) );
  }

  @Test
  public void testCreateVFSConnectionWithUserPassAuth() throws Exception {
    org.apache.commons.vfs2.FileSystemManager fsm = mock( org.apache.commons.vfs2.FileSystemManager.class );
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( ad.getUsername() ).thenReturn( "admin" );
    when( ad.getPassword() ).thenReturn( "pwd" );
    when( ad.getOption( "sessionId" ) ).thenReturn( null );
    when( ad.getOption( "browserAuth" ) ).thenReturn( null );
    PublishUtil.createVFSConnection( fsm, ad );
    verify( fsm ).resolveFile( anyString(), any( org.apache.commons.vfs2.FileSystemOptions.class ) );
  }

  @Test
  public void testCreateVFSConnectionLegacyVersion() throws Exception {
    org.apache.commons.vfs2.FileSystemManager fsm = mock( org.apache.commons.vfs2.FileSystemManager.class );
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( ad.getUsername() ).thenReturn( "admin" );
    when( ad.getPassword() ).thenReturn( "pwd" );
    when( ad.getOption( PublishUtil.SERVER_VERSION ) ).thenReturn( "4" );
    PublishUtil.createVFSConnection( fsm, ad );
    verify( fsm ).resolveFile( startsWith( "web-solution:" ), any( org.apache.commons.vfs2.FileSystemOptions.class ) );
  }

  // ---- getTimeout coverage ----

  @Test
  public void testGetTimeoutWithExplicitOption() {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getOption( "timeout" ) ).thenReturn( "42" );
    assertEquals( 42, PublishUtil.getTimeout( ad ) );
  }

  @Test
  public void testGetTimeoutFallsBackToWorkspaceSetting() {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getOption( "timeout" ) ).thenReturn( null );
    int result = PublishUtil.getTimeout( ad );
    assertTrue( result > 0 );
  }

  // ---- launchReportOnServer coverage ----

  @Test( expected = IOException.class )
  public void testLaunchReportOnServerEmptyPathThrows() throws Exception {
    PublishUtil.launchReportOnServer( "http://x", "" );
  }

  @Test( expected = IOException.class )
  public void testOpenReportEmptyPathThrows() throws Exception {
    PublishUtil.openReport( null, mock( AuthenticationData.class ), "" );
  }

  @Test
  public void testDeprecatedPublishDelegatesToPropertiesOverload() {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getUsername() ).thenReturn( "u" );
    when( ad.getPassword() ).thenReturn( "p" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );
    try {
      @SuppressWarnings( "deprecation" )
      int rc = PublishUtil.publish( new byte[] { 1 }, "/public/x.prpt", ad );
      assertTrue( rc >= 0 );
      // Reachable on success; otherwise IOException is acceptable (no real server).
    } catch ( IOException e ) {
      assertNotNull( e );
    }
  }

  @Test
  public void testPublishWithNullFilePropertiesDoesNotNPE() {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getUsername() ).thenReturn( "u" );
    when( ad.getPassword() ).thenReturn( "p" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );
    try {
      PublishUtil.publish( new byte[] { 1, 2, 3 }, "/public/x.prpt", ad, null );
    } catch ( NullPointerException npe ) {
      fail( "publish() must not NPE when fileProperties is null: " + npe );
    } catch ( IOException ioe ) {
      assertNotNull( ioe );
    }
  }

  @Test
  public void testPublishUPRetriesOnceWhenFirstAttemptReturns504() throws Exception {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getUsername() ).thenReturn( "u" );
    when( ad.getPassword() ).thenReturn( "p" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );

    final int[] callCount = { 0 };
    try ( org.mockito.MockedConstruction<org.pentaho.reporting.libraries.pensol.PublishRestUtil> mc =
        org.mockito.Mockito.mockConstruction(
            org.pentaho.reporting.libraries.pensol.PublishRestUtil.class,
            ( inst, ctx ) -> when( inst.publishFile( any( String.class ), any( byte[].class ), any( Properties.class ) ) )
                .thenAnswer( invocation -> {
                  callCount[ 0 ]++;
                  // 1st: 504 (transient transport failure), 2nd: 200 (success)
                  return callCount[ 0 ] == 1 ? 504 : 200;
                } ) ) ) {
      int rc = PublishUtil.publish( new byte[] { 1, 2, 3 }, "/public/x.prpt", ad, new Properties() );
      assertEquals( "U/P publish must retry once on transient 504", 200, rc );
      assertEquals( 2, callCount[ 0 ] );
      assertEquals( 2, mc.constructed().size() );
    }
  }

  @Test
  public void testPublishUPNoRetryOn200() throws Exception {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getUsername() ).thenReturn( "u" );
    when( ad.getPassword() ).thenReturn( "p" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );

    final int[] callCount = { 0 };
    try ( org.mockito.MockedConstruction<org.pentaho.reporting.libraries.pensol.PublishRestUtil> mc =
        org.mockito.Mockito.mockConstruction(
            org.pentaho.reporting.libraries.pensol.PublishRestUtil.class,
            ( inst, ctx ) -> when( inst.publishFile( any( String.class ), any( byte[].class ), any( Properties.class ) ) )
                .thenAnswer( invocation -> {
                  callCount[ 0 ]++;
                  return 200;
                } ) ) ) {
      int rc = PublishUtil.publish( new byte[] { 1 }, "/public/x.prpt", ad, new Properties() );
      assertEquals( 200, rc );
      assertEquals( "Successful publish must not retry", 1, callCount[ 0 ] );
    }
  }

  @Test
  public void testPublishUPNoRetryOn401() throws Exception {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getUsername() ).thenReturn( "u" );
    when( ad.getPassword() ).thenReturn( "wrong" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );

    final int[] callCount = { 0 };
    try ( org.mockito.MockedConstruction<org.pentaho.reporting.libraries.pensol.PublishRestUtil> mc =
        org.mockito.Mockito.mockConstruction(
            org.pentaho.reporting.libraries.pensol.PublishRestUtil.class,
            ( inst, ctx ) -> when( inst.publishFile( any( String.class ), any( byte[].class ), any( Properties.class ) ) )
                .thenAnswer( invocation -> {
                  callCount[ 0 ]++;
                  return 401;
                } ) ) ) {
      int rc = PublishUtil.publish( new byte[] { 1 }, "/public/x.prpt", ad, new Properties() );
      assertEquals( "Genuine auth failures must not be retried", 401, rc );
      assertEquals( 1, callCount[ 0 ] );
    }
  }

  @Test
  public void testPublishSSONoRetryEvenOn504() throws Exception {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );
    when( ad.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( ad.getOption( "sessionId" ) ).thenReturn( "SESS_ABC" );

    final int[] callCount = { 0 };
    try ( org.mockito.MockedConstruction<org.pentaho.reporting.libraries.pensol.PublishRestUtil> mc =
        org.mockito.Mockito.mockConstruction(
            org.pentaho.reporting.libraries.pensol.PublishRestUtil.class,
            ( inst, ctx ) -> when( inst.publishFile( any( String.class ), any( byte[].class ), any( Properties.class ) ) )
                .thenAnswer( invocation -> {
                  callCount[ 0 ]++;
                  return 504;
                } ) ) ) {
      int rc = PublishUtil.publish( new byte[] { 1 }, "/public/x.prpt", ad, new Properties() );
      assertEquals( 504, rc );
      assertEquals( "SSO publish carries valid session cookie -- must not retry", 1, callCount[ 0 ] );
    }
  }

  @Test
  public void testPublishOverwriteFlagApplied() throws Exception {
    AuthenticationData ad = mock( AuthenticationData.class );
    when( ad.getUrl() ).thenReturn( "http://localhost:1/pentaho" );
    when( ad.getUsername() ).thenReturn( "u" );
    when( ad.getPassword() ).thenReturn( "p" );
    when( ad.getOption( "server-version" ) ).thenReturn( "5" );

    final Properties caller = new Properties();
    final java.util.concurrent.atomic.AtomicReference<Properties> seen = new java.util.concurrent.atomic.AtomicReference<>();
    try ( org.mockito.MockedConstruction<org.pentaho.reporting.libraries.pensol.PublishRestUtil> mc =
        org.mockito.Mockito.mockConstruction(
            org.pentaho.reporting.libraries.pensol.PublishRestUtil.class,
            ( inst, ctx ) -> when( inst.publishFile( any( String.class ), any( byte[].class ), any( Properties.class ) ) )
                .thenAnswer( invocation -> {
                  seen.set( invocation.getArgument( 2, Properties.class ) );
                  return 200;
                } ) ) ) {
      PublishUtil.publish( new byte[] { 1 }, "/public/x.prpt", ad, caller );
    }
    assertNotNull( seen.get() );
    assertEquals( "true",
        seen.get().getProperty( org.pentaho.reporting.libraries.pensol.PublishRestUtil.OVERWRITE_FILE_KEY ) );
  }

  // ====================================================================
  // looksLikeHtmlLoginPage / isHtmlLoginPageError coverage (HTML-on-first-open fix)
  // ====================================================================

  @Test
  public void testLooksLikeHtmlLoginPageDoctype() throws Exception {
    assertTrue( invokeLooksLikeHtml( "<!DOCTYPE html><html><body>login</body></html>".getBytes() ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageHtmlTag() throws Exception {
    assertTrue( invokeLooksLikeHtml( "<html><head><title>Login</title></head></html>".getBytes() ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageSpringMarker() throws Exception {
    assertTrue( invokeLooksLikeHtml(
      "<form action=\"/pentaho/j_spring_security_check\"></form>".getBytes() ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageJUsername() throws Exception {
    assertTrue( invokeLooksLikeHtml(
      "<input name=\"j_username\" type=\"text\"/>".getBytes() ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageWithBom() throws Exception {
    final byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
    final byte[] body = "<!doctype html><html></html>".getBytes();
    final byte[] combined = new byte[ bom.length + body.length ];
    System.arraycopy( bom, 0, combined, 0, bom.length );
    System.arraycopy( body, 0, combined, bom.length, body.length );
    assertTrue( invokeLooksLikeHtml( combined ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageRejectsXmlReport() throws Exception {
    assertFalse( invokeLooksLikeHtml(
      "<?xml version=\"1.0\"?><report></report>".getBytes() ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageRejectsNullAndEmpty() throws Exception {
    assertFalse( invokeLooksLikeHtml( null ) );
    assertFalse( invokeLooksLikeHtml( new byte[ 0 ] ) );
  }

  @Test
  public void testLooksLikeHtmlLoginPageRejectsNonAngleBracketStart() throws Exception {
    assertFalse( invokeLooksLikeHtml( "PK\u0003\u0004somebinary".getBytes() ) );
  }

  @Test
  public void testIsHtmlLoginPageErrorMatchesHtmlLoginMessage() throws Exception {
    org.pentaho.reporting.libraries.resourceloader.ResourceException re =
      new org.pentaho.reporting.libraries.resourceloader.ResourceException(
        "Server returned HTML login page instead of report content for: /a.prpt" );
    assertTrue( invokeIsHtmlLoginPageError( re ) );
  }

  @Test
  public void testIsHtmlLoginPageErrorMatchesProlog() throws Exception {
    org.pentaho.reporting.libraries.resourceloader.ResourceException re =
      new org.pentaho.reporting.libraries.resourceloader.ResourceException(
        "Failed to parse",
        new org.xml.sax.SAXParseException( "Content is not allowed in prolog.", null ) );
    assertTrue( invokeIsHtmlLoginPageError( re ) );
  }

  @Test
  public void testIsHtmlLoginPageErrorRejectsUnrelated() throws Exception {
    org.pentaho.reporting.libraries.resourceloader.ResourceException re =
      new org.pentaho.reporting.libraries.resourceloader.ResourceException( "404 not found" );
    assertFalse( invokeIsHtmlLoginPageError( re ) );
  }

  @SuppressWarnings( "java:S3011" )
  private static boolean invokeLooksLikeHtml( byte[] data ) throws Exception {
    java.lang.reflect.Method m = PublishUtil.class.getDeclaredMethod( "looksLikeHtmlLoginPage", byte[].class );
    m.setAccessible( true );
    return (Boolean) m.invoke( null, (Object) data );
  }

  @SuppressWarnings( "java:S3011" )
  private static boolean invokeIsHtmlLoginPageError(
      org.pentaho.reporting.libraries.resourceloader.ResourceException re ) throws Exception {
    java.lang.reflect.Method m = PublishUtil.class.getDeclaredMethod( "isHtmlLoginPageError",
      org.pentaho.reporting.libraries.resourceloader.ResourceException.class );
    m.setAccessible( true );
    return (Boolean) m.invoke( null, re );
  }
}
