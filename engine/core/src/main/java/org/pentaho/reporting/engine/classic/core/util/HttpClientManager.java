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
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

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

    /**
     * Sets a session cookie that is sent via a raw {@code Cookie} header on every
     * request, bypassing Apache HttpClient's cookie-spec domain validation.
     * <p>
     * Use this instead of {@link #setCookie(String, String, String)} when the
     * server is accessed by IP address, because the default cookie spec rejects
     * cookies whose domain is an IP address.
     *
     * @param name  cookie name (e.g. {@code JSESSIONID})
     * @param value cookie value
     * @return this builder for chaining
     */
    public HttpClientBuilderFacade setSessionCookie( String name, String value ) {
      this.sessionCookieHeader = name + "=" + value;
      return this;
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

      RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
      if ( socketTimeout > 0 ) {
        requestConfigBuilder.setSocketTimeout( socketTimeout );
      }
      if ( connectionTimeout > 0 ) {
        requestConfigBuilder.setConnectTimeout( socketTimeout );
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

      // RequestConfig built
      httpClientBuilder.setDefaultRequestConfig( requestConfigBuilder.build() );

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
        final String cookieHeaderValue = sessionCookieHeader;
        httpClientBuilder.addInterceptorFirst( new HttpRequestInterceptor() {
          @Override
          public void process( HttpRequest request, HttpContext context ) {
            request.setHeader( "Cookie", cookieHeaderValue );
          }
        } );
      }

      return httpClientBuilder.build();
    }
  }
}
