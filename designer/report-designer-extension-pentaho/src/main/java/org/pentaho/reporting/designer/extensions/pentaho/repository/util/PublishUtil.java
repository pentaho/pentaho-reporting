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


package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.URLEncoder;
import org.pentaho.reporting.libraries.pensol.PentahoSolutionsFileSystemConfigBuilder;
import org.pentaho.reporting.libraries.pensol.PublishRestUtil;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

public class PublishUtil {

  private static final String WEB_SOLUTION_PREFIX = "web-solution:";
  private static final String JCR_SOLUTION_PREFIX = "jcr-solution:";
  private static final String HTTP_SCHEME = "http://";
  private static final String HTTPS_SCHEME = "https://";
  public static final String SERVER_VERSION = "server-version";
  public static final int SERVER_VERSION_SUGAR = 5;
  public static final int SERVER_VERSION_LEGACY = 4;
  private static final int HTTP_RESPONSE_FAIL = 504; // RepresentS an unknown rest failure as this code
  private static final int HTTP_RESPONSE_OK = 200;

  private static final String TIMEOUT = "timeout";

  protected static String reservedChars = "/\\\t\r\n";

  protected static String reservedCharsDisplay = "/, \\, TAB, CR, LF";

  private static Pattern containsReservedCharsPattern = makePattern( reservedChars );

  private PublishUtil() {
  }

  public static ReportRenderContext openReport( final ReportDesignerContext context,
      final AuthenticationData loginData, final String path ) throws IOException, ReportDataFactoryException, ResourceException {
    if ( StringUtils.isEmpty( path ) ) {
      throw new IOException( "Path is empty." );
    }

    final String urlPath = path.replace( "%", "%25" ).replace( "%2B", "+" ).replace( "!", "%21" ).replace( ":", "%3A" );
    final FileObject connection = createVFSConnection( loginData );
    final FileObject object = connection.resolveFile( urlPath );
    if ( !object.exists() ) {
      throw new FileNotFoundException( path );
    }

    try ( InputStream inputStream = object.getContent().getInputStream();
          ByteArrayOutputStream out = new ByteArrayOutputStream( Math.max( 8192, (int) object.getContent().getSize() ) ) ) {
      IOUtils.getInstance().copyStreams( inputStream, out );
      final MasterReport report = loadReport( out.toByteArray(), path );
      final int index = context.addMasterReport( report );
      return context.getReportRenderContext( index );
    }
  }

  private static MasterReport loadReport( final byte[] data, final String fileName ) throws IOException,
    ResourceException {
    if ( data == null ) {
      throw new NullPointerException();
    }
    final ResourceManager resourceManager = new ResourceManager();
    final MasterReport resource = OpenReportAction.loadReport( data, resourceManager );
    resource.setAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path", fileName ); // NON-NLS
    return resource;
  }

  public static void launchReportOnServer( final String baseUrl, final String path ) throws IOException {

    if ( StringUtils.isEmpty( path ) ) {
      throw new IOException( "Path is empty." );
    }

    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    final String urlMessage = config.getConfigProperty( "org.pentaho.reporting.designer.extensions.pentaho.repository.LaunchReport" );

    final String fullRepoViewerPath =  MessageFormat.format( urlMessage, URLEncoder.encode( RepositoryPathEncoder.encodeRepositoryPath( path ), "UTF-8" ) );
    final String url = baseUrl + fullRepoViewerPath;

    ExternalToolLauncher.openURL( url );
  }

  public static byte[] createBundleData( final MasterReport report ) throws BundleWriterException {
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BundleWriter.writeReportToZipStream( report, outputStream );
      return outputStream.toByteArray();
    } catch ( final ContentIOException | IOException e ) {
      throw new BundleWriterException( "Failed to write report", e );
    }
  }

  public static int publish( final byte[] data, final String path, final AuthenticationData loginData, final Properties fileProperties ) throws IOException {
    int responseCode = HTTP_RESPONSE_FAIL;
    final String versionText = loginData.getOption( SERVER_VERSION );
    final int version = ParserUtil.parseInt( versionText, SERVER_VERSION_SUGAR );

    if ( SERVER_VERSION_SUGAR == version ) {
      Properties propertiesForPublish = fileProperties;
      if ( propertiesForPublish == null ) {
        propertiesForPublish = new Properties();
      }
      //Force overwrite flag here so that the server does not fail with an error in case the report already exists in the JCR
      propertiesForPublish.setProperty( PublishRestUtil.OVERWRITE_FILE_KEY, Boolean.TRUE.toString() );

      PublishRestUtil publishRestUtil = new PublishRestUtil( loginData.getUrl(), loginData.getUsername(),
          loginData.getPassword(), loginData.getOption( "sessionId" ) );
      responseCode = publishRestUtil.publishFile( path, data, propertiesForPublish );
    } else {
      final FileObject connection = createVFSConnection( loginData );
      final FileObject object = connection.resolveFile( path );
      final OutputStream out = object.getContent().getOutputStream( false );
      try {
        out.write( data );
        responseCode = HTTP_RESPONSE_OK;
      } finally {
        out.close();
      }
    }
    return responseCode;
  }

  /**
   * We need to keep the options of report. please use {@link #publish(byte[], String, AuthenticationData, Properties)}
   * We keep the method for backward compatibility.
   *
   * @deprecated since 8.1, use {@link #publish(byte[], String, AuthenticationData, Properties)} instead.
   */
  @Deprecated( since = "8.1", forRemoval = false )
  public static int publish( final byte[] data, final String path, final AuthenticationData loginData ) throws IOException {
    return publish( data, path, loginData, new Properties() );
  }

  public static boolean acceptFilter( final String[] filters, final String name ) {
    if ( filters == null || filters.length == 0 ) {
      return true;
    }
    for ( int i = 0; i < filters.length; i++ ) {
      if ( name.endsWith( filters[i] ) ) {
        return true;
      }
    }
    return false;
  }

  public static FileObject createVFSConnection( final AuthenticationData loginData ) throws FileSystemException {
    return createVFSConnection( VFS.getManager(), loginData );
  }

  public static FileObject createVFSConnection( final FileSystemManager fileSystemManager,
      final AuthenticationData loginData ) throws FileSystemException {
    if ( fileSystemManager == null ) {
      throw new NullPointerException();
    }
    if ( loginData == null ) {
      throw new NullPointerException();
    }

    final String versionText = loginData.getOption( SERVER_VERSION );
    final int version = ParserUtil.parseInt( versionText, SERVER_VERSION_SUGAR );

    final String normalizedUrl = normalizeURL( loginData.getUrl(), version );
    final FileSystemOptions fileSystemOptions = new FileSystemOptions();
    final PentahoSolutionsFileSystemConfigBuilder configBuilder = new PentahoSolutionsFileSystemConfigBuilder();
    configBuilder.setTimeOut( fileSystemOptions, getTimeout( loginData ) * 1000 );
    configBuilder.setUserAuthenticator( fileSystemOptions, new StaticUserAuthenticator( normalizedUrl, loginData
        .getUsername(), loginData.getPassword() ) );

    final String sessionId = loginData.getOption( "sessionId" );
    if ( sessionId != null && !sessionId.isEmpty() ) {
      configBuilder.setSessionId( fileSystemOptions, sessionId );
    }

    return fileSystemManager.resolveFile( normalizedUrl, fileSystemOptions );
  }

  public static int getTimeout( final AuthenticationData loginData ) {
    final String s = loginData.getOption( TIMEOUT );
    return ParserUtil.parseInt( s, WorkspaceSettings.getInstance().getConnectionTimeout() );
  }

  public static String normalizeURL( final String baseURL, final int version ) {
    if ( baseURL == null ) {
      throw new NullPointerException();
    }
    final StringBuilder prefix = new StringBuilder( 100 );
    final String url2;
    final String lowerUrl = baseURL.toLowerCase( Locale.ENGLISH );
    if ( version == SERVER_VERSION_LEGACY ) {
      if ( lowerUrl.startsWith( HTTP_SCHEME ) ) {  // NON-NLS
        url2 = baseURL.substring( HTTP_SCHEME.length() ); // NON-NLS
        prefix.append( WEB_SOLUTION_PREFIX );
        prefix.append( HTTP_SCHEME ); // NON-NLS
      } else if ( lowerUrl.startsWith( HTTPS_SCHEME ) ) {  // NON-NLS
        url2 = baseURL.substring( HTTPS_SCHEME.length() );  // NON-NLS
        prefix.append( WEB_SOLUTION_PREFIX );
        prefix.append( HTTPS_SCHEME );  // NON-NLS
      } else {
        throw new IllegalArgumentException( "Not a expected URL" );
      }
    } else {
      if ( lowerUrl.startsWith( HTTP_SCHEME ) ) {  // NON-NLS
        url2 = baseURL.substring( HTTP_SCHEME.length() ); // NON-NLS
        prefix.append( JCR_SOLUTION_PREFIX );
        prefix.append( HTTP_SCHEME ); // NON-NLS
      } else if ( lowerUrl.startsWith( HTTPS_SCHEME ) ) {  // NON-NLS
        url2 = baseURL.substring( HTTPS_SCHEME.length() );  // NON-NLS
        prefix.append( JCR_SOLUTION_PREFIX );
        prefix.append( HTTPS_SCHEME );  // NON-NLS
      } else {
        throw new IllegalArgumentException( "Not a expected URL" );
      }
    }
    return prefix.append( url2 ).toString();
  }

  private static Pattern makePattern( String reservedChars ) {
    // escape all reserved characters as they may have special meaning to regex engine
    StringBuilder buf = new StringBuilder();
    buf.append( ".*[" ); //$NON-NLS-1$
    for ( int i = 0; i < reservedChars.length(); i++ ) {
      buf.append( "\\" ); //$NON-NLS-1$
      buf.append( reservedChars.substring( i, i + 1 ) );
    }
    buf.append( "]+.*" ); //$NON-NLS-1$
    return Pattern.compile( buf.toString() );
  }

  /**
   * Checks for presence of black listed chars as well as illegal permutations of legal chars.
   */
  public static boolean validateName( final String name ) {
    return !StringUtils.isEmpty( name, true ) && name.trim().equals( name ) && // no leading or trailing whitespace
        !containsReservedCharsPattern.matcher( name ).matches() && // no reserved characters
        !".".equals( name ) && // no . //$NON-NLS-1$
        !"..".equals( name ); // no .. //$NON-NLS-1$
  }

  public static void setReservedChars( String reservedChars ) {
    containsReservedCharsPattern = makePattern( reservedChars );
  }

  public static Pattern getPattern() {
    return containsReservedCharsPattern;
  }

  public static String getReservedCharsDisplay() {
    return reservedCharsDisplay;
  }

  public static void setReservedCharsDisplay( String reservedCharsDisplay ) {
    PublishUtil.reservedCharsDisplay = reservedCharsDisplay;
  }

  /**
   * Returns {@code true} if the exception indicates an authentication or
   * authorization failure (HTTP 401/403).
   */
  public static boolean isAuthenticationError( final Exception e ) {
    Throwable current = e;
    while ( current != null ) {
      final String msg = current.getMessage();
      if ( msg != null ) {
        final String lower = msg.toLowerCase( Locale.ENGLISH );
        if ( lower.contains( "invalid username or password" )
            || lower.contains( "unauthorized" )
            || lower.contains( "status code 401" )
            || lower.contains( "status code 403" ) ) {
          return true;
        }
      }
      if ( current == current.getCause() ) {
        break;
      }
      current = current.getCause();
    }
    return false;
  }

}
