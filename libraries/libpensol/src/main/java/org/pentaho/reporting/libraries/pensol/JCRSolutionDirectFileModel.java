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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.http.HttpStatus;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
import org.pentaho.platform.web.http.api.resources.DirectoryResource;
import org.pentaho.platform.web.http.api.resources.FileResource;
import org.pentaho.platform.web.http.api.resources.RepositoryImportResource;
import org.pentaho.platform.repository2.unified.webservices.RepositoryFileAdapter;
import org.pentaho.reporting.libraries.base.util.FastStack;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Marco Vala
 */
public class JCRSolutionDirectFileModel implements SolutionFileModel {
  private static final Log logger = LogFactory.getLog( JCRSolutionDirectFileModel.class );

  private static final String SLASH = "/";
  private static final String COLON = ":";
  private static final String DO_GET_ROOT_CHILDREN = "doGetRootChildren";
  private static final String DO_GET_FILE_OR_DIR_AS_DOWNLOAD = "doGetFileOrDirAsDownload";
  private static final String FAILED_TO_ACCESS_REPOSITORY = "Failed to access repository";
  private static final String FILE_NOT_FOUND = "File does not exist: {0}";
  private static final String FAILED_TO_WRITE_FILE = "Failed to write file: {0}";
  private static final String NULL_OBJECT = "Repository returned <null> for file: {0}";
  private static final String NULL_MODIFIED_DATE = "Repository returned <null> for last-modified-date on file: ";
  private static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
  private static final String BAD_RESPONSE = "Failed with error-code: {0}";
  private static final String NOT_SUPPORTED = "Operation not supported: {0}";

  private RepositoryFileTreeDto root;
  private FileResource fileRes;
  private DirectoryResource dirRes;
  private RepositoryImportResource importRes;
  private Method doGetRootChildrenMethod;
  private Method doGetFileOrDirAsDownloadMethod;


  public JCRSolutionDirectFileModel() {
    this.fileRes = new FileResource();
    this.dirRes = new DirectoryResource();
    this.importRes = new RepositoryImportResource();

    // check if [5.0-SNAPSHOT] backports are needed
    try {
      /*
      [5.0-SNAPSHOT]
      /api/repo/files/children?depth=-1&filter=*&showHidden=true
      public RepositoryFileTreeDto doGetRootChildren(
        @QueryParam("depth") Integer depth,
        @QueryParam("filter") String filter,
        @QueryParam("showHidden") Boolean showHidden )
      */
      this.doGetRootChildrenMethod = this.fileRes.getClass().getDeclaredMethod( DO_GET_ROOT_CHILDREN,
        Integer.class, String.class, Boolean.class );
    } catch ( NoSuchMethodException e ) {
      this.doGetRootChildrenMethod = null;
    }
    try {
      /*
      [5.0-SNAPSHOT]
      /api/repo/files/{0}/download?withManifest=false
      public Response doGetFileOrDirAsDownload(
        @PathParam("pathId") String pathId,
        @QueryParam("withManifest") String strWithManifest )
      */
      this.doGetFileOrDirAsDownloadMethod = this.fileRes.getClass().getDeclaredMethod( DO_GET_FILE_OR_DIR_AS_DOWNLOAD,
        String.class, String.class );
    } catch ( NoSuchMethodException e ) {
      this.doGetFileOrDirAsDownloadMethod = null;
    }
  }

  ////
  //// Repository access
  ////

  private RepositoryFileTreeDto getFileTree() throws FileSystemException {
    /*
    /api/repo/files/tree?depth=-1&filter=*&showHidden=true
    public RepositoryFileTreeDto doGetRootTree(
      @QueryParam( "depth" ) Integer depth,
      @QueryParam( "filter" ) String filter,
      @QueryParam( "showHidden" ) Boolean showHidden,
      @DefaultValue( "false" ) @QueryParam( "includeAcls" ) Boolean includeAcls )
    */

    final int depth = -1;
    final String filter = "*";
    final boolean showHidden = true;

    RepositoryFileTreeDto fileTree = null;
    if ( this.doGetRootChildrenMethod == null ) {
      fileTree = this.fileRes.doGetRootTree( depth, filter, showHidden, false );
    } else {
      // apply [5.0-SNAPSHOT] backport
      try {
        fileTree =
          (RepositoryFileTreeDto) this.doGetRootChildrenMethod.invoke( this.fileRes, depth, filter, showHidden );
      } catch ( InvocationTargetException e ) {
        throw new FileSystemException( FAILED_TO_ACCESS_REPOSITORY );
      } catch ( IllegalAccessException e ) {
        throw new FileSystemException( FAILED_TO_ACCESS_REPOSITORY );
      }
    }
    return fileTree;
  }

  private RepositoryFileTreeDto getTreeNode( FileName fullName ) throws FileSystemException {
    if ( this.root == null ) {
      refresh();
    }
    return searchTreeNode( stackName( fullName ), this.root );
  }

  private RepositoryFileTreeDto searchTreeNode( FastStack<String> fullName, RepositoryFileTreeDto treeNode ) {
    // no more name parts to search, found file in subtree
    if ( fullName.size() == 0 ) {
      return treeNode;
    }

    // search recursively for each name part in subtree
    final String fileOrDirName = fullName.peek();
    final List<RepositoryFileTreeDto> children = treeNode.getChildren();
    for ( final RepositoryFileTreeDto child : children ) {
      final String childName = child.getFile().getName();
      if ( fileOrDirName.equals( childName ) ) {
        fullName.pop();
        return searchTreeNode( fullName, child );
      }
    }

    // didn't find file
    return null;
  }

  private FastStack<String> stackName( FileName fullName ) {
    final FastStack<String> stack = new FastStack<String>();
    while ( fullName != null ) {
      final String name = fullName.getBaseName().trim();
      if ( !name.equals( "" ) ) {
        stack.push( name );
      }
      fullName = fullName.getParent();
    }
    return stack;
  }

  private RepositoryFileDto getFile( FileName fullName ) throws FileSystemException {
    final RepositoryFileTreeDto tree = getTreeNode( fullName );
    if ( tree == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, fullName );
    }
    return tree.getFile();
  }

  private List<RepositoryFileTreeDto> getChildNodes( final FileName fullName ) throws FileSystemException {
    final RepositoryFileTreeDto tree = getTreeNode( fullName );
    if ( tree == null ) {
      throw new FileSystemException( FILE_NOT_FOUND, fullName );
    }
    final List<RepositoryFileTreeDto> children = tree.getChildren();
    return children == null ? Collections.<RepositoryFileTreeDto>emptyList() : children;
  }

  private String pathToId( String path ) {
    return path.replace( SLASH, COLON );
  }

  private void throwExceptionOnBadResponse( Response response ) throws FileSystemException {
    final int status = response.getStatus();
    switch ( status ) {
      case HttpStatus.SC_OK:
        logger.debug( "OK" );
        // response OK => do not throw exception and continue execution
        break;

      case HttpStatus.SC_UNAUTHORIZED:
      case HttpStatus.SC_FORBIDDEN:
      case HttpStatus.SC_MOVED_TEMPORARILY:
        logger.debug( "FORBIDDEN" );
        throw new FileSystemException( INVALID_USERNAME_OR_PASSWORD );

      default:
        logger.debug( "ERROR " + status );
        throw new FileSystemException( BAD_RESPONSE, status );
    }
  }

  ////
  //// SolutionFileModel implementation
  ////

  @Override
  public void refresh() throws FileSystemException {
    logger.debug( "refresh" );

    this.root = getFileTree();
  }

  @Override
  public String[] getChilds( final FileName fullName ) throws FileSystemException {
    logger.debug( "getChilds: " + fullName );

    final List<RepositoryFileTreeDto> children = getChildNodes( fullName );
    logger.debug( "size = " + children.size() );

    final String[] childrenArray = new String[ children.size() ];
    for ( int i = 0; i < children.size(); i++ ) {
      final RepositoryFileTreeDto treeNode = children.get( i );
      if ( treeNode != null ) {
        final RepositoryFileDto file = treeNode.getFile();
        if ( file != null ) {
          logger.debug( "file " + file.getName() );
          childrenArray[ i ] = file.getName();
        } else {
          throw new FileSystemException( NULL_OBJECT );
        }
      }
    }
    return childrenArray;
  }

  @Override
  public boolean exists( final FileName fullName ) throws FileSystemException {
    return ( getTreeNode( fullName ) != null );
  }

  @Override
  public boolean isVisible( final FileName fullName ) throws FileSystemException {
    return !getFile( fullName ).isHidden();
  }

  @Override
  public boolean isDirectory( final FileName fullName ) throws FileSystemException {
    return getFile( fullName ).isFolder();
  }

  @Override
  public long getLastModifiedDate( final FileName fullName ) throws FileSystemException {
    final Date lastModifiedDate = RepositoryFileAdapter.unmarshalDate( getFile( fullName ).getLastModifiedDate() );
    if ( lastModifiedDate == null ) {
      logger.error( NULL_MODIFIED_DATE + fullName );
      return -1;
    }
    return lastModifiedDate.getTime();
  }

  @Override
  public String getLocalizedName( final FileName fullName ) throws FileSystemException {
    return getFile( fullName ).getTitle();
  }

  @Override
  public void setDescription( final FileName fullName, final String description ) throws FileSystemException {
    getFile( fullName ).setDescription( description );
  }

  @Override
  public String getDescription( final FileName fullName ) throws FileSystemException {
    return getFile( fullName ).getDescription();
  }

  @Override
  public long getContentSize( final FileName fullName ) throws FileSystemException {
    return getFile( fullName ).getFileSize();
  }

  @Override
  public byte[] getData( final FileName fullName ) throws FileSystemException {
    /*
    /api/repo/files/{0}/download?withManifest=false
    public Response doGetFileOrDirAsDownloadMethod(
      @PathParam("pathId") String pathId,
      @QueryParam("withManifest") String strWithManifest )
    */
    logger.debug( "getData: " + fullName );

    try {
      final String fileId = pathToId( fullName.getPath() );
      Response response = null;
      if ( this.doGetFileOrDirAsDownloadMethod == null ) {
        response = this.fileRes.doGetFileOrDirAsDownload( "", fileId, "false" );
      } else {
        // apply [5.0-SNAPSHOT] backport
        try {
          response = (Response) this.doGetFileOrDirAsDownloadMethod.invoke( this.fileRes, fileId, "false" );
        } catch ( InvocationTargetException e ) {
          throw new FileSystemException( FAILED_TO_ACCESS_REPOSITORY );
        } catch ( IllegalAccessException e ) {
          throw new FileSystemException( FAILED_TO_ACCESS_REPOSITORY );
        }
      }
      throwExceptionOnBadResponse( response );
      StreamingOutput output = (StreamingOutput) response.getEntity();
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      output.write( stream );
      return stream.toByteArray();
    } catch ( FileNotFoundException e ) {
      throw new FileSystemException( FILE_NOT_FOUND, fullName );
    } catch ( IOException e ) {
      throw new FileSystemException( FAILED_TO_WRITE_FILE, fullName );
    }
  }

  @Override
  public void setData( final FileName fullName, final byte[] data ) throws FileSystemException {
    /*
    /api/repo/files/import
    public Response doPostImport(
      @FormDataParam("importDir") String uploadDir,
      @FormDataParam("fileUpload") InputStream fileIS,
      @FormDataParam("overwriteFile") String overwriteFile,
      @FormDataParam("overwriteAclPermissions") String overwriteAclPermissions,
      @FormDataParam("applyAclPermissions") String applyAclPermission,
      @FormDataParam("retainOwnership") String retainOwnership,
      @FormDataParam("charSet") String charSet,
      @FormDataParam("logLevel") String logLevel,
      @FormDataParam("fileUpload") FormDataContentDisposition fileInfo,
      @FormDataParam("fileNameOverried) String fileNameOveride )
    */
    logger.debug( "setData: " + fullName );

    final String name = fullName.getBaseName();
    final String parent = fullName.getParent().getPath();
    final ByteArrayInputStream stream = new ByteArrayInputStream( data );
    final FormDataContentDisposition fd = FormDataContentDisposition
      .name( name )
      .fileName( name )
      .build();
    Response response =
      this.importRes.doPostImport( parent, stream, "true", null, "true", "true", null, "WARN", fd, null );
    throwExceptionOnBadResponse( response );
  }

  @Override
  public void createFolder( final FileName fullName ) throws FileSystemException {
    /*
    /api/repo/dirs/{0}
    public Response createDirs(
      @PathParam( "pathId" ) String pathId )
    */
    logger.debug( "createFolder: " + fullName );

    Response response = this.dirRes.createDirs( pathToId( fullName.getPath() ) );
    throwExceptionOnBadResponse( response );
    refresh();
  }

  @Override
  public boolean delete( final FileName fullName ) throws FileSystemException {
    /*
    /api/repo/files/delete
    public Response doDeleteFiles(String params)
     */
    logger.debug( "delete: " + fullName );

    Response response = fileRes.doDeleteFiles( getFile( fullName ).getId() );
    throwExceptionOnBadResponse( response );
    refresh();
    return true;
  }

  @Override public String getParamServiceUrl( FileName fullName ) throws FileSystemException {
    throw new FileSystemException( NOT_SUPPORTED, "getParamServiceUrl" );
  }

  @Override public String getUrl( FileName fullName ) throws FileSystemException {
    throw new FileSystemException( NOT_SUPPORTED, "getUrl" );
  }
}
