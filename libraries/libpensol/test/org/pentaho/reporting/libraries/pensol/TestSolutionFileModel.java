package org.pentaho.reporting.libraries.pensol;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.pentaho.reporting.libraries.pensol.vfs.FileInfo;
import org.pentaho.reporting.libraries.pensol.vfs.XmlSolutionFileModel;

public class TestSolutionFileModel extends XmlSolutionFileModel
{
  public TestSolutionFileModel()
  {
  }

  public void refresh() throws IOException
  {
    final InputStream stream = TestSolutionFileModel.class.getResourceAsStream
        ("/org/pentaho/reporting/libraries/pensol/SolutionRepositoryService.xml");
    try
    {
      setRoot(this.performParse(stream));
    }
    finally
    {
      stream.close();
    }
  }

  public FileInfo performParse(final InputStream postResult) throws IOException
  {
    return super.performParse(postResult);
  }

  protected byte[] getDataInternally(final FileInfo fileInfo) throws FileSystemException
  {
    return new byte[0];
  }

  public long getContentSize(final FileName name) throws FileSystemException
  {
    return 0;
  }

  protected void setDataInternally(final FileInfo fileInfo, final byte[] data) throws FileSystemException
  {
    throw new FileSystemException("Not implemented");
  }
}
