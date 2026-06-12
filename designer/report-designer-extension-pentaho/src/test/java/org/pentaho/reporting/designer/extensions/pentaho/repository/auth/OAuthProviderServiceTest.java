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

package org.pentaho.reporting.designer.extensions.pentaho.repository.auth;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OAuthProviderServiceTest {
  private static final String LOCAL_URL = "http://localhost:8080/pentaho";
  private static final String GOOGLE = "Google";
  private static final String JSON_OBJ_A = "{\"a\":1}";
  private static final String JSON_OBJ_B = "{\"b\":2}";
  private static final String JSON_ONE_OBJECT = JSON_OBJ_A;
  private static final String JSON_TWO_OBJECTS = JSON_OBJ_A + "," + JSON_OBJ_B;
  private static final String OAUTH_ENABLED_TRUE_QUOTED = "{\"isOAuthEnabled\":\"true\"}";

  @Test
  public void testNormalizeBaseUrlTrailingSlashRemoved() {
    assertEquals( LOCAL_URL,
      OAuthProviderService.normalizeBaseUrl( LOCAL_URL + "/" ) );
  }

  @Test
  public void testNormalizeBaseUrlNoTrailingSlashUnchanged() {
    assertEquals( LOCAL_URL,
      OAuthProviderService.normalizeBaseUrl( LOCAL_URL ) );
  }

  @Test
  public void testBuildProvidersUrlAppendsPath() {
    final String url = OAuthProviderService.buildProvidersUrl( "http://server" );
    assertTrue( url.startsWith( "http://server" ) );
    assertTrue( url.contains( "/oauth-providers" ) );
  }

  @Test
  public void testIsOAuthEnabledNullUrlReturnsFalse() {
    assertFalse( OAuthProviderService.isOAuthEnabled( null ) );
  }

  @Test
  public void testIsOAuthEnabledEmptyUrlReturnsFalse() {
    assertFalse( OAuthProviderService.isOAuthEnabled( "" ) );
  }

  @Test
  public void testIsOAuthEnabledBlankUrlReturnsFalse() {
    assertFalse( OAuthProviderService.isOAuthEnabled( "   " ) );
  }

  @Test
  public void testParseOAuthEnabledNullJsonReturnsFalse() {
    assertFalse( OAuthProviderService.parseOAuthEnabled( null ) );
  }

  @Test
  public void testParseOAuthEnabledEmptyJsonReturnsFalse() {
    assertFalse( OAuthProviderService.parseOAuthEnabled( "" ) );
  }

  @Test
  public void testParseOAuthEnabledNonObjectJsonReturnsFalse() {
    assertFalse( OAuthProviderService.parseOAuthEnabled( "[1,2,3]" ) );
  }

  @Test
  public void testParseOAuthEnabledQuotedTrueReturnsTrue() {
    assertTrue( OAuthProviderService.parseOAuthEnabled( OAUTH_ENABLED_TRUE_QUOTED ) );
  }

  @Test
  public void testParseOAuthEnabledQuotedFalseReturnsFalse() {
    assertFalse( OAuthProviderService.parseOAuthEnabled( "{\"isOAuthEnabled\":\"false\"}" ) );
  }

  @Test
  public void testParseOAuthEnabledBareTrueReturnsTrue() {
    assertTrue( OAuthProviderService.parseOAuthEnabled( "{\"isOAuthEnabled\":true}" ) );
  }

  @Test
  public void testParseOAuthEnabledBareFalseReturnsFalse() {
    assertFalse( OAuthProviderService.parseOAuthEnabled( "{\"isOAuthEnabled\":false}" ) );
  }

  @Test
  public void testParseOAuthEnabledMissingKeyReturnsFalse() {
    assertFalse( OAuthProviderService.parseOAuthEnabled( "{\"otherKey\":true}" ) );
  }

  @Test
  public void testExtractJsonStringMissingKeyReturnsNull() {
    assertNull( OAuthProviderService.extractJsonString( "{\"a\":\"b\"}", "missingKey" ) );
  }

  @Test
  public void testExtractJsonStringPresentKeyReturnsValue() {
    assertEquals( "hello",
      OAuthProviderService.extractJsonString( "{\"key\":\"hello\"}", "key" ) );
  }

  @Test
  public void testExtractJsonStringNoColonReturnsNull() {
    assertNull( OAuthProviderService.extractJsonString( "{\"key\"}", "key" ) );
  }

  @Test
  public void testIsOAuthEnabledConnectionDisconnectedOnException() {
    assertFalse(
      OAuthProviderService.isOAuthEnabled(
        "http://localhost:1/pentaho" ) );
  }

  @Test
  public void testParseProvidersBadEntryCovered() {

    List<OAuthProvider> result =
      OAuthProviderService.parseProviders(
        "[{}, {\"enabled\":true}]" );

    assertNotNull( result );
  }

  @Test
  public void testPrivateConstructor() throws Exception {

    Constructor<OAuthProviderService> c =
      OAuthProviderService.class.getDeclaredConstructor();

    c.setAccessible( true );

    assertNotNull( c.newInstance() );
  }

  @Test
  public void testParseOAuthEnabledBlankJsonReturnsFalse() {
    assertFalse(
      OAuthProviderService.parseOAuthEnabled(
        "     " ) );
  }

  @Test
  public void testParseProvidersMalformedProviderObject() {

    List<OAuthProvider> providers =
      OAuthProviderService.parseProviders(
        "[{\"enabled\":true},{{{{{{]" );

    assertNotNull( providers );
  }

  @Test
  public void testParseProvidersProviderThrowsBranch() {

    List<OAuthProvider> providers =
      OAuthProviderService.parseProviders(
        "[{\"enabled\":true},\"bad\"]" );

    assertNotNull( providers );
  }

  @Test
  public void testExtractJsonStringNoOpeningQuoteReturnsNull() {
    assertNull( OAuthProviderService.extractJsonString( "{\"key\":noQuote}", "key" ) );
  }

  @Test
  public void testExtractJsonBooleanMissingKeyReturnsFalse() {
    assertFalse( OAuthProviderService.extractJsonBoolean( "{\"a\":true}", "missing" ) );
  }

  @Test
  public void testExtractJsonBooleanTrueValueReturnsTrue() {
    assertTrue( OAuthProviderService.extractJsonBoolean( "{\"enabled\":true}", "enabled" ) );
  }

  @Test
  public void testExtractJsonBooleanFalseValueReturnsFalse() {
    assertFalse( OAuthProviderService.extractJsonBoolean( "{\"enabled\":false}", "enabled" ) );
  }

  @Test
  public void testExtractJsonBooleanNoColonReturnsFalse() {
    assertFalse( OAuthProviderService.extractJsonBoolean( "{\"enabled\"}", "enabled" ) );
  }

  @Test
  public void testSplitJsonObjectsSingleObject() {
    final List<String> result = OAuthProviderService.splitJsonObjects( JSON_ONE_OBJECT );
    assertEquals( 1, result.size() );
    assertEquals( JSON_ONE_OBJECT, result.get( 0 ) );
  }

  @Test
  public void testSplitJsonObjectsTwoObjects() {
    final List<String> result = OAuthProviderService.splitJsonObjects( JSON_TWO_OBJECTS );
    assertEquals( 2, result.size() );
  }

  @Test
  public void testSplitJsonObjectsEmptyStringReturnsEmptyList() {
    assertTrue( OAuthProviderService.splitJsonObjects( "" ).isEmpty() );
  }

  @Test
  public void testSplitJsonObjectsNestedBraces() {
    final List<String> result = OAuthProviderService.splitJsonObjects( "{\"a\":{\"b\":1}}" );
    assertEquals( 1, result.size() );
  }

  @Test
  public void testParseProviderSetsAllFields() {
    final String json = "{\"authorizationUri\":\"http://auth\","
      + "\"imageUri\":\"http://img\","
      + "\"clientName\":\"Google\","
      + "\"registrationId\":\"google\","
      + "\"enabled\":true}";
    final OAuthProvider provider = OAuthProviderService.parseProvider( json );
    assertEquals( "http://auth", provider.getAuthorizationUri() );
    assertEquals( "http://img", provider.getImageUri() );
    assertEquals( GOOGLE, provider.getClientName() );
    assertEquals( "google", provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void testParseProviderDisabledProvider() {
    final String json = "{\"clientName\":\"Azure\",\"enabled\":false}";
    final OAuthProvider provider = OAuthProviderService.parseProvider( json );
    assertEquals( "Azure", provider.getClientName() );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void testParseProvidersNullJsonReturnsEmpty() {
    assertTrue( OAuthProviderService.parseProviders( null ).isEmpty() );
  }

  @Test
  public void testParseProvidersEmptyJsonReturnsEmpty() {
    assertTrue( OAuthProviderService.parseProviders( "" ).isEmpty() );
  }

  @Test
  public void testParseProvidersEmptyArrayReturnsEmpty() {
    assertTrue( OAuthProviderService.parseProviders( "[]" ).isEmpty() );
  }

  @Test
  public void testParseProvidersNonArrayJsonReturnsEmpty() {
    assertTrue( OAuthProviderService.parseProviders( "{\"a\":1}" ).isEmpty() );
  }

  @Test
  public void testIsJsonArrayResponseNullReturnsFalse() {
    assertFalse( OAuthProviderService.isJsonArrayResponse( null ) );
  }

  @Test
  public void testIsJsonArrayResponseHtmlReturnsFalse() {
    assertFalse( OAuthProviderService.isJsonArrayResponse( "<html>login</html>" ) );
  }

  @Test
  public void testIsJsonArrayResponseArrayReturnsTrue() {
    assertTrue( OAuthProviderService.isJsonArrayResponse( "[]" ) );
  }

  @Test
  public void testIsJsonArrayResponseStartsWithArrayButMissingClosingBracketReturnsFalse() {
    assertFalse( OAuthProviderService.isJsonArrayResponse( "[not-closed" ) );
  }

  @Test
  public void testIsJsonArrayResponseEndsWithArrayButMissingOpeningBracketReturnsFalse() {
    assertFalse( OAuthProviderService.isJsonArrayResponse( "not-opened]" ) );
  }

  @Test
  public void testParseProvidersOneEnabledProviderReturnsList() {
    final String json = "[{\"authorizationUri\":\"http://auth\","
      + "\"clientName\":\"Google\","
      + "\"registrationId\":\"google\","
      + "\"enabled\":true}]";
    final List<OAuthProvider> providers = OAuthProviderService.parseProviders( json );
    assertEquals( 1, providers.size() );
    assertEquals( GOOGLE, providers.get( 0 ).getClientName() );
  }

  @Test
  public void testParseProvidersDisabledProviderNotIncluded() {
    final String json = "[{\"clientName\":\"Disabled\",\"enabled\":false}]";
    assertTrue( OAuthProviderService.parseProviders( json ).isEmpty() );
  }

  @Test
  public void testParseProvidersFetchProvidersNullUrlReturnsEmpty() throws Exception {
    assertTrue( OAuthProviderService.fetchProviders( null ).isEmpty() );
  }

  @Test
  public void testParseProvidersFetchProvidersEmptyUrlReturnsEmpty() throws Exception {
    assertTrue( OAuthProviderService.fetchProviders( "" ).isEmpty() );
  }

  @Test
  public void testParseProvidersMixedEnabledAndDisabledReturnsOnlyEnabled() {
    final String json = "[{\"clientName\":\"A\",\"enabled\":true},"
      + "{\"clientName\":\"B\",\"enabled\":false},"
      + "{\"clientName\":\"C\",\"enabled\":true}]";
    final List<OAuthProvider> providers = OAuthProviderService.parseProviders( json );
    assertEquals( 2, providers.size() );
    assertNotNull( providers.get( 0 ).getClientName() );
  }

  @Test
  public void testIsProvidersEndpointAvailableNullUrlReturnsFalse() {
    assertFalse( OAuthProviderService.isProvidersEndpointAvailable( null ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableEmptyUrlReturnsFalse() {
    assertFalse( OAuthProviderService.isProvidersEndpointAvailable( "" ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableBlankUrlReturnsFalse() {
    assertFalse( OAuthProviderService.isProvidersEndpointAvailable( "   " ) );
  }

  /**
   * Starts a test server that returns the specified HTTP status.
   */
  private void withServer( final int status, final ThrowingRunnable<String> test ) throws Exception {
    withServerBody( status, null, test );
  }

  /**
   * Starts a test server that returns the specified HTTP status and response body.
   */
  private void withServerBody( final int status, final String body,
                               final ThrowingRunnable<String> test ) throws Exception {
    final com.sun.net.httpserver.HttpServer server =
      com.sun.net.httpserver.HttpServer.create( new java.net.InetSocketAddress( 0 ), 0 );
    server.createContext( "/", exchange -> {
      if ( body != null ) {
        final byte[] bytes = body.getBytes( java.nio.charset.StandardCharsets.UTF_8 );
        exchange.getResponseHeaders().add( "Content-Type", "application/json" );
        exchange.sendResponseHeaders( status, bytes.length );
        exchange.getResponseBody().write( bytes );
      } else {
        exchange.sendResponseHeaders( status, -1 );
      }
      exchange.close();
    } );
    server.start();
    try {
      final String url = "http://localhost:" + server.getAddress().getPort() + "/pentaho";
      test.run( url );
    } finally {
      server.stop( 0 );
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable<T> {
    void run( T arg ) throws java.io.IOException;
  }

  @Test
  public void testIsOAuthEnabledServer200WithTrueReturnsTrue() throws Exception {
    withServerBody( 200, "{\"isOAuthEnabled\":true}",
      url -> assertTrue( OAuthProviderService.isOAuthEnabled( url ) ) );
  }

  @Test
  public void testIsOAuthEnabledServer200WithFalseReturnsFalse() throws Exception {
    withServerBody( 200, "{\"isOAuthEnabled\":false}",
      url -> assertFalse( OAuthProviderService.isOAuthEnabled( url ) ) );
  }

  @Test
  public void testIsOAuthEnabledServer200WithQuotedTrueReturnsTrue() throws Exception {
    withServerBody( 200, OAUTH_ENABLED_TRUE_QUOTED,
      url -> assertTrue( OAuthProviderService.isOAuthEnabled( url ) ) );
  }

  @Test
  public void testIsOAuthEnabledServer200WithMissingKeyReturnsFalse() throws Exception {
    withServerBody( 200, "{\"otherKey\":\"value\"}",
      url -> assertFalse( OAuthProviderService.isOAuthEnabled( url ) ) );
  }

  @Test
  public void testFetchProvidersServer200WithHtmlBodyThrowsIOException() throws Exception {
    withServerBody( 200, "<html>login</html>", url -> {
      try {
        OAuthProviderService.fetchProviders( url );
        fail( "Expected IOException for non-JSON provider response" );
      } catch ( java.io.IOException expected ) {
        assertTrue( expected.getMessage().contains( "Unexpected OAuth providers response" ) );
      }
    } );
  }

  @Test
  public void testIsOAuthEnabledServer401ReturnsFalse() throws Exception {
    withServer( 401, url -> assertFalse( OAuthProviderService.isOAuthEnabled( url ) ) );
  }

  @Test
  public void testIsOAuthEnabledServer404ReturnsFalse() throws Exception {
    withServer( 404, url -> assertFalse( OAuthProviderService.isOAuthEnabled( url ) ) );
  }

  @Test
  public void testIsOAuthEnabledServerUnreachableReturnsFalse() {
    // Unreachable host — must not throw, must return false
    assertFalse( OAuthProviderService.isOAuthEnabled( "http://localhost:19875/pentaho" ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableServer200ReturnsTrue() throws Exception {
    withServer( 200, url -> assertTrue( OAuthProviderService.isProvidersEndpointAvailable( url ) ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableServer401ReturnsTrue() throws Exception {
    withServer( 401, url -> assertTrue( OAuthProviderService.isProvidersEndpointAvailable( url ) ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableServer403ReturnsTrue() throws Exception {
    withServer( 403, url -> assertTrue( OAuthProviderService.isProvidersEndpointAvailable( url ) ) );
  }

  @Test
  public void testExtractJsonStringUnclosedQuoteReturnsNull() {
    assertNull(
      OAuthProviderService.extractJsonString(
        "{\"key\":\"value",
        "key" ) );
  }

  @Test
  public void testExtractJsonStringWithEscapedQuoteParsesUntilUnescapedQuote() {
    final String value = OAuthProviderService.extractJsonString(
      "{\"key\":\"ab\\\"cd\"}", "key" );
    assertEquals( "ab\\\"cd", value );
  }

  @Test
  public void testParseProvidersInvalidObjectIgnored() {
    List<OAuthProvider> result =
      OAuthProviderService.parseProviders(
        "[{\"enabled\":true},invalid-json]" );

    assertNotNull( result );
  }

  @Test
  public void testParseProvidersMalformedArrayReturnsEmpty() {
    List<OAuthProvider> result =
      OAuthProviderService.parseProviders(
        "[{\"enabled\":true}" );

    assertTrue( result.isEmpty() );
  }

  @Test
  public void testParseOAuthEnabledColonMissingReturnsFalse() {

    assertFalse(
      OAuthProviderService.parseOAuthEnabled(
        "{\"isOAuthEnabled\"}" )
    );
  }

  @Test
  public void testParseOAuthEnabledInvalidBooleanReturnsFalse() {

    assertFalse(
      OAuthProviderService.parseOAuthEnabled(
        "{\"isOAuthEnabled\":xyz}" )
    );
  }

  @Test
  public void testParseProviderMissingFields() {
    OAuthProvider provider =
      OAuthProviderService.parseProvider( "{}" );

    assertNull( provider.getAuthorizationUri() );
    assertNull( provider.getImageUri() );
    assertNull( provider.getClientName() );
    assertNull( provider.getRegistrationId() );
    assertFalse( provider.isEnabled() );
  }

  @Test
  public void testSplitJsonObjectsUnbalancedBraces() {

    List<String> result =
      OAuthProviderService.splitJsonObjects(
        "{\"a\":1" );

    assertTrue( result.isEmpty() );
  }

  @Test
  public void testNormalizeBaseUrlRootSlashOnly() {

    assertEquals(
      "",
      OAuthProviderService.normalizeBaseUrl( "/" )
    );
  }

  @Test
  public void testIsProvidersEndpointAvailableServer404ReturnsFalse() throws Exception {
    withServer( 404, url -> assertFalse( OAuthProviderService.isProvidersEndpointAvailable( url ) ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableServer500ReturnsTrue() throws Exception {
    withServer( 500, url -> assertTrue( OAuthProviderService.isProvidersEndpointAvailable( url ) ) );
  }

  @Test
  public void testIsProvidersEndpointAvailableServerUnreachableReturnsFalse() {
    assertFalse( OAuthProviderService.isProvidersEndpointAvailable(
      "http://localhost:19874/pentaho" ) );
  }

  @Test
  public void testFetchProvidersServer400ThrowsIOExceptionWithStatusMessage() throws Exception {
    withServer( 400, url -> {
      try {
        OAuthProviderService.fetchProviders( url );
        fail( "Expected IOException for 400 response" );
      } catch ( java.io.IOException e ) {
        assertTrue( "Exception message should contain HTTP status code",
          e.getMessage().contains( "Server returned HTTP" ) );
      }
    } );
  }

  @Test
  public void testFetchProvidersServer500ThrowsIOExceptionWithStatusMessage() throws Exception {
    withServer( 500, url -> {
      try {
        OAuthProviderService.fetchProviders( url );
        fail( "Expected IOException for 500 response" );
      } catch ( java.io.IOException e ) {
        assertTrue( "Exception message should contain HTTP status code",
          e.getMessage().contains( "Server returned HTTP" ) );
      }
    } );
  }

  @Test
  public void testIsOAuthEnabledFileSchemeUrlNullConnectionInFinally() {
    assertFalse( OAuthProviderService.isOAuthEnabled( "file:///test/path" ) );
  }

  @Test
  public void testFetchProvidersFileSchemeUrlNullConnectionInFinally() {
    try {
      OAuthProviderService.fetchProviders( "file:///test/path" );
      fail( "Expected an exception for file:// scheme" );
    } catch ( java.io.IOException | ClassCastException e ) {
      // expected — connection was null in the finally block
    }
  }

  @Test
  public void testParseProvidersStartsWithBracketButMissingClosingBracketReturnsEmpty() {
    final List<OAuthProvider> result = OAuthProviderService.parseProviders( "[{\"enabled\":true}" );
    assertTrue( "parseProviders should return empty list for unclosed array", result.isEmpty() );
  }
}
