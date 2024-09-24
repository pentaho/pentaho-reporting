/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

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
  private AuthenticationData loginData;

  public UpdateReservedCharsTask( final AuthenticationData loginData ) {
    this.loginData = loginData;
  }

  public void setLoginData( AuthenticationData loginData, boolean storeUpdates ) {
    this.loginData = loginData;
  }

  private HttpClient createHttpClient() {
    HttpClientManager.HttpClientBuilderFacade clientBuilder = HttpClientManager.getInstance().createBuilder();
    HttpClient client =
      clientBuilder.setSocketTimeout( WorkspaceSettings.getInstance().getConnectionTimeout() * 1000 )
        .setCredentials( loginData.getUsername(), loginData.getPassword() ).setCookieSpec( CookieSpecs.DEFAULT )
        .build();

    return client;
  }

  private boolean checkResult( int result ) throws PublishException {
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

    final HttpGet reservedCharactersDisplayMethod =
      new HttpGet( loginData.getUrl() + "/api/repo/files/reservedCharactersDisplay" );

    try {
      HttpResponse httpResponse = client.execute( reservedCharactersMethod );
      final int result = httpResponse.getStatusLine().getStatusCode();
      if ( !checkResult( result ) ) {
        throw new PublishException( 1 );
      }
      PublishUtil.setReservedChars( HttpClientUtil.responseToString( httpResponse ) );
    } catch ( Exception e ) {
      throw new RuntimeException( e );
    }

    try {
      HttpResponse httpResponse = client.execute( reservedCharactersDisplayMethod );
      final int result = httpResponse.getStatusLine().getStatusCode();
      if ( !checkResult( result ) ) {
        throw new PublishException( 1 );
      }
      PublishUtil.setReservedCharsDisplay( HttpClientUtil.responseToString( httpResponse ) );
    } catch ( Exception e ) {
      throw new RuntimeException( e );
    }

  }
}
