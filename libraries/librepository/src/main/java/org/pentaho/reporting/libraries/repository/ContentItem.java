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

package org.pentaho.reporting.libraries.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A content item holds the actual content. On a file system, this would be a file. Whether reading and writing the same
 * content item at the same time is allowed is implementation specific.
 *
 * @author Thomas Morgner
 */
public interface ContentItem extends ContentEntity {
  /**
   * Returns the mime type for the content entity. If the repository does not store mimetypes, this call usually uses
   * the repositories MimeRegistry to resolve the mimetype.
   *
   * @return the mime type.
   * @throws ContentIOException if an error occured.
   */
  public String getMimeType() throws ContentIOException;

  /**
   * Tries to open and return a output stream for writing into the content item. This call will fail if the item is not
   * writeable. Whether opening multiple output streams at the same time is possible is implementation dependent, but it
   * is generally not recommended to try this.
   * <p/>
   * Having both an input and output stream open at the same time is not guaranteed to work. Generally if you need to
   * append data, first open the inputstream and copy the content to a temporary location and then write the content
   * along with the appended content to the new output stream.
   *
   * @return the output stream for writing the item.
   * @throws ContentIOException if an repository related error prevents the creation of the output stream.
   * @throws IOException        if an IO error occurs.
   */
  public OutputStream getOutputStream() throws ContentIOException, IOException;

  /**
   * Tries to open and return a input stream for reading from the content item. This call will fail if the item is not
   * readable. Whether opening multiple input streams at the same time is possible is implementation dependent.
   * <p/>
   * Having both an input and output stream open at the same time is not guaranteed to work. Generally if you need to
   * append data, first open the inputstream and copy the content to a temporary location and then write the content
   * along with the appended content to the new output stream.
   *
   * @return the input stream for reading from the item.
   * @throws ContentIOException if an repository related error prevents the creation of the input stream.
   * @throws IOException        if an IO error occurs.
   */
  public InputStream getInputStream()
    throws ContentIOException, IOException;

  /**
   * Checks, whether the content item is readable. A content item that is not readable will never return a valid
   * inputstream and any call to getInputStream is bound to fail.
   *
   * @return true, if the content item is readable, false otherwise.
   */
  public boolean isReadable();

  /**
   * Checks, whether the content item is writable. A content item that is not writable will never return a valid
   * outputstream and any call to getOutputStream is bound to fail.
   *
   * @return true, if the content item is writeable, false otherwise.
   */
  public boolean isWriteable();
}
