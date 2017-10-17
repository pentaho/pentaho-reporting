/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pensol.vfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.pentaho.platform.util.StringUtil;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.pensol.LibPensolBoot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;

public class LocalFileModel extends XmlSolutionFileModel {
  private static final Log logger = LogFactory.getLog( LocalFileModel.class );

  private String url;
  private String username;
  private String password;
  private HttpClient client;
  private HttpClientContext context;

  @Deprecated
  public LocalFileModel( final String url,
                         final HttpClient client,
                         final String username,
                         final String password ) {
    if ( url == null ) {
      throw new NullPointerException();
    }
    this.url = url;
    this.username = username;
    this.password = password;
    this.client = client;
    this.client.getParams().setParameter( ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY );
    this.client.getParams().setParameter( ClientPNames.MAX_REDIRECTS, Integer.valueOf( 10 ) );
    this.client.getParams().setParameter( ClientPNames.ALLOW_CIRCULAR_REDIRECTS, Boolean.TRUE );
    this.client.getParams().setParameter( ClientPNames.REJECT_RELATIVE_REDIRECT, Boolean.FALSE );
    this.context = HttpClientContext.create();
  }

  /**
   * @deprecated use {@link LocalFileModel#LocalFileModel(java.lang.String,
   * org.pentaho.reporting.engine.classic.core.util.HttpClientManager.HttpClientBuilderFacade,
   * java.lang.String, java.lang.String, java.lang.String, int) }.
   */
  @Deprecated()
  public LocalFileModel( final String url,
                         final HttpClient client,
                         final String username,
                         final String password,
                         final String hostName,
                         int port ) {
    if ( url == null ) {
      throw new NullPointerException();
    }
    this.url = url;
    this.username = username;
    this.password = password;
    this.client = client;

    this.context = HttpClientContext.create();
    if ( !StringUtil.isEmpty( hostName ) ) {
      // Preemptive Basic Authentication
      HttpHost target = new HttpHost( hostName, port, "http" );
      // Create AuthCache instance
      AuthCache authCache = new BasicAuthCache();
      // Generate BASIC scheme object and add it to the local
      // auth cache
      BasicScheme basicAuth = new BasicScheme();
      authCache.put( target, basicAuth );
      // Add AuthCache to the execution context
      this.context.setAuthCache( authCache );
    }
    this.client.getParams().setParameter( ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY );
    this.client.getParams().setParameter( ClientPNames.MAX_REDIRECTS, Integer.valueOf( 10 ) );
    this.client.getParams().setParameter( ClientPNames.ALLOW_CIRCULAR_REDIRECTS, Boolean.TRUE );
    this.client.getParams().setParameter( ClientPNames.REJECT_RELATIVE_REDIRECT, Boolean.FALSE );
  }

  public LocalFileModel( final String url,
                         final HttpClientManager.HttpClientBuilderFacade clientBuilder,
                         final String username,
                         final String password,
                         final String hostName,
                         int port ) {

    if ( url == null ) {
      throw new NullPointerException();
    }
    this.url = url;
    this.username = username;
    this.password = password;

    this.context = HttpClientContext.create();
    if ( !StringUtil.isEmpty( hostName ) ) {
      // Preemptive Basic Authentication
      HttpHost target = new HttpHost( hostName, port, "http" );
      // Create AuthCache instance
      AuthCache authCache = new BasicAuthCache();
      // Generate BASIC scheme object and add it to the local
      // auth cache
      BasicScheme basicAuth = new BasicScheme();
      authCache.put( target, basicAuth );
      // Add AuthCache to the execution context
      this.context.setAuthCache( authCache );
    }
    clientBuilder.setCookieSpec( CookieSpecs.DEFAULT );
    clientBuilder.setMaxRedirects( 10 );
    clientBuilder.allowCircularRedirects();
    clientBuilder.allowRelativeRedirect();
  }

  public void refresh() throws IOException {
    getDescriptionEntries().clear();

    final Configuration configuration = LibPensolBoot.getInstance().getGlobalConfig();
    final String service =
      configuration.getConfigProperty( "org.pentaho.reporting.libraries.pensol.web.LoadRepositoryDoc" );

    URI uri;
    String baseUrl = url + service;
    try {
      URIBuilder builder = new URIBuilder( baseUrl );
      logger.debug( "Connecting to '" + baseUrl + '\'' );
      if ( username != null ) {
        builder.setParameter( "userid", username );
      }
      if ( password != null ) {
        builder.setParameter( "password", password );
      }
      uri = builder.build();
    } catch ( URISyntaxException e ) {
      throw new IOException( "Provided URL is invalid: " + baseUrl );
    }
    final HttpPost filePost = new HttpPost( uri );
    filePost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=utf-8" );

    HttpResponse httpResponse = client.execute( filePost, context );
    final int lastStatus = httpResponse.getStatusLine().getStatusCode();
    if ( lastStatus == HttpStatus.SC_UNAUTHORIZED ) {
      throw new IOException( "401: User authentication failed." );
    } else if ( lastStatus == HttpStatus.SC_NOT_FOUND ) {
      throw new IOException( "404: Repository service not found on server." );
    } else if ( lastStatus != HttpStatus.SC_OK ) {
      throw new IOException( "Server error: HTTP lastStatus code " + lastStatus );
    }

    final InputStream postResult = httpResponse.getEntity().getContent();
    try {
      setRoot( performParse( postResult ) );
    } finally {
      postResult.close();
    }
  }

  /**
   * @noinspection ThrowCaughtLocally
   */
  protected byte[] getDataInternally( final FileInfo fileInfo ) throws FileSystemException {
    URI uri;
    String baseUrl = fileInfo.getUrl();
    try {
      URIBuilder builder = new URIBuilder( baseUrl );
      logger.debug( "Connecting to '" + baseUrl + '\'' );
      if ( username != null ) {
        builder.setParameter( "userid", username );
      }
      if ( password != null ) {
        builder.setParameter( "password", password );
      }
      uri = builder.build();
    } catch ( URISyntaxException e ) {
      throw new FileSystemException( "Provided URL is invalid: " + baseUrl );
    }
    final HttpPost filePost = new HttpPost( uri );
    filePost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=utf-8" );

    try {
      HttpResponse httpResponse = client.execute( filePost, context );
      final int lastStatus = httpResponse.getStatusLine().getStatusCode();
      if ( lastStatus == HttpStatus.SC_UNAUTHORIZED ) {
        throw new FileSystemException( "401: User authentication failed." );
      } else if ( lastStatus == HttpStatus.SC_NOT_FOUND ) {
        throw new FileSystemException( "404: Repository service not found on server." );
      } else if ( lastStatus != HttpStatus.SC_OK ) {
        throw new FileSystemException( "Server error: HTTP lastStatus code " + lastStatus );
      }

      final InputStream postResult = httpResponse.getEntity().getContent();
      try {
        final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
        IOUtils.getInstance().copyStreams( postResult, bout );
        return bout.toByteArray();
      } finally {
        postResult.close();
      }
    } catch ( FileSystemException ioe ) {
      throw ioe;
    } catch ( IOException ioe ) {
      throw new FileSystemException( "Failed", ioe );
    }
  }

  public void createFolder( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );

    if ( fileName.length < 2 ) {
      throw new FileSystemException( "Cannot create directory in the root." );
    }

    final String[] parentPath = new String[ fileName.length - 1 ];
    System.arraycopy( fileName, 0, parentPath, 0, parentPath.length );
    final FileInfo fileInfo = lookupNode( parentPath );
    if ( fileInfo == null ) {
      throw new FileSystemException( "Cannot locate parent directory." );
    }

    try {
      final String solution = fileName[ 0 ];
      final String path = buildPath( fileName, 1, fileName.length - 1 );
      final String name = fileName[ fileName.length - 1 ];
      String description = getDescriptionEntries().get( file );
      if ( description == null ) {
        description = "";
      }
      final Configuration config = LibPensolBoot.getInstance().getGlobalConfig();
      final String urlMessage =
        config.getConfigProperty( "org.pentaho.reporting.libraries.pensol.web.CreateNewFolder" );
      final MessageFormat fmt = new MessageFormat( urlMessage );
      final String fullpath = fmt.format( new Object[] {
        URLEncoder.encode( solution, "UTF-8" ),
        URLEncoder.encode( path, "UTF-8" ),
        URLEncoder.encode( name, "UTF-8" ),
        URLEncoder.encode( description, "UTF-8" )
      } );

      URI uri;
      String baseUrl = url + fullpath;
      try {
        URIBuilder builder = new URIBuilder( baseUrl );
        logger.debug( "Connecting to '" + baseUrl + '\'' );
        if ( username != null ) {
          builder.setParameter( "user", username );
        }
        if ( password != null ) {
          builder.setParameter( "password", password );
        }
        uri = builder.build();
      } catch ( URISyntaxException e ) {
        throw new FileSystemException( "Provided URL is invalid: " + baseUrl );
      }
      final HttpPost filePost = new HttpPost( uri );
      filePost.setHeader( "Content-Type", "application/x-www-form-urlencoded; charset=utf-8" );

      HttpResponse httpResponse = client.execute( filePost, context );
      final int lastStatus = httpResponse.getStatusLine().getStatusCode();
      if ( lastStatus != HttpStatus.SC_OK ) {
        throw new FileSystemException( "Server error: HTTP status code " + lastStatus );
      }
      if ( name == null ) {
        throw new FileSystemException( "Error creating folder: Empty name" );
      }

      new FileInfo( fileInfo, name, description );
    } catch ( FileSystemException fse ) {
      throw fse;
    } catch ( IOException ioe ) {
      throw new FileSystemException( "Failed", ioe );
    }
  }

  private String buildPath( final String[] fileName, final int index, final int endIndex ) {
    final StringBuilder b = new StringBuilder( 100 );
    for ( int i = index; i < endIndex; i++ ) {
      if ( i != index ) {
        b.append( '/' );
      }
      b.append( fileName[ i ] );
    }
    return b.toString();
  }

  public long getContentSize( final FileName name ) throws FileSystemException {
    return 0;
  }

  protected void setDataInternally( final FileInfo fileInfo, final byte[] data ) throws FileSystemException {
    throw new FileSystemException( "Not supported" );
  }

  @Override
  public boolean delete( FileName name ) throws FileSystemException {
    throw new FileSystemException( "Not supported" );
  }
}
