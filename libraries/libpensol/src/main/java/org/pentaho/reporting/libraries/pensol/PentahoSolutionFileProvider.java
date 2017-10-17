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

package org.pentaho.reporting.libraries.pensol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.commons.vfs2.provider.LayeredFileName;
import org.apache.commons.vfs2.provider.LayeredFileNameParser;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.util.StringUtil;
import org.pentaho.reporting.engine.classic.core.util.HttpClientManager;
import org.pentaho.reporting.libraries.pensol.vfs.LocalFileModel;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class PentahoSolutionFileProvider extends AbstractOriginatingFileProvider {
  private static final Log logger = LogFactory.getLog( PentahoSolutionFileProvider.class );

  private boolean bypassAuthentication = false;

  public static final Collection capabilities = Collections.unmodifiableCollection( Arrays.asList(
      Capability.GET_TYPE,
      Capability.GET_LAST_MODIFIED,
      Capability.LIST_CHILDREN,
      Capability.READ_CONTENT,
      Capability.WRITE_CONTENT,
      Capability.CREATE,
      Capability.FS_ATTRIBUTES,
      Capability.URI ) );

  public static final UserAuthenticationData.Type[] AUTHENTICATOR_TYPES = new UserAuthenticationData.Type[]
    { UserAuthenticationData.USERNAME, UserAuthenticationData.PASSWORD
      // future: Publish Password for write access ..
    };

  public PentahoSolutionFileProvider() {
    setFileNameParser( LayeredFileNameParser.getInstance() );

    // check if can bypass authentication
    try {
      IPentahoSession session = PentahoSessionHolder.getSession();
      if ( session != null ) {
        // running locally => access directly bypassing authentication
        this.bypassAuthentication = true;
      }
    } catch ( NoClassDefFoundError e ) {
      // no server running
    }
  }

  /**
   * Creates a {@link org.apache.commons.vfs2.FileSystem}.  If the returned FileSystem implements {@link
   * org.apache.commons.vfs2.provider.VfsComponent}, it will be initialised.
   *
   * @param rootName The name of the root file of the file system to create.
   */
  protected FileSystem doCreateFileSystem( final FileName rootName,
                                           final FileSystemOptions fileSystemOptions ) throws FileSystemException {
    final LayeredFileName genericRootName = (LayeredFileName) rootName;
    if ( "jcr-solution".equals( rootName.getScheme() ) ) {

      // bypass authentication if running inside server
      if ( this.bypassAuthentication ) {
        return createJCRDirectFileSystem( genericRootName, fileSystemOptions );
      }

      return createJCRFileSystem( genericRootName, fileSystemOptions );
    }
    return createWebFileSystem( genericRootName, fileSystemOptions );
  }

  private FileSystem createJCRDirectFileSystem( final LayeredFileName genericRootName,
                                                final FileSystemOptions fileSystemOptions ) throws FileSystemException {
    final JCRSolutionDirectFileModel model = new JCRSolutionDirectFileModel();
    return new JCRSolutionDirectFileSystem( genericRootName, fileSystemOptions, model );
  }

  private FileSystem createJCRFileSystem( final LayeredFileName genericRootName,
                                          final FileSystemOptions fileSystemOptions ) {
    UserAuthenticationData authData = null;
    try {
      authData = UserAuthenticatorUtils.authenticate( fileSystemOptions, AUTHENTICATOR_TYPES );
      final GenericFileName outerName = (GenericFileName) genericRootName.getOuterName();

      final String username = UserAuthenticatorUtils.toString( UserAuthenticatorUtils
        .getData( authData, UserAuthenticationData.USERNAME, UserAuthenticatorUtils.toChar( outerName.getUserName() ) ) );

      final String password = UserAuthenticatorUtils.toString( UserAuthenticatorUtils
        .getData( authData, UserAuthenticationData.PASSWORD, UserAuthenticatorUtils.toChar( outerName.getPassword() ) ) );
      final PentahoSolutionsFileSystemConfigBuilder configBuilder = new PentahoSolutionsFileSystemConfigBuilder();
      final int timeOut = configBuilder.getTimeOut( fileSystemOptions );

      final JCRSolutionFileModel model = new JCRSolutionFileModel( outerName.getURI(), username, password, timeOut );
      return new JCRSolutionFileSystem( genericRootName, fileSystemOptions, model );
    } finally {
      UserAuthenticatorUtils.cleanup( authData );
    }
  }

  private FileSystem createWebFileSystem( final LayeredFileName genericRootName,
                                          final FileSystemOptions fileSystemOptions ) throws FileSystemException {
    final GenericFileName outerName = (GenericFileName) genericRootName.getOuterName();
    String scheme = outerName.getScheme();
    String hostName = outerName.getHostName();
    int port = outerName.getPort();
    String userName = outerName.getUserName();
    String password = outerName.getPassword();

    HttpClientManager.HttpClientBuilderFacade clientBuilder = HttpClientManager.getInstance().createBuilder();
    if ( !StringUtil.isEmpty( hostName ) ) {
      clientBuilder.setProxy( hostName, port, scheme );
    }
    if ( !StringUtil.isEmpty( userName ) ) {
      clientBuilder.setCredentials( userName, password );
    }
    final PentahoSolutionsFileSystemConfigBuilder configBuilder = new PentahoSolutionsFileSystemConfigBuilder();
    final int timeOut = configBuilder.getTimeOut( fileSystemOptions );
    clientBuilder.setSocketTimeout( Math.max( 0, timeOut ) );

    return new WebSolutionFileSystem( genericRootName, fileSystemOptions,
      new LocalFileModel( outerName.getURI(), clientBuilder, userName, password, hostName, port )
    );
  }

  /**
   * Get the filesystem capabilities.<br> These are the same as on the filesystem, but available before the first
   * filesystem was instanciated.
   */
  public Collection getCapabilities() {
    return capabilities;
  }
}
