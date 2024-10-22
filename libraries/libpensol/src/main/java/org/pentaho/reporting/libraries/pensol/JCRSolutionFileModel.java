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


package org.pentaho.reporting.libraries.pensol;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.http.HttpStatus;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileAdapter;
import org.pentaho.platform.util.RepositoryPathEncoder;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.URLEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class JCRSolutionFileModel implements SolutionFileModel {
  private static final Log logger = LogFactory.getLog( JCRSolutionFileModel.class );

  private static final String LOAD_REPOSITORY_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.LoadRepositoryDoc" );
  private static final String PARTIAL_LOADING_ENABLED = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.PartialLoading", Boolean.TRUE.toString() );
  private static final String LOAD_REPOSITORY_PARTIALLY_SERVICE =
    LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.libraries.pensol.jcr.LoadRepositoryDocPartially" );

  private static final String CREATE_FOLDER_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.CreateNewFolder" );
  private static final String DOWNLOAD_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.DownloadService" );
  private static final String UPLOAD_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.UploadService" );
  public static final String RETRIEVE_CONTENT_SERVICE = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.RetrieveContent" );
  public static final String RETRIEVE_PARAMETER_URL_SERVICE =
    LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.libraries.pensol.jcr.RetrieveParameters" );
  public static final String DELETE_FILE_OR_FOLDER = LibPensolBoot.getInstance().getGlobalConfig().getConfigProperty(
    "org.pentaho.reporting.libraries.pensol.jcr.delete.file.or.folder" );

  private static final String BI_SERVER_NULL_OBJECT =
    "BI-Server returned a RepositoryFileTreeDto without an attached RepositoryFileDto. "
    + "Please file a bug report at http://jira.pentaho.org/browse/BISERVER !";
  private static final String FILE_NOT_FOUND = "The specified file name does not exist: {0}";
  //this is required to retrieve a prpt - if true we get z ZIP file with .locale info
  private static final String SLASH = "/";

  // ":" --> "%3A"
  private static final String ENCODED_COLON = RepositoryPathEncoder.encodeURIComponent( ":" );

  private Client client;
  private String url;
  private RepositoryFileTreeDto root;
  private HashMap<FileName, String> descriptionEntries;
  private long refreshTime;
  private String majorVersion;
  private String minorVersion;
  private String releaseVersion;
  private String buildVersion;
  private String milestoneVersion;
  private final boolean loadTreePartially;

  public JCRSolutionFileModel( final String url,
                               final String username,
                               final String password,
                               final int timeout ) {
    if ( url == null ) {
      throw new NullPointerException();
    }
    this.url = url;
    descriptionEntries = new HashMap<FileName, String>();

    CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );

    final ClientConfig config = new ClientConfig();
    config.property( ClientProperties.FOLLOW_REDIRECTS, true );
    config.property( ClientProperties.READ_TIMEOUT, timeout );
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic( username, password );
    this.client = ClientBuilder.newClient( config ).register( feature );
    this.majorVersion = "999";
    this.minorVersion = "999";
    this.releaseVersion = "999";
    this.buildVersion = "999";
    this.milestoneVersion = "999";
    this.loadTreePartially = Boolean.parseBoolean( PARTIAL_LOADING_ENABLED );
  }

  // package-local constructor for testing purposes
  JCRSolutionFileModel( String url, Client client, boolean loadTreePartially ) {
    this.url = url;
    this.descriptionEntries = new HashMap<FileName, String>();
    this.client = client;
    this.loadTreePartially = loadTreePartially;
  }

  public void refresh() throws IOException {
    RepositoryFileTreeDto tree;
    if ( loadTreePartially ) {
      WebTarget target = client.target( url + LOAD_REPOSITORY_PARTIALLY_SERVICE );
      tree = target.path( "" ).request( MediaType.APPLICATION_XML_TYPE ).get( RepositoryFileTreeDto.class );
      tree = proxyRoot( tree );
    } else {
      WebTarget target = client.target( url + LOAD_REPOSITORY_SERVICE );
      tree = target.path( "" ).request( MediaType.APPLICATION_XML_TYPE ).get( RepositoryFileTreeDto.class );
    }
    setRoot( tree );
  }

  private RepositoryFileTreeDtoProxy proxyRoot( RepositoryFileTreeDto original ) {
    // save it, as proxy will replace the list with empty
    List<RepositoryFileTreeDto> originalChildren = original.getChildren();
    RepositoryFileTreeDtoProxy proxy = new RepositoryFileTreeDtoProxy( original, client, url );
    if ( originalChildren == null ) {
      proxy.setChildren( Collections.<RepositoryFileTreeDto>emptyList() );
    } else {
      // pre-populating root's direct children
      List<RepositoryFileTreeDto> proxiedChildren = new ArrayList<RepositoryFileTreeDto>( originalChildren.size() );
      for ( RepositoryFileTreeDto child : originalChildren ) {
        proxiedChildren.add( new RepositoryFileTreeDtoProxy( child, client, url ) );
      }
      proxy.setChildren( proxiedChildren );
    }
    return proxy;
  }

  public void createFolder( final FileName file ) throws FileSystemException {
    try {
      final String path = URLEncoder
        .encodeUTF8( URLDecoder.decode( normalizePath( file.getPath().replaceAll( "\\+", "%2B" ) ), "UTF-8" ) )
        .replaceAll( "\\!", "%21" );
      final String service = MessageFormat.format( CREATE_FOLDER_SERVICE, path );

      final WebTarget target = client.target( url + service );
      final Response response = target.request( MediaType.TEXT_PLAIN ).put( Entity.text( "" ) );
      if ( response.getStatus() == Response.Status.OK.getStatusCode() ) {
      refresh();
      } else {
        throw new FileSystemException( "Failed with error-code", response.getStatus() );
      }
    } catch ( FileSystemException fse ) {
      throw fse;
    } catch ( Exception e ) {
      throw new FileSystemException( "Failed", e );
    }
  }

  private static String normalizePath( final String path ) {
    return RepositoryPathEncoder.encodeRepositoryPath( path );
  }

  static String encodePathForRequest( String path ) {
    String pathEncoded = RepositoryPathEncoder.encodeRepositoryPath( path );
    String utf8 = RepositoryPathEncoder.encodeURIComponent( pathEncoded );
    // replace encoded colons with original
    return utf8.replaceAll( ENCODED_COLON, ":" );
  }

  public RepositoryFileTreeDto getRoot() throws IOException {
    if ( root == null && refreshTime == 0 ) {
      refresh();
    }

    return root;
  }

  public void setRoot( final RepositoryFileTreeDto root ) {
    if ( root == null ) {
      throw new NullPointerException();
    }

    this.descriptionEntries.clear();
    this.refreshTime = System.currentTimeMillis();
    this.root = root;
  }

  public boolean isDirectory( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    return fileDto.isFolder();
  }

  public boolean exists( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    return ( fileInfo != null );
  }

  public boolean isVisible( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    return !fileDto.isHidden();
  }

  public void setDescription( final FileName file, final String description ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    fileDto.setDescription( description );
  }

  public String getDescription( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    return fileDto.getDescription();
  }

  public long getLastModifiedDate( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }

    final String lastModifiedDateRaw = fileDto.getLastModifiedDate();
    if ( lastModifiedDateRaw.isEmpty() ) {
      // Folders have an empty lastModifiedDate field
      // Returning -1 here means that lastModifiedDate wasn't found
      return -1;
    }

    final Date lastModifiedDate = RepositoryFileAdapter.unmarshalDate( lastModifiedDateRaw );
    if ( lastModifiedDate == null ) {
      logger.error( "Repository returned <null> for last-modified-date on file: " + file );
      return -1;
    }
    return lastModifiedDate.getTime();
  }

  private String getFormattedServiceUrl( final String urlService, final FileName file ) throws FileSystemException {
    if ( urlService == null ) {
      throw new NullPointerException();
    }
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }

    final String restName = normalizePath( file.getPath() );
    return MessageFormat
      .format( urlService, URLEncoder.encodeUTF8( restName ).replaceAll( "\\!", "%21" ).replaceAll( "\\+", "%2B" ) );
  }

  public String getUrl( final FileName file ) throws FileSystemException {
    return getFormattedServiceUrl( RETRIEVE_CONTENT_SERVICE, file );
  }

  public List<RepositoryFileTreeDto> getChildren( final FileName parent ) throws FileSystemException {
    final String[] pathArray = computeFileNames( parent );
    final RepositoryFileTreeDto fileInfo = lookupNode( pathArray );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, parent );
    }

    final List<RepositoryFileTreeDto> childNodes = fileInfo.getChildren();
    return childNodes == null ? Collections.<RepositoryFileTreeDto>emptyList() : childNodes;
  }

  protected RepositoryFileTreeDto lookupNode( final String[] path ) throws FileSystemException {
    if ( root == null ) {
      try {
        refresh();
      } catch ( IOException e ) {
        throw new FileSystemException( e );
      }
    }
    if ( path.length == 0 ) {
      return root;
    }
    if ( "".equals( path[ 0 ] ) ) {
      if ( path.length == 1 ) {
        return root;
      }
    }
    RepositoryFileTreeDto element = root;
    for ( final String pathSegment : path ) {
      RepositoryFileTreeDto name = null;
      final List<RepositoryFileTreeDto> children = element.getChildren();
      if ( children == null ) {
        return null;
      }

      for ( final RepositoryFileTreeDto child : children ) {
        final RepositoryFileDto file = child.getFile();
        if ( file == null ) {
          throw new FileSystemException( BI_SERVER_NULL_OBJECT );
        }
        if ( pathSegment.equals( file.getName() ) ) {
          name = child;
          break;
        }
      }
      if ( name == null ) {
        return null;
      }
      element = name;
    }
    return element;
  }

  protected String[] computeFileNames( FileName file ) {
    final FastStack<String> stack = new FastStack<String>();
    while ( file != null ) {
      String name;
      try {
        name = URLDecoder.decode( file.getBaseName().trim().replaceAll( "\\+", "%2B" ), "UTF-8" );
      } catch ( UnsupportedEncodingException e ) {
        name = file.getBaseName().trim();
      }
      if ( !"".equals( name ) ) {
        stack.push( name );
      }
      file = file.getParent();
    }

    final int size = stack.size();
    final String[] result = new String[ size ];
    for ( int i = 0; i < result.length; i++ ) {
      result[ i ] = stack.pop();
    }
    return result;
  }

  public byte[] getData( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }

    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new IllegalStateException( BI_SERVER_NULL_OBJECT );
    }
    final String path = normalizePath( fileDto.getPath() );
    String urlPath = path;
    try {
      urlPath = URLEncoder.encodeUTF8( path ).replaceAll( "\\!", "%21" ).replaceAll( "\\+", "%2B" );
    } catch ( Exception ex ) {
    } //tcb
    final String service = MessageFormat.format( DOWNLOAD_SERVICE, urlPath );

    return client.target( url + service ).request( MediaType.APPLICATION_XML_TYPE ).get( byte[].class );
  }

  public void setData( final FileName file, final byte[] data ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final StringBuilder b = new StringBuilder();
    for ( int i = 0; i < fileName.length; i++ ) {
      if ( i != 0 ) {
        b.append( SLASH );
      }
      b.append( fileName[ i ] );
    }

    String service = MessageFormat.format( UPLOAD_SERVICE,
      URLEncoder.encodeUTF8( normalizePath( b.toString() ).replaceAll( "\\!", "%21" ).replaceAll( "\\+", "%2B" ) ) );
    final WebTarget target = client.target( url + service );
    final ByteArrayInputStream stream = new ByteArrayInputStream( data );
    Entity<InputStream> entity = Entity.entity( stream, MediaType.APPLICATION_OCTET_STREAM_TYPE );
    Response response = target.request().put( entity );
    int status = response.getStatus();

    if ( status != HttpStatus.SC_OK ) {
      if ( status == HttpStatus.SC_MOVED_TEMPORARILY
        || status == HttpStatus.SC_FORBIDDEN
        || status == HttpStatus.SC_UNAUTHORIZED ) {
        throw new FileSystemException( "ERROR_INVALID_USERNAME_OR_PASSWORD" );
      } else {
        throw new FileSystemException( "ERROR_FAILED", status );
      }
    }

    try {
      // Perhaps a New file was created. Refresh local tree model.
      this.refresh();
    } catch ( IOException e ) {
      // Ignore if unable to refresh
    }
  }

  public String[] getChilds( final FileName name ) throws FileSystemException {
    final List<RepositoryFileTreeDto> children = getChildren( name );
    final String[] childrenArray = new String[ children.size() ];
    for ( int i = 0; i < children.size(); i++ ) {
      final RepositoryFileTreeDto repositoryFileTreeDto = children.get( i );
      if ( repositoryFileTreeDto == null ) {
        continue;
      }

      final RepositoryFileDto file = repositoryFileTreeDto.getFile();
      if ( file == null ) {
        throw new FileSystemException( BI_SERVER_NULL_OBJECT );
      }
      childrenArray[ i ] =
        file.getName().replaceAll( "\\%", "%25" ).replaceAll( "\\!", "%21" ).replaceAll( "\\+", "%2B" );
    }
    return childrenArray;
  }

  public String getLocalizedName( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final RepositoryFileTreeDto fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, file );
    }
    final RepositoryFileDto fileDto = fileInfo.getFile();
    if ( fileDto == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    return fileDto.getTitle();
  }

  public String getParamServiceUrl( final FileName name ) throws FileSystemException {
    return url + getFormattedServiceUrl( RETRIEVE_PARAMETER_URL_SERVICE, name );
  }

  public long getRefreshTime() {
    return refreshTime;
  }

  public void setRefreshTime( final long refreshTime ) {
    this.refreshTime = refreshTime;
  }

  public HashMap<FileName, String> getDescriptionEntries() {
    return descriptionEntries;
  }

  public String getMajorVersion() {
    return majorVersion;
  }

  public String getMinorVersion() {
    return minorVersion;
  }

  public String getReleaseVersion() {
    return releaseVersion;
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public String getMilestoneVersion() {
    return milestoneVersion;
  }

  public long getContentSize( final FileName name ) throws FileSystemException {
    final String[] pathArray = computeFileNames( name );
    final RepositoryFileTreeDto fileInfo = lookupNode( pathArray );
    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, name );
    }
    final RepositoryFileDto file = fileInfo.getFile();
    if ( file == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    return file.getFileSize();
  }

  @Override
  public boolean delete( FileName name ) throws FileSystemException {

    boolean success = false;

    final RepositoryFileDto file = getFile( name );

    try {

      final WebTarget target = client.target( url + DELETE_FILE_OR_FOLDER );
      Entity<String> entity = Entity.entity( file.getId(), MediaType.TEXT_PLAIN );
      Response response = target.request().put( entity );

      if ( response != null && response.getStatus() == Response.Status.OK.getStatusCode() ) {
        refresh();
        success = true;
      } else {
        throw new FileSystemException( "Failed with error-code " + response.getStatus() );
      }

    } catch ( Exception e ) {
      throw new FileSystemException( "Failed", e );
    }

    return success;
  }

  private RepositoryFileDto getFile( FileName name ) throws FileSystemException {

    if ( name == null ) {
      throw new FileSystemException( FILE_NOT_FOUND );
    }

    final String[] pathArray = computeFileNames( name );
    final RepositoryFileTreeDto fileInfo = lookupNode( pathArray );

    if ( fileInfo == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, name );
    }

    final RepositoryFileDto file = fileInfo.getFile();

    if ( file == null ) {
      throw new FileSystemException( BI_SERVER_NULL_OBJECT );
    }
    return file;
  }
}
