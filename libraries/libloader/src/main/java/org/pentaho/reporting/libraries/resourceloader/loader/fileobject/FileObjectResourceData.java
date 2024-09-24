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
 * Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.resourceloader.loader.fileobject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.AbstractResourceData;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class FileObjectResourceData extends AbstractResourceData {
  private ResourceKey key;
  private FileObject fileObject;
  private volatile int length;
  private static final long serialVersionUID = 1L;

  public FileObjectResourceData( final ResourceKey key ) throws ResourceLoadingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    final FileObject fileObject = (FileObject) key.getIdentifier();
    try {
      if ( fileObject.exists() == false ) {
        throw new ResourceLoadingException( "File-handle given does not point to an existing file." );
      }
      if ( fileObject.isFile() == false ) {
        throw new ResourceLoadingException( "File-handle given does not point to a regular file." );
      }
      if ( fileObject.isReadable() == false ) {
        throw new ResourceLoadingException( "File '" + fileObject + "' is not readable." );
      }
    } catch ( FileSystemException fse ) {
      throw new ResourceLoadingException( "Unable to create FileObjectResourceData : ", fse );
    }

    this.key = key;
    this.fileObject = fileObject;
  }

  public InputStream getResourceAsStream( final ResourceManager caller ) throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }
    try {
      final int buffer = (int) Math.max( 4096, Math.min( fileObject.getContent().getSize(), 128 * 1024 ) );
      return new BufferedInputStream( fileObject.getContent().getInputStream(), buffer );
    } catch ( Exception e ) {
      throw new ResourceLoadingException( "Unable to open Stream: ", e );
    }
  }

  public Object getAttribute( final String attrkey ) {
    if ( attrkey == null ) {
      throw new NullPointerException();
    }
    try {
      if ( attrkey.equals( ResourceData.FILENAME ) ) {
        return fileObject.getName().getBaseName();
      }
      if ( attrkey.equals( ResourceData.CONTENT_LENGTH ) ) {
        return new Long( fileObject.getContent().getSize() );
      }
    } catch ( FileSystemException fse ) {

    }

    return null;
  }

  public long getVersion( final ResourceManager caller )
        throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }
    try {
      return fileObject.getContent().getLastModifiedTime();
    } catch ( FileSystemException fse ) {
      throw new ResourceLoadingException( "Unable to get version: ", fse );
    }
  }

  public ResourceKey getKey() {
    return key;
  }
}
