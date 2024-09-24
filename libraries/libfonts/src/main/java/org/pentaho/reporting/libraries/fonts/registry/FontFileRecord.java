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

package org.pentaho.reporting.libraries.fonts.registry;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FontFileRecord implements Serializable {
  private static final long serialVersionUID = 5378117215425762149L;

  private long lastAccessTime;
  private long fileSize;
  private String filename;

  public FontFileRecord( final File file ) throws IOException {
    this( file.getCanonicalPath(), file.length(), file.lastModified() );
  }

  public FontFileRecord( final String filename,
                         final long fileSize,
                         final long lastAccessTime ) {
    if ( filename == null ) {
      throw new NullPointerException();
    }
    this.filename = filename;
    this.fileSize = fileSize;
    this.lastAccessTime = lastAccessTime;
  }

  public long getLastAccessTime() {
    return lastAccessTime;
  }

  public long getFileSize() {
    return fileSize;
  }

  public String getFilename() {
    return filename;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FontFileRecord that = (FontFileRecord) o;

    if ( fileSize != that.fileSize ) {
      return false;
    }
    if ( lastAccessTime != that.lastAccessTime ) {
      return false;
    }
    if ( !filename.equals( that.filename ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = (int) ( lastAccessTime ^ ( lastAccessTime >>> 32 ) );
    result = 29 * result + (int) ( fileSize ^ ( fileSize >>> 32 ) );
    result = 29 * result + filename.hashCode();
    return result;
  }
}
