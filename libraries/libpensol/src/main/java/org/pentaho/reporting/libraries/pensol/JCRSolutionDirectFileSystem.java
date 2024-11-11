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

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.util.Collection;

/**
 * @author Marco Vala
 */
public class JCRSolutionDirectFileSystem extends AbstractFileSystem {

  public static final String LAST_REFRESH_TIME_ATTRIBUTE = "lastRefreshTime";
  public static final String MAJOR_VERSION = "version-major";
  public static final String MINOR_VERSION = "version-minor";
  public static final String RELEASE_VERSION = "version-release";
  public static final String MILESTONE_VERSION = "version-milestone";
  public static final String BUILD_VERSION = "version-build";

  private JCRSolutionDirectFileModel solutionFileModel;

  public JCRSolutionDirectFileSystem( final FileName rootName,
                                      final FileSystemOptions fileSystemOptions,
                                      final JCRSolutionDirectFileModel solutionFileModel ) {
    super( rootName, null, fileSystemOptions );
    this.solutionFileModel = solutionFileModel;
  }

  protected FileObject createFile( final AbstractFileName name ) throws Exception {
    return new WebSolutionFileObject( name, this, solutionFileModel );
  }

  protected void addCapabilities( final Collection caps ) {
    caps.addAll( PentahoSolutionFileProvider.capabilities );
  }

  public JCRSolutionDirectFileModel getLocalFileModel() {
    return solutionFileModel;
  }

  public Object getAttribute( final String attrName ) throws FileSystemException {
    if ( LAST_REFRESH_TIME_ATTRIBUTE.equals( attrName ) ) {
      return 0;
    }
    if ( MAJOR_VERSION.equals( attrName ) ) {
      return 999;
    }
    if ( MINOR_VERSION.equals( attrName ) ) {
      return 999;
    }
    if ( BUILD_VERSION.equals( attrName ) ) {
      return 999;
    }
    if ( RELEASE_VERSION.equals( attrName ) ) {
      return 999;
    }
    if ( MILESTONE_VERSION.equals( attrName ) ) {
      return 999;
    }
    return null;
  }
}
