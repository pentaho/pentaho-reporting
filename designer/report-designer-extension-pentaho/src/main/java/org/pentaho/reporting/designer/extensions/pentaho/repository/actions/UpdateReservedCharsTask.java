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



package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpGet;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishException;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;
import org.pentaho.reporting.engine.classic.core.util.HttpClientUtil;

public class UpdateReservedCharsTask implements AuthenticatedServerTask {
  private static final String SESSION_ID_OPTION = "sessionId";
  private static final String COOKIE_HEADER = "Cookie";
  private static final String SESSION_COOKIE_PREFIX = "JSESSIONID=";
  private static final String RESERVED_CHARS_ERROR = "Failed to update reserved characters";
  private static final String RESERVED_CHARS_DISPLAY_ERROR = "Failed to update reserved characters display";

  private AuthenticationData loginData;

  public UpdateReservedCharsTask( final AuthenticationData loginData ) {
    this.loginData = loginData;
  }

  public void setLoginData( AuthenticationData loginData, boolean storeUpdates ) {
    this.loginData = loginData;
  }

  private HttpClient createHttpClient() {
    HttpClientManager.HttpClientBuilderFacade clientBuilder = HttpClientManager.getInstance().createBuilder();
    clientBuilder.setSocketTimeout( WorkspaceSettings.getInstance().getConnectionTimeout() * 1000 )
      .setCookieSpec( CookieSpecs.DEFAULT );
    final String sessionId = loginData.getOption( SESSION_ID_OPTION );
    if ( sessionId == null || sessionId.isEmpty() ) {
      clientBuilder.setCredentials( loginData.getUsername(), loginData.getPassword() );
    }
    return clientBuilder.build();
  }

  boolean isSsoSession() {
    final String sessionId = loginData.getOption( SESSION_ID_OPTION );
    return sessionId != null && !sessionId.isEmpty();
  }

  void applySsoCookie( HttpGet request ) {
    if ( isSsoSession() ) {
      request.setHeader( COOKIE_HEADER, SESSION_COOKIE_PREFIX + loginData.getOption( SESSION_ID_OPTION ) );
    }
  }

  boolean checkResult( int result ) {
    return ( result == HttpStatus.SC_OK );
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    HttpClient client = createHttpClient();
    final HttpGet reservedCharactersMethod =
      new HttpGet( loginData.getUrl() + "/api/repo/files/reservedCharacters" );
    applySsoCookie( reservedCharactersMethod );

    final HttpGet reservedCharactersDisplayMethod =
      new HttpGet( loginData.getUrl() + "/api/repo/files/reservedCharactersDisplay" );
    applySsoCookie( reservedCharactersDisplayMethod );

    try {
      HttpResponse httpResponse = client.execute( reservedCharactersMethod );
      final int result = httpResponse.getStatusLine().getStatusCode();
      if ( !checkResult( result ) ) {
        throw new PublishException( 1 );
      }
      PublishUtil.setReservedChars( HttpClientUtil.responseToString( httpResponse ) );
    } catch ( PublishException | IOException e ) {
      throw new IllegalStateException( RESERVED_CHARS_ERROR, e );
    }

    try {
      HttpResponse httpResponse = client.execute( reservedCharactersDisplayMethod );
      final int result = httpResponse.getStatusLine().getStatusCode();
      if ( !checkResult( result ) ) {
        throw new PublishException( 1 );
      }
      PublishUtil.setReservedCharsDisplay( HttpClientUtil.responseToString( httpResponse ) );
    } catch ( PublishException | IOException e ) {
      throw new IllegalStateException( RESERVED_CHARS_DISPLAY_ERROR, e );
    }

  }
}
