package org.pentaho.reporting.libraries.pensol;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

public class TestWebSolutionFileProvider extends PentahoSolutionFileProvider
{
  /**
   * Creates a {@link org.apache.commons.vfs.FileSystem}.  If the returned FileSystem implements
   * {@link org.apache.commons.vfs.provider.VfsComponent}, it will be initialised.
   *
   * @param rootName The name of the root file of the file system to create.
   */
  protected FileSystem doCreateFileSystem(final FileName rootName,
                                          final FileSystemOptions fileSystemOptions) throws FileSystemException
  {
    return new WebSolutionFileSystem(rootName, fileSystemOptions, new TestSolutionFileModel());
  }
}
