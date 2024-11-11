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


package org.pentaho.reporting.libraries.repository.stream;

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A stream-content item that wraps around the input and output streams given in the repository. Depending on which
 * stream are given, this item reports itself as read or writable.
 *
 * @author Thomas Morgner
 */
public class StreamContentItem implements ContentItem {
  private WrappedInputStream inputStream;
  private WrappedOutputStream outputStream;
  private ContentLocation parent;
  private String name;

  /**
   * Creates a new stream-content item. The item will have the given name and parent and will wrap around the provided
   * streams.
   *
   * @param name         the name of the content item.
   * @param parent       the parent location.
   * @param inputStream  the (optional) input stream.
   * @param outputStream the (optional) output stream.
   */
  public StreamContentItem( final String name,
                            final ContentLocation parent,
                            final WrappedInputStream inputStream,
                            final WrappedOutputStream outputStream ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }

    this.name = name;
    this.parent = parent;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  /**
   * Checks, whether the content item is readable. A content item that is not readable will never return a valid
   * inputstream and any call to getInputStream is bound to fail.
   *
   * @return true, if the content item is readable, false otherwise.
   */
  public boolean isReadable() {
    if ( inputStream == null ) {
      return false;
    }
    return inputStream.isClosed() == false;
  }

  /**
   * Checks, whether the content item is writable. A content item that is not writable will never return a valid
   * outputstream and any call to getOutputStream is bound to fail.
   *
   * @return true, if the content item is writeable, false otherwise.
   */
  public boolean isWriteable() {
    if ( outputStream == null ) {
      return false;
    }
    return outputStream.isClosed() == false;
  }

  /**
   * Returns the mime type for the content entity. If the repository does not store mimetypes, this call usually uses
   * the repositories MimeRegistry to resolve the mimetype.
   *
   * @return the mime type.
   * @throws ContentIOException if an error occured.
   */
  public String getMimeType() throws ContentIOException {
    return getRepository().getMimeRegistry().getMimeType( this );
  }

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
  public OutputStream getOutputStream() throws ContentIOException, IOException {
    return outputStream;
  }

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
  public InputStream getInputStream() throws ContentIOException, IOException {
    return inputStream;
  }

  /**
   * Returns the name of the entry.
   *
   * @return the name, never null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a unique identifier. This can be canonical filename or a database key. It must be guaranteed that within
   * the same repository the key will be unique.
   *
   * @return the unique content ID.
   */
  public Object getContentId() {
    return parent.getName() + '/' + name;
  }

  /**
   * Stream-Repositories do not support attributes.
   *
   * @param domain the attribute domain.
   * @param key    the name of the attribute.
   * @return always null.
   */
  public Object getAttribute( final String domain, final String key ) {
    return null;
  }

  /**
   * Stream-Repositories do not support attributes.
   *
   * @param domain the attribute domain.
   * @param key    the attribute name
   * @param value  the new attribute value.
   * @return always false.
   */
  public boolean setAttribute( final String domain, final String key, final Object value ) {
    return false;
  }

  /**
   * Returns a reference to the parent location. If this entity represents the root directory, this method will return
   * null.
   *
   * @return the parent or null, if this is the root-directory.
   */
  public ContentLocation getParent() {
    return parent;
  }

  /**
   * Returns the current repository, to which tis entity belongs.
   *
   * @return the repository.
   */
  public Repository getRepository() {
    return parent.getRepository();
  }

  /**
   * Stream-repositories do not support the deletion of entries.
   *
   * @return always false.
   */
  public boolean delete() {
    return false;
  }
}
