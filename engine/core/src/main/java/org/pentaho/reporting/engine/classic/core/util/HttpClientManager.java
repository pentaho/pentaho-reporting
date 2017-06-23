/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2017 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

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
    private int connectionTimeout;
    private int socketTimeout;
    private HttpHost proxy;

    public HttpClientBuilderFacade setConnectionTimeout( int connectionTimeout ) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    public HttpClientBuilderFacade setSocketTimeout( int socketTimeout ) {
      this.socketTimeout = socketTimeout;
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
      requestConfigBuilder.build();

      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
      httpClientBuilder.setConnectionManager( manager );
      if ( provider != null ) {
        httpClientBuilder.setDefaultCredentialsProvider( provider );
      }
      if ( redirectStrategy != null ) {
        httpClientBuilder.setRedirectStrategy( redirectStrategy );
      }

      return httpClientBuilder.build();
    }
  }
}
