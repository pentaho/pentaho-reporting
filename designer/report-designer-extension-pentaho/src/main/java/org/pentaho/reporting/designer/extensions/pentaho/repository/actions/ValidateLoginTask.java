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
