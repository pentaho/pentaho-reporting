package org.pentaho.reporting.libraries.pensol;

import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;

public class JCRSolutionFileSystem extends AbstractFileSystem
{
  private JCRSolutionFileModel solutionFileModel;
  public static final String LAST_REFRESH_TIME_ATTRIBUTE = "lastRefreshTime";
  public static final String MAJOR_VERSION = "version-major";
  public static final String MINOR_VERSION = "version-minor";
  public static final String RELEASE_VERSION = "version-release";
  public static final String MILESTONE_VERSION = "version-milestone";
  public static final String BUILD_VERSION = "version-build";

  public JCRSolutionFileSystem(final FileName rootName,
                               final FileSystemOptions fileSystemOptions,
                               final JCRSolutionFileModel solutionFileModel)
  {
    super(rootName, null, fileSystemOptions);
    this.solutionFileModel = solutionFileModel;
  }

  /**
   * Creates a file object.  This method is called only if the requested
   * file is not cached.
   */
  protected FileObject createFile(final FileName name) throws Exception
  {
    return new WebSolutionFileObject(name, this, solutionFileModel);
  }

  /**
   * Adds the capabilities of this file system.
   */
  protected void addCapabilities(final Collection caps)
  {
    caps.addAll(PentahoSolutionFileProvider.capabilities);
  }

  public JCRSolutionFileModel getLocalFileModel()
  {
    return solutionFileModel;
  }

  /**
   * Retrieves the attribute with the specified name. The default
   * implementation simply throws an exception.
   */
  public Object getAttribute(final String attrName) throws FileSystemException
   {
     if (LAST_REFRESH_TIME_ATTRIBUTE.equals(attrName))
     {
       return Long.valueOf(solutionFileModel.getRefreshTime());
     }
     if (MAJOR_VERSION.equals(attrName))
     {
       return (solutionFileModel.getMajorVersion());
     }
     if (MINOR_VERSION.equals(attrName))
     {
       return (solutionFileModel.getMinorVersion());
     }
     if (BUILD_VERSION.equals(attrName))
     {
       return (solutionFileModel.getBuildVersion());
     }
     if (RELEASE_VERSION.equals(attrName))
     {
       return (solutionFileModel.getReleaseVersion());
     }
     if (MILESTONE_VERSION.equals(attrName))
     {
       return (solutionFileModel.getMilestoneVersion());
     }
     return null;
  }
}
