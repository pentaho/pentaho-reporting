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

package org.pentaho.reporting.designer.core.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User: Martin Date: 03.03.2006 Time: 14:12:13
 */
public class ProxySettings {
  private class SettingsAuthenticator extends Authenticator {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication( getProxyUser(),
        getProxyPassword().toCharArray() );
    }
  }

  public static final String HTTP_DOT_PROXY_HOST = "http.proxyHost";
  public static final String HTTP_DOT_PROXY_PORT = "http.proxyPort";
  public static final String SOCKS_PROXY_HOST = "socksProxyHost";
  public static final String SOCKS_PROXY_PORT = "socksProxyPort";
  public static final String DEPLOYMENT_PROXY_HTTP_HOST = "deployment.proxy.http.host";
  public static final String DEPLOYMENT_PROXY_HTTP_PORT = "deployment.proxy.http.port";
  public static final String DEPLOYMENT_PROXY_SOCKS_HOST = "deployment.proxy.socks.host";
  public static final String DEPLOYMENT_PROXY_SOCKS_PORT = "deployment.proxy.socks.port";
  public static final String PROXY_PORT = "proxyPort";
  public static final String PROXY_HOST = "proxyHost";

  private static final Log logger = LogFactory.getLog( ProxySettings.class );
  private static ProxySettings instance;
  private static final String PROXY_TYPE_KEY = "ProxyType";
  private static final String PROXY_USER_KEY = "ProxyUser";
  private static final String PROXY_PASSWORD_KEY = "ProxyPassword";
  private static final String USE_SOCKS_PROXY_KEY = "UseSocksProxy";

  public static synchronized ProxySettings getInstance() {
    if ( instance == null ) {
      instance = new ProxySettings();
    }
    return instance;
  }

  private Preferences preferences;

  public ProxySettings() {
    preferences = Preferences.userRoot().node( "org/pentaho/reporting/designer/core/settings/proxy-settings" ); //NON-NLS
  }

  public void installAuthenticator() {
    Authenticator.setDefault( new SettingsAuthenticator() );
  }

  public ProxyType getProxyType() {
    final String unitText = preferences.get( PROXY_TYPE_KEY, ProxyType.AUTO_DETECT_PROXY.toString() );
    try {
      return ProxyType.valueOf( unitText );
    } catch ( Exception e ) {
      return null;
    }
  }

  public void setProxyType( final ProxyType proxyType ) {
    if ( proxyType == null ) {
      throw new IllegalArgumentException( "proxyType must not be null" );
    }

    preferences.put( PROXY_TYPE_KEY, String.valueOf( proxyType ) );
  }


  public String getHTTPProxyHost() {
    return preferences.get( HTTP_DOT_PROXY_HOST, System.getProperty( HTTP_DOT_PROXY_HOST, "" ) );
  }


  public void setHTTPProxyHost( final String httpProxyHost ) {
    //noinspection ConstantConditions
    if ( httpProxyHost == null ) {
      preferences.remove( HTTP_DOT_PROXY_HOST );
    } else {
      preferences.put( HTTP_DOT_PROXY_HOST, httpProxyHost );
    }
  }


  public int getHTTPProxyPort() {
    return preferences
      .getInt( HTTP_DOT_PROXY_PORT, ParserUtil.parseInt( System.getProperty( HTTP_DOT_PROXY_PORT ), -1 ) );
  }


  public void setHTTPProxyPort( final int httpProxyPort ) {
    preferences.putInt( HTTP_DOT_PROXY_PORT, httpProxyPort );
  }

  public String getProxyUser() {
    return preferences.get( PROXY_USER_KEY, "" );
  }

  public void setProxyUser( final String proxyUser ) {
    preferences.put( PROXY_USER_KEY, proxyUser );
  }


  public String getProxyPassword() {
    return preferences.get( PROXY_PASSWORD_KEY, "" );
  }


  public void setProxyPassword( final String proxyPassword ) {
    preferences.put( PROXY_PASSWORD_KEY, proxyPassword );
  }

  public boolean isUseSocksProxy() {
    return preferences.getBoolean( USE_SOCKS_PROXY_KEY, false );
  }

  public void setUseSocksProxy( final boolean useSocksProxy ) {
    preferences.putBoolean( USE_SOCKS_PROXY_KEY, useSocksProxy );
  }

  public int getSocksProxyPort() {
    return preferences.getInt( SOCKS_PROXY_PORT, ParserUtil.parseInt( System.getProperty( SOCKS_PROXY_PORT ), -1 ) );
  }

  public void setSocksProxyPort( final int socksProxyPort ) {
    preferences.putInt( SOCKS_PROXY_PORT, socksProxyPort );
  }


  public String getSocksProxyHost() {
    return preferences.get( SOCKS_PROXY_HOST, System.getProperty( SOCKS_PROXY_HOST, "" ) );
  }


  public void setSocksProxyHost( final String socksProxyHost ) {
    //noinspection ConstantConditions
    if ( socksProxyHost == null ) {
      preferences.remove( SOCKS_PROXY_HOST );
    } else {
      preferences.put( SOCKS_PROXY_HOST, socksProxyHost );
    }
  }


  public void applySettings() {
    try {
      System.clearProperty( HTTP_DOT_PROXY_HOST );
      System.clearProperty( HTTP_DOT_PROXY_PORT );
      System.clearProperty( SOCKS_PROXY_HOST );
      System.clearProperty( SOCKS_PROXY_PORT );

      switch ( getProxyType() ) {
        case AUTO_DETECT_PROXY: {
          final String host = getWebstartHTTPProxyHost();
          boolean httpProxySet = false;
          if ( host != null && host.trim().length() > 0 ) {
            System.setProperty( HTTP_DOT_PROXY_HOST, host );
            httpProxySet = true;
          }
          final String port = getWebstartHTTPProxyPort();
          if ( port != null ) {
            System.setProperty( HTTP_DOT_PROXY_PORT, port );
          }

          if ( !httpProxySet ) {
            final String socksHost = getWebstartSOCKSProxyHost();
            if ( StringUtils.isEmpty( socksHost, true ) == false ) {
              System.setProperty( SOCKS_PROXY_HOST, socksHost );
            }

            final String socksPort = getWebstartSOCKSProxyPort();
            if ( StringUtils.isEmpty( socksPort, true ) == false ) {
              System.setProperty( SOCKS_PROXY_PORT, socksPort );
            }
          }
          break;
        }
        case NO_PROXY: {
          break;
        }
        case USER_PROXY: {
          if ( isUseSocksProxy() ) {
            System.clearProperty( HTTP_DOT_PROXY_HOST );
            System.clearProperty( HTTP_DOT_PROXY_PORT );

            final String host = getSocksProxyHost();
            if ( StringUtils.isEmpty( host ) == false ) {
              System.setProperty( SOCKS_PROXY_HOST, host );
            }

            final int port = getSocksProxyPort();
            if ( port != -1 ) {
              System.setProperty( SOCKS_PROXY_PORT, String.valueOf( port ) );
            }
          } else {
            final String host = getHTTPProxyHost();
            if ( StringUtils.isEmpty( host ) == false ) {
              System.setProperty( HTTP_DOT_PROXY_HOST, host );
            }
            final int port = getHTTPProxyPort();
            if ( port != -1 ) {
              System.setProperty( HTTP_DOT_PROXY_PORT, String.valueOf( port ) );
            }
            System.clearProperty( SOCKS_PROXY_HOST );
            System.clearProperty( SOCKS_PROXY_PORT );
          }
          break;
        }
      }

    } catch ( Throwable t ) {
      logger.error( "Failed to configure proxy settings.", t );
    }
    printProxyConfiguration();
  }

  private void printProxyConfiguration() {
    logger.info( HTTP_DOT_PROXY_HOST + System.getProperty( HTTP_DOT_PROXY_HOST, "<undefined>" ) );
    logger.info( HTTP_DOT_PROXY_PORT + System.getProperty( HTTP_DOT_PROXY_PORT, "<undefined>" ) );
    logger.info( SOCKS_PROXY_HOST + System.getProperty( SOCKS_PROXY_HOST, "<undefined>" ) );
    logger.info( SOCKS_PROXY_PORT + System.getProperty( SOCKS_PROXY_PORT, "<undefined>" ) );

  }


  private static String getWebstartHTTPProxyHost() {
    final String host = System.getProperty( DEPLOYMENT_PROXY_HTTP_HOST );
    if ( host != null ) {
      return host;
    }

    return System.getProperty( PROXY_HOST );
  }


  private static String getWebstartHTTPProxyPort() {
    final String port = System.getProperty( DEPLOYMENT_PROXY_HTTP_PORT );
    if ( port != null ) {
      return port;
    }

    return System.getProperty( PROXY_PORT );
  }


  private static String getWebstartSOCKSProxyHost() {
    return System.getProperty( DEPLOYMENT_PROXY_SOCKS_HOST );
  }


  private static String getWebstartSOCKSProxyPort() {
    return System.getProperty( DEPLOYMENT_PROXY_SOCKS_PORT );
  }

  public void flush() {
    try {
      preferences.flush();
    } catch ( BackingStoreException e ) {
      e.printStackTrace();
    }
  }
}
