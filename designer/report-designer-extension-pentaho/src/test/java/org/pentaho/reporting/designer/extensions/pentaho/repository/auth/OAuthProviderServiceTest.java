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

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for {@link OAuthProviderService} - 100% coverage of static methods.
 */
@RunWith( Parameterized.class )
public class OAuthProviderServiceTest {

  private static final String MICROSOFT = "Microsoft";

  @Test
  public void testPrivateConstructorCoverage() throws Exception {
    Constructor<OAuthProviderService> constructor =
      OAuthProviderService.class.getDeclaredConstructor();
    assertTrue( java.lang.reflect.Modifier.isPrivate( constructor.getModifiers() ) );
    constructor.setAccessible( true );
    OAuthProviderService instance = constructor.newInstance();
    assertNotNull( instance );
  }

  @Test
  public void buildProvidersUrlNoTrailingSlash() {
    String result = OAuthProviderService.buildProvidersUrl( "http://localhost:8080/pentaho" );
    assertEquals( "http://localhost:8080/pentaho/plugin/login/api/v0/oauth-providers", result );
  }

  @Test
  public void buildProvidersUrlWithTrailingSlash() {
    String result = OAuthProviderService.buildProvidersUrl( "http://localhost:8080/pentaho/" );
    assertEquals( "http://localhost:8080/pentaho/plugin/login/api/v0/oauth-providers", result );
  }

  @Parameter( 0 )
  public String fetchUrl;
  @Parameter( 1 )
  public boolean isFetchProviderTest;

  @Parameters( name = "{index}: {0}" )
  public static Collection<Object[]> data() {
    Collection<Object[]> data = new ArrayList<>();
    // fetchProviders tests
    data.add( new Object[] { null, true } );
    data.add( new Object[] { "", true } );
    data.add( new Object[] { "   ", true } );
    // parseProviders tests  
    data.add( new Object[] { null, false } );
    data.add( new Object[] { "", false } );
    data.add( new Object[] { "   ", false } );
    data.add( new Object[] { "[]", false } );
    data.add( new Object[] { "[  ]", false } );
    data.add( new Object[] { "{\"key\":\"value\"}", false } );
    return data;
  }

  @Test
  public void testEmptyInputs() throws IOException {
    if ( isFetchProviderTest ) {
      List<OAuthProvider> result = OAuthProviderService.fetchProviders( fetchUrl );
      assertTrue( "Expected empty list for fetchProviders: " + fetchUrl, result.isEmpty() );
    } else {
      List<OAuthProvider> result = OAuthProviderService.parseProviders( fetchUrl );
      assertTrue( "Expected empty list for parseProviders: " + fetchUrl, result.isEmpty() );
    }
  }

  @Test( expected = IOException.class )
  public void fetchProvidersUnreachableServer() throws IOException {
    OAuthProviderService.fetchProviders( "http://localhost:19999/pentaho" );
  }

  @Test
  public void parseProvidersSingleEnabledProvider() {
    String json = "[{\"authorizationUri\":\"oauth2/authorization/azure\","
      + "\"imageUri\":\"https://example.com/azure.png\","
      + "\"clientName\":\"" + MICROSOFT + "\","
      + "\"registrationId\":\"azure\","
      + "\"enabled\":true}]";

    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    assertEquals( 1, result.size() );

    OAuthProvider provider = result.get( 0 );
    assertEquals( "oauth2/authorization/azure", provider.getAuthorizationUri() );
    assertEquals( "https://example.com/azure.png", provider.getImageUri() );
    assertEquals( MICROSOFT, provider.getClientName() );
    assertEquals( "azure", provider.getRegistrationId() );
    assertTrue( provider.isEnabled() );
  }

  @Test
  public void parseProvidersSingleDisabledProvider() {
    String json = "[{\"authorizationUri\":\"oauth2/authorization/google\","
      + "\"clientName\":\"Google\","
      + "\"registrationId\":\"google\","
      + "\"enabled\":false}]";

    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    // Disabled providers are filtered out
    assertTrue( result.isEmpty() );
  }

  @Test
  public void parseProvidersMultipleProviders() {
    String json = "["
      + "{\"authorizationUri\":\"oauth2/authorization/azure\",\"clientName\":\"" + MICROSOFT + "\",\"registrationId\":\"azure\",\"enabled\":true},"
      + "{\"authorizationUri\":\"oauth2/authorization/google\",\"clientName\":\"Google\",\"registrationId\":\"google\",\"enabled\":true},"
      + "{\"authorizationUri\":\"oauth2/authorization/okta\",\"clientName\":\"Okta\",\"registrationId\":\"okta\",\"enabled\":false}"
      + "]";

    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    // Only enabled providers
    assertEquals( 2, result.size() );
    assertEquals( MICROSOFT, result.get( 0 ).getClientName() );
    assertEquals( "Google", result.get( 1 ).getClientName() );
  }

  @Test
  public void parseProvidersMissingFields() {
    String json = "[{\"clientName\":\"Minimal\",\"enabled\":true}]";

    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    assertEquals( 1, result.size() );
    OAuthProvider provider = result.get( 0 );
    assertEquals( "Minimal", provider.getClientName() );
    assertNull( provider.getAuthorizationUri() );
    assertNull( provider.getImageUri() );
    assertNull( provider.getRegistrationId() );
  }

  @Test
  public void parseProvidersMalformedJson() {
    // Not valid JSON but starts with [ — should not throw
    String json = "[{invalid json content}]";
    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    // parseProvider will still run (it just won't find proper keys)
    assertNotNull( result );
  }

  @Test
  public void parseProvidersWithWhitespace() {
    String json = "  [ { \"authorizationUri\" : \"oauth2/authorization/azure\" , "
      + "\"clientName\" : \"" + MICROSOFT + "\" , \"registrationId\" : \"azure\" , \"enabled\" : true } ]  ";

    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    assertEquals( 1, result.size() );
    assertEquals( MICROSOFT, result.get( 0 ).getClientName() );
  }

  @Test
  public void parseProvidersEnabledFieldMissing() {
    // No "enabled" field — default should be false → not included
    String json = "[{\"clientName\":\"NoEnabled\"}]";
    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    assertTrue( result.isEmpty() );
  }

  @Test
  public void parseProvidersEscapedQuotesInValue() {
    String json = "[{\"clientName\":\"With \\\"Quotes\\\"\",\"enabled\":true}]";
    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    assertEquals( 1, result.size() );
    assertEquals( "With \\\"Quotes\\\"", result.get( 0 ).getClientName() );
  }

  @Test
  public void parseProvidersImageUriPresent() {
    String json = "[{\"imageUri\":\"https://cdn.example.com/icon.svg\","
      + "\"clientName\":\"TestProvider\",\"enabled\":true}]";
    List<OAuthProvider> result = OAuthProviderService.parseProviders( json );
    assertEquals( 1, result.size() );
    assertEquals( "https://cdn.example.com/icon.svg", result.get( 0 ).getImageUri() );
  }

  @Test
  public void extractJsonStringMissingKeyReturnsNull() {
    assertNull( OAuthProviderService.extractJsonString( "{\"a\":\"1\"}", "missing" ) );
  }

  @Test
  public void extractJsonStringMissingColonReturnsNull() {
    // Key present but no colon after it
    assertNull( OAuthProviderService.extractJsonString( "{\"key\"}", "key" ) );
  }

  @Test
  public void extractJsonStringMissingOpeningQuoteReturnsNull() {
    // Key + colon present but no opening quote (numeric value)
    assertNull( OAuthProviderService.extractJsonString( "{\"key\":42}", "key" ) );
  }

  @Test
  public void extractJsonStringUnterminatedReturnsNull() {
    // Opening quote but no closing one
    assertNull( OAuthProviderService.extractJsonString( "{\"key\":\"unterminated", "key" ) );
  }

  @Test
  public void extractJsonStringHonoursEscapedClosingQuote() {
    String value = OAuthProviderService.extractJsonString(
      "{\"key\":\"a\\\"b\",\"x\":\"y\"}", "key" );
    assertEquals( "a\\\"b", value );
  }

  @Test
  public void extractJsonBooleanMissingKey() {
    assertFalse( OAuthProviderService.extractJsonBoolean( "{\"a\":true}", "missing" ) );
  }

  @Test
  public void extractJsonBooleanMissingColon() {
    assertFalse( OAuthProviderService.extractJsonBoolean( "{\"key\"}", "key" ) );
  }

  @Test
  public void extractJsonBooleanFalseValue() {
    assertFalse( OAuthProviderService.extractJsonBoolean( "{\"key\":false}", "key" ) );
  }

  @Test
  public void extractJsonBooleanTrueValueWithWhitespace() {
    assertTrue( OAuthProviderService.extractJsonBoolean( "{\"key\":   true}", "key" ) );
  }

  @Test
  public void splitJsonObjectsHandlesMultiple() {
    List<String> parts = OAuthProviderService.splitJsonObjects( "{\"a\":1},{\"b\":2}" );
    assertEquals( 2, parts.size() );
    assertEquals( "{\"a\":1}", parts.get( 0 ) );
    assertEquals( "{\"b\":2}", parts.get( 1 ) );
  }

  @Test
  public void splitJsonObjectsHandlesNestedBraces() {
    List<String> parts = OAuthProviderService.splitJsonObjects( "{\"a\":{\"x\":1}},{\"b\":2}" );
    assertEquals( 2, parts.size() );
    assertEquals( "{\"a\":{\"x\":1}}", parts.get( 0 ) );
    assertEquals( "{\"b\":2}", parts.get( 1 ) );
  }

  @Test
  public void splitJsonObjectsEmpty() {
    assertTrue( OAuthProviderService.splitJsonObjects( "" ).isEmpty() );
  }

  @Test
  public void parseProviderHandlesEmptyObject() {
    OAuthProvider p = OAuthProviderService.parseProvider( "{}" );
    assertNotNull( p );
    assertNull( p.getAuthorizationUri() );
    assertNull( p.getClientName() );
    assertFalse( p.isEnabled() );
  }

  @Test
  public void fetchProvidersReturnsProvidersOn200() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200,
      "[{\"authorizationUri\":\"oauth2/authorization/azure\","
        + "\"clientName\":\"MS\",\"registrationId\":\"azure\",\"enabled\":true}]" );
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/pentaho";
      List<OAuthProvider> result = OAuthProviderService.fetchProviders( url );
      assertEquals( 1, result.size() );
      assertEquals( "MS", result.get( 0 ).getClientName() );
    } finally {
      server.stop( 0 );
    }
  }

  @Test( expected = IOException.class )
  public void fetchProvidersThrowsOnNon2xx() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 500, "boom" );
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/pentaho";
      OAuthProviderService.fetchProviders( url );
    } finally {
      server.stop( 0 );
    }
  }

  @Test
  public void fetchProvidersHandlesEmptyJsonArray() throws Exception {
    com.sun.net.httpserver.HttpServer server = startServer( 200, "[]" );
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/pentaho/";
      List<OAuthProvider> result = OAuthProviderService.fetchProviders( url );
      assertTrue( result.isEmpty() );
    } finally {
      server.stop( 0 );
    }
  }

  private static com.sun.net.httpserver.HttpServer startServer( final int status, final String body )
    throws IOException {
    com.sun.net.httpserver.HttpServer server =
      com.sun.net.httpserver.HttpServer.create( new java.net.InetSocketAddress( "127.0.0.1", 0 ), 0 );
    server.createContext( "/pentaho/plugin/login/api/v0/oauth-providers", exchange -> {
      byte[] bytes = body.getBytes( java.nio.charset.StandardCharsets.UTF_8 );
      exchange.sendResponseHeaders( status, bytes.length );
      try ( java.io.OutputStream os = exchange.getResponseBody() ) {
        os.write( bytes );
      }
    } );
    server.start();
    return server;
  }
}
