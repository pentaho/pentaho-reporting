package org.pentaho.reporting.libraries.repository.file;

import java.io.File;
import java.io.IOException;

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.NameGenerator;

public class TempFileNameGenerator implements NameGenerator
{
  private FileContentLocation fileContentLocation;

  public TempFileNameGenerator(final FileContentLocation fileContentLocation)
  {
    if (fileContentLocation == null)
    {
      throw new NullPointerException();
    }
    this.fileContentLocation = fileContentLocation;
  }

  /**
   * Generates a new name for the location. The name-generator may use both the name-hint and mimetype to compute
   * the new name.
   *
   * @param nameHint the name hint, usually a identifier for the new filename (can be null).
   * @param mimeType the mime type of the new filename. Usually used to compute a suitable file-suffix.
   * @return the generated name, never null.
   * @throws org.pentaho.reporting.libraries.repository.ContentIOException
   *          if the name could not be generated for any reason.
   */
  public String generateName(final String nameHint, final String mimeType) throws ContentIOException
  {
    final MimeRegistry mimeRegistry = fileContentLocation.getRepository().getMimeRegistry();
    final File targetDirectory = fileContentLocation.getBackend();
    final String suffix = mimeRegistry.getSuffix(mimeType);
    try
    {
      final File tempFile = File.createTempFile(nameHint, "." + suffix, targetDirectory);
      return tempFile.getName();
    }
    catch (IOException e)
    {
      throw new ContentIOException("Unable to generate a name for the data file", e);
    }
  }
}
