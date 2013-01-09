package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import org.apache.commons.vfs.FileSystemException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.01.11
 * Time: 15:04
 *
 * @author Thomas Morgner.
 */
public class Prd3139Test extends TestCase
{
  public Prd3139Test()
  {
  }

  public Prd3139Test(final String name)
  {
    super(name);
  }

  public void testSchemaResolve () throws FileSystemException
  {
    final URL url = Prd3139Test.class.getResource
        ("/org/pentaho/reporting/engine/classic/extensions/datasources/mondrian/steelwheels.mondrian.xml");
    final String fileTxt = url.getFile();
    final File file = new File (fileTxt).getAbsoluteFile();
    assertTrue(file.canRead());
    assertNotNull(
        SchemaResolver.resolveSchema(null, null, "../../../../../../../../../../../../../../" + file.getPath()));
  }
}
