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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

import java.io.IOException;

public class ValidateLoginTask implements Runnable {
  private AuthenticationData loginData;
  private Exception exception;
  private boolean loginComplete;

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
      loginComplete = validateLoginData();
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
}
