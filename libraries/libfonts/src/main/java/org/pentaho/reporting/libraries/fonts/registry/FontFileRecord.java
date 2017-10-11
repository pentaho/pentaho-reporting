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
