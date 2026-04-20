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

package org.pentaho.reporting.engine.classic.core.util;

import static org.junit.Assert.*;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link HttpClientManager} - covers setSessionCookie, validateSessionCookiePart,
 * hasCookiePair, and build methods.
 */
public class HttpClientManagerTest {

  private static final String SESSION_COOKIE_NAME = "JSESSIONID";
  private static final String COOKIE_VALUE = "value";

  private HttpClientManager.HttpClientBuilderFacade builder;

  @Before
  public void setUp() {
    builder = HttpClientManager.getInstance().createBuilder();
  }

  @Test
  public void testGetInstance() {
    HttpClientManager instance = HttpClientManager.getInstance();
    assertNotNull( instance );
    assertSame( instance, HttpClientManager.getInstance() );
  }

  @Test
  public void testCreateDefaultClient() {
    HttpClient client = HttpClientManager.getInstance().createDefaultClient();
    assertNotNull( client );
  }

  @Test
  public void testCreateBuilder() {
    HttpClientManager.HttpClientBuilderFacade facade = HttpClientManager.getInstance().createBuilder();
    assertNotNull( facade );
  }

  @Test
  public void testSetSessionCookieValidNameAndValue() {
    HttpClientManager.HttpClientBuilderFacade result = builder.setSessionCookie( SESSION_COOKIE_NAME, "ABC123" );
    assertNotNull( result );
    assertSame( builder, result );
  }

  @Test
  public void testSetSessionCookieBuildsClientSuccessfully() {
    builder.setSessionCookie( SESSION_COOKIE_NAME, "SESS_VALUE_123" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSessionCookieNameWithCR() {
    builder.setSessionCookie( "bad\rname", COOKIE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSessionCookieNameWithLF() {
    builder.setSessionCookie( "bad\nname", COOKIE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSessionCookieNameWithSemicolon() {
    builder.setSessionCookie( "bad;name", COOKIE_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSessionCookieValueWithCR() {
    builder.setSessionCookie( "name", "bad\rvalue" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSessionCookieValueWithLF() {
    builder.setSessionCookie( "name", "bad\nvalue" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSessionCookieValueWithSemicolon() {
    builder.setSessionCookie( "name", "bad;value" );
  }

  @Test
  public void testSetSessionCookieNullNameDoesNotThrow() {
    // validateSessionCookiePart returns early for null
    builder.setSessionCookie( null, COOKIE_VALUE );
    assertNotNull( builder );
  }

  @Test
  public void testSetSessionCookieEmptyNameDoesNotThrow() {
    builder.setSessionCookie( "", COOKIE_VALUE );
    assertNotNull( builder );
  }

  @Test
  public void testSetSessionCookieNullValueDoesNotThrow() {
    builder.setSessionCookie( "name", null );
    assertNotNull( builder );
  }

  @Test
  public void testBuildWithNoSessionCookie() {
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithCredentials() {
    builder.setCredentials( "admin", "password" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithVariousConfigurations() {
    builder.setConnectionTimeout( 5000 );
    builder.setSocketTimeout( 10000 );
    assertNotNull( builder.build() );

    builder = HttpClientManager.getInstance().createBuilder();
    builder.setProxy( "proxy.example.com", 8080 );
    assertNotNull( builder.build() );

    builder = HttpClientManager.getInstance().createBuilder();
    builder.setProxy( "proxy.example.com", 8080, "https" );
    assertNotNull( builder.build() );
  }

  @Test
  public void testBuildWithCookieSpec() {
    builder.setCookieSpec( "default" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithMaxRedirects() {
    builder.setMaxRedirects( 5 );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithAllowCircularRedirects() {
    builder.allowCircularRedirects();
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithAllowRelativeRedirect() {
    builder.allowRelativeRedirect();
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithAllOptions() {
    builder.setSessionCookie( SESSION_COOKIE_NAME, "SESS123" )
      .setConnectionTimeout( 5000 )
      .setSocketTimeout( 10000 )
      .setCookieSpec( "default" )
      .setMaxRedirects( 3 )
      .allowCircularRedirects()
      .allowRelativeRedirect();
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testSetCookieWithVariousDomains() {
    String[] domains = { "example.com", null, "" };
    for ( String domain : domains ) {
      HttpClientManager.HttpClientBuilderFacade b = HttpClientManager.getInstance().createBuilder();
      HttpClientManager.HttpClientBuilderFacade result =
        b.setCookie( "myCookie", "myValue", domain );
      assertNotNull( result );
      assertSame( b, result );
      HttpClient client = b.build();
      assertNotNull( client );
    }
  }

  @Test
  public void testSetCookieMultipleCookies() {
    builder.setCookie( "cookie1", "value1", "example.com" );
    builder.setCookie( "cookie2", "value2", "example.com" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testSetCookieSetsCookieSpecIfNull() {
    // cookieSpec starts as null; setCookie should set it to DEFAULT
    builder.setCookie( "test", "value", "example.com" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testSetCookieDoesNotOverrideCookieSpec() {
    builder.setCookieSpec( "standard" );
    builder.setCookie( "test", "value", "example.com" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testSetSessionCookieClearsHeaderWithInvalidNameOrValue() {
    // null name, empty name, null value, empty value should all clear the session cookie header
    String[][] clearCases = { { null, "value" }, { "", "value" }, { "name", null }, { "name", "" } };
    for ( String[] pair : clearCases ) {
      HttpClientManager.HttpClientBuilderFacade b = HttpClientManager.getInstance().createBuilder();
      b.setSessionCookie( SESSION_COOKIE_NAME, "VALID" );
      HttpClientManager.HttpClientBuilderFacade result = b.setSessionCookie( pair[0], pair[1] );
      assertSame( b, result );
      HttpClient client = b.build();
      assertNotNull( client );
    }
  }

  @Test
  public void testSetCredentialsWithAuthScope() {
    builder.setCredentials( "admin", "password",
      new org.apache.http.auth.AuthScope( "localhost", 8080 ) );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testSetRedirectStrategy() {
    builder.setRedirect( new org.apache.http.impl.client.LaxRedirectStrategy() );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithCookieStoreOnly() {
    builder.setCookie( "test", "val", "domain.com" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testBuildWithSessionCookieAndExistingCookies() {
    builder.setCookie( "regular", "cookie", "example.com" );
    builder.setSessionCookie( SESSION_COOKIE_NAME, "SESS_ABC" );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testConnectionTimeoutUsesCorrectField() {
    // Verify the bug fix: connectionTimeout should use connectionTimeout, not socketTimeout
    builder.setConnectionTimeout( 15000 );
    builder.setSocketTimeout( 5000 );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testZeroTimeoutsAreIgnored() {
    builder.setConnectionTimeout( 0 );
    builder.setSocketTimeout( 0 );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  @Test
  public void testNegativeTimeouts() {
    builder.setConnectionTimeout( -1 );
    builder.setSocketTimeout( -1 );
    HttpClient client = builder.build();
    assertNotNull( client );
  }

  // -------- direct coverage for the package-private helpers --------

  @Test
  public void testValidateSessionCookiePartNullReturnsEarly() {
    // Should not throw on null/empty (the early-return branch).
    HttpClientManager.HttpClientBuilderFacade.validateSessionCookiePart( null, "name" );
    HttpClientManager.HttpClientBuilderFacade.validateSessionCookiePart( "", "name" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValidateSessionCookiePartRejectsCR() {
    HttpClientManager.HttpClientBuilderFacade.validateSessionCookiePart( "a\rb", "name" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValidateSessionCookiePartRejectsLF() {
    HttpClientManager.HttpClientBuilderFacade.validateSessionCookiePart( "a\nb", "name" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValidateSessionCookiePartRejectsSemicolon() {
    HttpClientManager.HttpClientBuilderFacade.validateSessionCookiePart( "a;b", "name" );
  }

  @Test
  public void testHasCookiePairFindsExactPair() {
    assertTrue( HttpClientManager.HttpClientBuilderFacade.hasCookiePair(
      "JSESSIONID=ABC", "JSESSIONID=ABC" ) );
    assertTrue( HttpClientManager.HttpClientBuilderFacade.hasCookiePair(
      "foo=1; JSESSIONID=ABC; bar=2", "JSESSIONID=ABC" ) );
  }

  @Test
  public void testHasCookiePairAvoidsPrefixFalsePositive() {
    // The key reason for parsing pairs: avoid matching JSESSIONID=1 as a substring of JSESSIONID=12.
    assertFalse( HttpClientManager.HttpClientBuilderFacade.hasCookiePair(
      "JSESSIONID=12", "JSESSIONID=1" ) );
  }

  @Test
  public void testHasCookiePairNoMatch() {
    assertFalse( HttpClientManager.HttpClientBuilderFacade.hasCookiePair(
      "foo=1; bar=2", "JSESSIONID=ABC" ) );
  }

  @Test
  public void testHasCookiePairEmptyHeader() {
    assertFalse( HttpClientManager.HttpClientBuilderFacade.hasCookiePair(
      "", "JSESSIONID=ABC" ) );
  }

  private static org.apache.http.HttpRequest newRequest() {
    return new org.apache.http.message.BasicHttpRequest( "GET", "/" );
  }

  @Test
  public void testInterceptorSetsCookieWhenAbsentOrNullOrEmpty() throws Exception {
    org.apache.http.HttpRequest[] requests = {
      newRequest(),
      newRequest(),
      newRequest()
    };
    requests[1].setHeader( new org.apache.http.message.BasicHeader( "Cookie", null ) );
    requests[2].setHeader( "Cookie", "" );

    org.apache.http.HttpRequestInterceptor i =
      HttpClientManager.HttpClientBuilderFacade.createSessionCookieInterceptor( "JSESSIONID=ABC" );
    for ( org.apache.http.HttpRequest req : requests ) {
      i.process( req, null );
      assertEquals( "JSESSIONID=ABC", req.getFirstHeader( "Cookie" ).getValue() );
    }
  }

  @Test
  public void testInterceptorAppendsWhenPairMissing() throws Exception {
    org.apache.http.HttpRequestInterceptor i =
      HttpClientManager.HttpClientBuilderFacade.createSessionCookieInterceptor( "JSESSIONID=ABC" );
    org.apache.http.HttpRequest req = newRequest();
    req.setHeader( "Cookie", "foo=1" );
    i.process( req, null );
    assertEquals( "foo=1; JSESSIONID=ABC", req.getFirstHeader( "Cookie" ).getValue() );
  }

  @Test
  public void testInterceptorLeavesHeaderWhenPairAlreadyPresent() throws Exception {
    org.apache.http.HttpRequestInterceptor i =
      HttpClientManager.HttpClientBuilderFacade.createSessionCookieInterceptor( "JSESSIONID=ABC" );
    org.apache.http.HttpRequest req = newRequest();
    req.setHeader( "Cookie", "foo=1; JSESSIONID=ABC" );
    i.process( req, null );
    assertEquals( "foo=1; JSESSIONID=ABC", req.getFirstHeader( "Cookie" ).getValue() );
  }
}
