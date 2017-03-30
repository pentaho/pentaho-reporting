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
* Copyright (c) 2002-2017 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ValidateLoginTask implements Runnable {
  private static final String AUTHORIZATION = "Authorization";
  private static final String TREE_API_URL_PART = "/api/repo/files/%3A/tree?depth=1";
  private AuthenticationData loginData;
  private Exception exception;
  private boolean loginComplete;

  private static final Log logger = LogFactory.getLog( ValidateLoginTask.class );

  public ValidateLoginTask( final LoginTask loginTask ) {
    this.loginData = loginTask.getLoginData();
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
    loginComplete = false;
    try {
      if ( useOldVfsValidation() ) {
        loginComplete = validateLoginData();
      } else {
        loginComplete = validateLoginDataFast();
      }
    } catch ( FileSystemException exception ) {
      this.loginComplete = false;
      this.exception = exception;
    }
  }

  public Exception getException() {
    return exception;
  }

  public boolean isLoginComplete() {
    return loginComplete;
  }


  /**
   * An old implementation, which may take unreasonable amount of time in case of a huge filesystem Disabled by default.
   * Could be switched on by setting a org.pentaho.reporting.designer.extensions.pentaho.repository.VfsValidation to
   * true
   */
  @Deprecated
  public boolean validateLoginData() throws FileSystemException {
    if ( loginData == null ) {
      return true;
    }
    final FileObject vfsConnection = PublishUtil.createVFSConnection( VFS.getManager(), loginData );
    try {
      final FileSystem fileSystem = vfsConnection.getFileSystem();
      if ( fileSystem instanceof WebSolutionFileSystem ) {
        final WebSolutionFileSystem webSolutionFileSystem = (WebSolutionFileSystem) fileSystem;
        final Long l = (Long) webSolutionFileSystem.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE );
        if ( l != null ) {
          if ( ( System.currentTimeMillis() - l ) > 500 ) {
            webSolutionFileSystem.getLocalFileModel().refresh();
          }
        }
        return true;
      }
    } catch ( FileSystemException fse ) {
      // not all file systems support attributes ..
    } catch ( IOException e ) {
      return false;
    }
    final FileType type = vfsConnection.getType();
    if ( type != FileType.FOLDER ) {
      return false;
    }
    return true;
  }


  /**
   * A faster way to check the provided credentials by calling a servers tree API with a basic authentication.
   *
   * @return
   */
  public boolean validateLoginDataFast() {

    //Backward compatibility
    if ( loginData == null ) {
      return true;
    }

    try {

      final HttpClient httpClient = getHttpClient();
      //Remove trailing slashes and spaces
      final URI uri = new URI( StringUtils.stripEnd( loginData.getUrl(), "/ " ) + TREE_API_URL_PART );
      final Credentials credentials =
        new UsernamePasswordCredentials( loginData.getUsername(), loginData.getPassword() );
      final HttpState state = new HttpState();
      state.setCredentials( new AuthScope( uri.getHost(), uri.getPort(), AuthScope.ANY_REALM ), credentials );
      httpClient.setState( state );

      final HttpMethod get = new GetMethod( uri.toString() );

      final int code = httpClient.executeMethod( get );

      if ( code == HttpStatus.SC_OK ) {
        return true;
      }

      logger.info( "Can't log in with the provided credentials. Status code: " + code );

    } catch ( final IOException | URISyntaxException | IllegalArgumentException e ) {
      logger.info( "Can't connect to the server", e );
    }
    return false;
  }

  @VisibleForTesting
  HttpClient getHttpClient() {
    return new HttpClient();
  }

  @VisibleForTesting
  boolean useOldVfsValidation() {
    boolean result = false;
    try {
      final String useOldVfsValidation = ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty(
        "org.pentaho.reporting.designer.extensions.pentaho.repository.VfsValidation" );
      result = "true".equals( useOldVfsValidation );
    } catch ( final Exception e ) {
      logger.info( "Cant't read a configuration: ", e );
    }
    return result;
  }

}
