package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.File;
import java.io.IOException;

import mondrian.olap.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * This class emulates Mondrian's schema resolving and allows us to find a string that correctly represents
 * a valid VFS path for use in mondrian.
 *
 * @author Thomas Morgner.
 */
public class SchemaResolver
{
  private static final Log logger = LogFactory.getLog(SchemaResolver.class);

  private SchemaResolver()
  {
  }

  public static String resolveSchema(final ResourceManager resourceManager,
                                     final ResourceKey contextKey,
                                     String catalogUrl)
      throws FileSystemException
  {
    final FileSystemManager fsManager = VFS.getManager();
    if (fsManager == null)
    {
      throw Util.newError("Cannot get virtual file system manager");
    }

    // Workaround VFS bug.
    if (catalogUrl.startsWith("file://localhost"))
    {
      catalogUrl = catalogUrl.substring("file://localhost".length());
    }
    if (catalogUrl.startsWith("file:"))
    {
      catalogUrl = catalogUrl.substring("file:".length());
    }

    try
    {
      final File catalogFile = new File(catalogUrl).getCanonicalFile();
      final FileObject file = fsManager.toFileObject(catalogFile);
      if (file.isReadable())
      {
        return catalogFile.getPath();
      }
    }
    catch (FileSystemException fse)
    {
      logger.info("Failed to resolve schema file '" + catalogUrl + "' as local file. Treating file as non-readable.", fse);
    }
    catch (IOException e)
    {
      logger.info("Failed to resolve schema file '" + catalogUrl + "' as local file. Treating file as non-readable.", e);
    }

    if (contextKey == null)
    {
      return catalogUrl;
    }

    final File contextAsFile = getContextAsFile(contextKey);
    if (contextAsFile == null)
    {
      return catalogUrl;
    }

    final File resolvedFile = new File(contextAsFile.getParentFile(), catalogUrl);
    if (resolvedFile.isFile() && resolvedFile.canRead())
    {
      return resolvedFile.getAbsolutePath();
    }

    return catalogUrl;
  }

  private static File getContextAsFile(ResourceKey key)
  {
    while (key != null)
    {
      final Object identifier = key.getIdentifier();
      if (identifier instanceof File)
      {
        return (File) identifier;
      }

      key = key.getParent();
    }
    return null;
  }

}
