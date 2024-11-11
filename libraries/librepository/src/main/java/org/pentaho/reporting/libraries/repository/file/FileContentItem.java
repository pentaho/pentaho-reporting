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


package org.pentaho.reporting.libraries.repository.file;

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A content item wrapping a file.
 *
 * @author Thomas Morgner
 */
public class FileContentItem extends FileContentEntity implements ContentItem {
  private static final long serialVersionUID = 5080072160607835550L;

  /**
   * Creates a new file based content item for the given file and parent location.
   *
   * @param parent  the parent.
   * @param backend the backend.
   */
  public FileContentItem( final ContentLocation parent, final File backend ) {
    super( parent, backend );
  }

  public String getMimeType() throws ContentIOException {
    final FileRepository fileRepository = (FileRepository) getRepository();
    return fileRepository.getMimeRegistry().getMimeType( this );
  }

  public OutputStream getOutputStream() throws ContentIOException, IOException {
    return new FileOutputStream( getBackend() );
  }

  public InputStream getInputStream()
    throws ContentIOException, IOException {
    return new FileInputStream( getBackend() );
  }

  public boolean isReadable() {
    return getBackend().canRead();
  }

  public boolean isWriteable() {
    return getBackend().canWrite();
  }
}
