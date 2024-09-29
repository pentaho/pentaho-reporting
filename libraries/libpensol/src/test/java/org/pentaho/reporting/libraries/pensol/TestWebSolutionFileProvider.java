/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.pensol;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

public class TestWebSolutionFileProvider extends PentahoSolutionFileProvider {
  /**
   * Creates a {@link org.apache.commons.vfs2.FileSystem}.  If the returned FileSystem implements
   * {@link org.apache.commons.vfs2.provider.VfsComponent}, it will be initialised.
   *
   * @param rootName The name of the root file of the file system to create.
   */
  protected FileSystem doCreateFileSystem( final FileName rootName,
                                           final FileSystemOptions fileSystemOptions ) throws FileSystemException {
    return new WebSolutionFileSystem( rootName, fileSystemOptions, new TestSolutionFileModel() );
  }
}
