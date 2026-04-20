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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * Single entry point for all {@link org.apache.http.client.HttpClient HttpClient instances} usages in pentaho projects.
 * Contains {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager Connection pool} of 200 connections.
 * Maximum connections per one route is 100.
 * Provides inner builder class for creating {@link org.apache.http.client.HttpClient HttpClients}.
 *
 * @author Yury_Bakhmutski
 * @since 07-10-2017
 */
public class HttpClientManager {
  private static final int CONNECTIONS_PER_ROUTE = 100;
  private static final int TOTAL_CONNECTIONS = 200;

  private static HttpClientManager httpClientManager;
  private static PoolingHttpClientConnectionManager manager;

  private HttpClientManager() {
    manager = new PoolingHttpClientConnectionManager();
    manager.setDefaultMaxPerRoute( CONNECTIONS_PER_ROUTE );
    manager.setMaxTotal( TOTAL_CONNECTIONS );
  }

  public static HttpClientManager getInstance() {
    if ( httpClientManager == null ) {
      httpClientManager = new HttpClientManager();
    }
    return httpClientManager;
  }

  public CloseableHttpClient createDefaultClient() {
    return HttpClients.custom().setConnectionManager( manager )
      .build();
  }

  public HttpClientBuilderFacade createBuilder() {
    return new HttpClientBuilderFacade();
  }

  public class HttpClientBuilderFacade {
    private static final String COOKIE_HEADER = "Cookie";
    private RedirectStrategy redirectStrategy;
    private CredentialsProvider provider;
    private BasicCookieStore cookieStore;
    private int connectionTimeout;
    private int socketTimeout;
    private int maxRedirects;
    private String cookieSpec;
    private Boolean allowCircularRedirects = false;
    private Boolean rejectRelativeRedirect = true;
    private HttpHost proxy;
    private String sessionCookieHeader;

    public HttpClientBuilderFacade setConnectionTimeout( int connectionTimeout ) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    public HttpClientBuilderFacade setSocketTimeout( int socketTimeout ) {
      this.socketTimeout = socketTimeout;
      return this;
    }

    public HttpClientBuilderFacade allowCircularRedirects( ) {
      this.allowCircularRedirects = true;
      return this;
    }

    public HttpClientBuilderFacade allowRelativeRedirect( ) {
      this.rejectRelativeRedirect = false;
      return this;
    }

    public HttpClientBuilderFacade setMaxRedirects( int maxRedirects ) {
      this.maxRedirects = maxRedirects;
      return this;
    }

    public HttpClientBuilderFacade setCookieSpec( final String cookieSpec ) {
      this.cookieSpec = cookieSpec;
      return this;
    }

    public HttpClientBuilderFacade setCredentials( String user, String password, AuthScope authScope ) {
      CredentialsProvider provider = new BasicCredentialsProvider();
      UsernamePasswordCredentials credentials = new UsernamePasswordCredentials( user, password );
      provider.setCredentials( authScope, credentials );
      this.provider = provider;
      return this;
    }

    public HttpClientBuilderFacade setCredentials( String user, String password ) {
      return setCredentials( user, password, AuthScope.ANY );
    }

    public HttpClientBuilderFacade setCookie( String name, String value, String domain ) {
      if ( cookieStore == null ) {
        cookieStore = new BasicCookieStore();
      }
      BasicClientCookie cookie = new BasicClientCookie( name, value );
      cookie.setPath( "/" );
      if ( domain != null && !domain.isEmpty() ) {
        cookie.setDomain( domain );
      }
      cookieStore.addCookie( cookie );
      // Also set cookie spec to ensure cookies are sent
      if ( this.cookieSpec == null ) {
        this.cookieSpec = CookieSpecs.DEFAULT;
      }
      return this;
    }

    public HttpClientBuilderFacade setSessionCookie( String name, String value ) {
      if ( name == null || name.isEmpty() || value == null || value.isEmpty() ) {
        this.sessionCookieHeader = null;
        return this;
      }
      validateSessionCookiePart( name, "name" );
      validateSessionCookiePart( value, "value" );
      this.sessionCookieHeader = name + "=" + value;
      return this;
    }

    static void validateSessionCookiePart( String part, String partName ) {
      if ( part == null || part.isEmpty() ) {
        return;
      }
      for ( int i = 0; i < part.length(); i++ ) {
        char ch = part.charAt( i );
        if ( ch == '\r' || ch == '\n' || ch == ';' ) {
          throw new IllegalArgumentException( "Session cookie " + partName + " contains illegal character at index " + i );
        }
      }
    }

    public HttpClientBuilderFacade setProxy( String proxyHost, int proxyPort ) {
      setProxy( proxyHost, proxyPort, "http" );
      return this;
    }

    public HttpClientBuilderFacade setProxy( String proxyHost, int proxyPort, String scheme ) {
      this.proxy = new HttpHost( proxyHost, proxyPort, scheme );
      return this;
    }

    public HttpClientBuilderFacade setRedirect( RedirectStrategy redirectStrategy ) {
      this.redirectStrategy = redirectStrategy;
      return this;
    }

    public CloseableHttpClient build() {
      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
      httpClientBuilder.setConnectionManager( manager );

      httpClientBuilder.setDefaultRequestConfig( buildRequestConfig() );

      if ( provider != null ) {
        httpClientBuilder.setDefaultCredentialsProvider( provider );
      }
      if ( redirectStrategy != null ) {
        httpClientBuilder.setRedirectStrategy( redirectStrategy );
      }
      if ( cookieStore != null ) {
        httpClientBuilder.setDefaultCookieStore( cookieStore );
      }

      if ( sessionCookieHeader != null ) {
        httpClientBuilder.addInterceptorFirst( createSessionCookieInterceptor( sessionCookieHeader ) );
      }

      return httpClientBuilder.build();
    }

    // Package-private for direct unit testing.
    static HttpRequestInterceptor createSessionCookieInterceptor( final String cookieHeaderValue ) {
      return ( request, context ) -> {
        final org.apache.http.Header existingCookieHeader = request.getFirstHeader( COOKIE_HEADER );
        if ( existingCookieHeader == null || existingCookieHeader.getValue() == null
          || existingCookieHeader.getValue().isEmpty() ) {
          request.setHeader( COOKIE_HEADER, cookieHeaderValue );
        } else if ( !hasCookiePair( existingCookieHeader.getValue(), cookieHeaderValue ) ) {
          request.setHeader( COOKIE_HEADER, existingCookieHeader.getValue() + "; " + cookieHeaderValue );
        }
      };
    }

    // Package-private for direct unit testing.
    static boolean hasCookiePair( String cookieHeader, String pairToFind ) {
      // Parse cookie pairs to avoid false positives (e.g., JSESSIONID=1 matches JSESSIONID=12)
      String[] pairs = cookieHeader.split( ";" );
      for ( String pair : pairs ) {
        if ( pair.trim().equals( pairToFind ) ) {
          return true;
        }
      }
      return false;
    }

    private RequestConfig buildRequestConfig() {
      RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
      if ( socketTimeout > 0 ) {
        requestConfigBuilder.setSocketTimeout( socketTimeout );
      }
      if ( connectionTimeout > 0 ) {
        requestConfigBuilder.setConnectTimeout( connectionTimeout );
      }
      if ( proxy != null ) {
        requestConfigBuilder.setProxy( proxy );
      }
      if ( cookieSpec != null ) {
        requestConfigBuilder.setCookieSpec( cookieSpec );
      }
      if ( maxRedirects > 0 ) {
        requestConfigBuilder.setMaxRedirects( maxRedirects );
      }
      if ( allowCircularRedirects ) {
        requestConfigBuilder.setCircularRedirectsAllowed( true );
      }
      if ( !rejectRelativeRedirect ) {
        requestConfigBuilder.setRelativeRedirectsAllowed( true );
      }
      return requestConfigBuilder.build();
    }
  }
}
