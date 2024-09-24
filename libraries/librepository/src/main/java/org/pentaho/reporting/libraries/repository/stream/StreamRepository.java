/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.repository.stream;

import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.Repository;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A repository that feeds a single source.
 *
 * @author Thomas Morgner
 */
public class StreamRepository implements Repository {
  private MimeRegistry mimeRegistry;
  private WrappedOutputStream outputStream;
  private WrappedInputStream inputStream;
  private StreamContentLocation rootLocation;
  private String contentName;

  /**
   * Creates a new repository that potentially allows both read and write access. If an input stream is given, then a
   * content name must be given as well.
   *
   * @param inputStream  the inputstream from which to read from.
   * @param outputStream the output stream to which to write to.
   * @param contentName  the content name by which the content should be accessed.
   */
  public StreamRepository( final InputStream inputStream,
                           final OutputStream outputStream,
                           final String contentName ) {
    if ( contentName == null ) {
      throw new NullPointerException();
    }
    if ( inputStream != null ) {
      this.inputStream = new WrappedInputStream( inputStream );
    }
    if ( outputStream != null ) {
      this.outputStream = new WrappedOutputStream( outputStream );
    }

    this.contentName = contentName;
    this.mimeRegistry = new DefaultMimeRegistry();
    this.rootLocation = new StreamContentLocation( this );
  }

  /**
   * Creates a new read/write repository with a hardcoded name for the input stream.
   *
   * @param inputStream  the input stream from where to read the data (can be null).
   * @param outputStream the output stream where data is written to (can be null).
   * @deprecated This constructor should not be used, as it hardcodes the filename for the input stream. Use one of the
   * other constructors instead.
   */
  public StreamRepository( final InputStream inputStream, final OutputStream outputStream ) {
    this( inputStream, outputStream, "content.data" );
  }

  /**
   * Creates a new read-only repository.
   *
   * @param inputStream the input stream from where to read the data (can be null).
   * @param contentName the content name by which the content should be accessed.
   */
  public StreamRepository( final InputStream inputStream, final String contentName ) {
    this( inputStream, null, contentName );
  }

  /**
   * Creates a new write-only repository.
   *
   * @param outputStream the output stream to which to write to.
   */
  public StreamRepository( final OutputStream outputStream ) {
    this( null, outputStream, "content.data" );
  }

  /**
   * Returns the optional content name by which the data in the input-stream should be accessed.
   *
   * @return the content name or null, if this repository is write-only.
   */
  public String getContentName() {
    return contentName;
  }

  /**
   * Returns the optional output stream.
   *
   * @return the stream or null, if this repository is read-only.
   */
  public WrappedOutputStream getOutputStream() {
    return outputStream;
  }

  /**
   * Returns the optional input stream.
   *
   * @return the stream or null, if this repository is write-only.
   */
  public WrappedInputStream getInputStream() {
    return inputStream;
  }

  /**
   * Returns the content root of this repository.
   *
   * @return the content root.
   */
  public ContentLocation getRoot() {
    return rootLocation;
  }

  /**
   * Returns the mime registry for this repository.
   *
   * @return the mime-registry.
   */
  public MimeRegistry getMimeRegistry() {
    return mimeRegistry;
  }
}
