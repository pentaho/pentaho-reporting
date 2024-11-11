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


package org.pentaho.reporting.libraries.fonts.io;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Creation-Date: 15.12.2005, 15:55:56
 *
 * @author Thomas Morgner
 */
public class FileFontDataInputSource implements FontDataInputSource {
  private File file;
  private RandomAccessFile fileReader;
  private long lastPosition;

  public FileFontDataInputSource( final File file ) throws IOException {
    this.file = file;
    this.fileReader = new RandomAccessFile( file, "r" );
  }

  public long getLength() {
    return file.length();
  }

  public void readFullyAt
    ( final long position, final byte[] buffer, final int length )
    throws IOException {
    if ( fileReader == null ) {
      this.fileReader = new RandomAccessFile( file, "r" );
      if ( position > fileReader.length() ) {
        throw new EOFException( "Given position is beyond the end of the file." );
      }

      fileReader.seek( position );
    } else {
      if ( position > fileReader.length() ) {
        throw new EOFException( "Given position is beyond the end of the file." );
      }

      if ( position != lastPosition ) {
        fileReader.seek( position );
      }
    }

    try {
      fileReader.readFully( buffer, 0, length );
      lastPosition = position + length;
    } catch ( IOException ioe ) {
      lastPosition = -1;
      throw ioe;
    }
  }

  public int readAt( final long position,
                     final byte[] buffer,
                     final int offset,
                     final int length ) throws IOException {
    if ( fileReader == null ) {
      this.fileReader = new RandomAccessFile( file, "r" );
      if ( position > fileReader.length() ) {
        return 0;
      }

      fileReader.seek( position );
    } else {
      if ( position > fileReader.length() ) {
        return 0;
      }

      if ( position != lastPosition ) {
        fileReader.seek( position );
      }
    }

    try {
      final int readLength = (int) Math.min( length, fileReader.length() - position );
      fileReader.readFully( buffer, offset, readLength );
      lastPosition = position + readLength;
      return readLength;
    } catch ( IOException ioe ) {
      lastPosition = -1;
      throw ioe;
    }
  }

  public int readAt( final long position ) throws IOException {
    if ( fileReader == null ) {
      this.fileReader = new RandomAccessFile( file, "r" );
      fileReader.seek( position );
    } else if ( position != lastPosition ) {
      fileReader.seek( position );
    }
    final int retval = fileReader.read();
    if ( retval == -1 ) {
      return -1;
    }
    lastPosition = position + 1;
    return retval;
  }

  public void dispose() {
    if ( this.fileReader == null ) {
      return;
    }
    try {
      this.fileReader.close();
    } catch ( IOException e ) {
      // we can safely ignore that one.
    }
    this.fileReader = null;
  }

  public File getFile() {
    return file;
  }

  public String getFileName() {
    return file.getPath();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FileFontDataInputSource that = (FileFontDataInputSource) o;

    if ( !file.equals( that.file ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return file.hashCode();
  }
}
