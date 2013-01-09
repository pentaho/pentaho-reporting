package org.pentaho.reporting.libraries.pensol;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

public class VfsTest extends TestCase
{
  public VfsTest()
  {
  }

  public VfsTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    LibPensolBoot.getInstance().start();
  }

  public void testParse() throws IOException
  {
    final InputStream stream = TestSolutionFileModel.class.getResourceAsStream
        ("/org/pentaho/reporting/libraries/pensol/ee-service.xml");
    try
    {
      TestSolutionFileModel model = new TestSolutionFileModel();
      model.performParse(stream);
    }
    finally
    {
      stream.close();
    }
    
  }

  public void testInitialLoading() throws FileSystemException
  {
    final FileObject nonExistent = VFS.getManager().resolveFile("test-solution://localhost/non-existent");
    assertFalse(nonExistent.exists());
    assertEquals(FileType.IMAGINARY, nonExistent.getType());
    assertEquals("non-existent", nonExistent.getName().getBaseName());
    final FileObject directory = VFS.getManager().resolveFile("test-solution://localhost/bi-developers");
    assertTrue(directory.exists());
    assertEquals(FileType.FOLDER, directory.getType());
    assertEquals("bi-developers", directory.getName().getBaseName());
    final FileObject file = VFS.getManager().resolveFile("test-solution://localhost/bi-developers/analysis/query1.xaction");
    assertTrue(file.exists());
    assertEquals(FileType.FILE, file.getType());
    assertEquals("query1.xaction", file.getName().getBaseName());
  }
}
