/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileSystem;
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
    } catch ( FileSystemException exception1 ) {
      this.loginComplete = false;
      this.exception = exception1;
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
    try ( FileObject vfsConnection = PublishUtil.createVFSConnection( VFS.getManager(), loginData ) ) {
      final FileSystem fileSystem = vfsConnection.getFileSystem();
      if ( fileSystem instanceof WebSolutionFileSystem webSolutionFileSystem ) {
        refreshIfStale( webSolutionFileSystem,
            WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE,
            webSolutionFileSystem.getLocalFileModel() );
        return true;
      }
      if ( fileSystem instanceof JCRSolutionFileSystem jcrFileSystem ) {
        refreshIfStale( jcrFileSystem,
            JCRSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE,
            jcrFileSystem.getLocalFileModel() );
        return true;
      }
      return vfsConnection.getType() == FileType.FOLDER;
    } catch ( FileSystemException fse ) {
      // refresh() threw — likely an expired session (HTTP 401/403).
      // Re-throw so LoginTask can handle re-login.
      throw fse;
    } catch ( IOException e ) {
      return false;
    }
  }

  private void refreshIfStale( final FileSystem fileSystem,
                                final String lastRefreshAttribute,
                                final org.pentaho.reporting.libraries.pensol.SolutionFileModel fileModel )
      throws IOException {
    Long lastRefresh = null;
    try {
      lastRefresh = (Long) fileSystem.getAttribute( lastRefreshAttribute );
    } catch ( FileSystemException fse ) {
      // not all file systems support attributes — ignore and force a refresh
    }
    if ( lastRefresh == null || ( System.currentTimeMillis() - lastRefresh ) > 500 ) {
      try {
        fileModel.refresh();
      } catch ( RuntimeException re ) {
        // Wrap HTTP errors for consistent handling.
        throw new FileSystemException( re );
      }
    }
  }
}
