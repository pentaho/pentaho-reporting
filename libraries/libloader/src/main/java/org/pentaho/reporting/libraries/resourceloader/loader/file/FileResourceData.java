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


package org.pentaho.reporting.libraries.resourceloader.loader.file;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.AbstractResourceData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple file reader. This class, as all core implementation, does not hold any references to the data read from the
 * file. Caching is left to the cache provider.
 *
 * @author Thomas Morgner
 */
public class FileResourceData extends AbstractResourceData {
  private ResourceKey key;
  private File file;
  private volatile int length;
  private static final long serialVersionUID = -5719048997437795736L;

  public FileResourceData( final ResourceKey key ) throws ResourceLoadingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    final File file = (File) key.getIdentifier();
    if ( file.exists() == false ) {
      throw new ResourceLoadingException
        ( "File-handle given does not point to an existing file." );
    }
    if ( file.isFile() == false ) {
      throw new ResourceLoadingException
        ( "File-handle given does not point to a regular file." );
    }
    if ( file.canRead() == false ) {
      throw new ResourceLoadingException
        ( "File '" + file + "' is not readable." );
    }
    this.key = key;
    this.file = file;
  }

  public InputStream getResourceAsStream( final ResourceManager caller ) throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }
    try {
      final int buffer = (int) Math.max( 4096, Math.min( file.length(), 128 * 1024 ) );
      return new BufferedInputStream( new FileInputStream( file ), buffer );
    } catch ( FileNotFoundException e ) {
      throw new ResourceLoadingException( "Unable to open Stream: ", e );
    }
  }

  public Object getAttribute( final String attrkey ) {
    if ( attrkey == null ) {
      throw new NullPointerException();
    }
    if ( attrkey.equals( ResourceData.FILENAME ) ) {
      return file.getName();
    }
    if ( attrkey.equals( ResourceData.CONTENT_LENGTH ) ) {
      return new Long( file.length() );
    }
    return null;
  }

  public long getVersion( final ResourceManager caller )
    throws ResourceLoadingException {
    if ( caller == null ) {
      throw new NullPointerException();
    }
    return file.lastModified();
  }

  public ResourceKey getKey() {
    return key;
  }
}
