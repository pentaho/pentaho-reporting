package org.pentaho.reporting.libraries.pensol;

import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;

public class PentahoSolutionsFileSystemConfigBuilder extends DefaultFileSystemConfigBuilder
{
  private static final String TIMEOUT_KEY = "timeout";

  public PentahoSolutionsFileSystemConfigBuilder()
  {
  }

  public void setTimeOut(final FileSystemOptions opts, final int timeOut)
  {
      setParam(opts, TIMEOUT_KEY, timeOut);
  }

  public int getTimeOut(final FileSystemOptions opts)
  {
    final Integer param = (Integer) getParam(opts, TIMEOUT_KEY);
    if (param != null)
    {
      return param;
    }
    return 0;
  }
}
