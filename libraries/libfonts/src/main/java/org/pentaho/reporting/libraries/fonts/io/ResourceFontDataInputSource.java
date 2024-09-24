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

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.io.IOException;

/**
 * The current use of iText and its inherent dependence on the font filename makes it not feasible to use libLoader for
 * the font loading right now.
 *
 * @author Thomas Morgner
 */
public class ResourceFontDataInputSource implements FontDataInputSource {
  //private transient byte[] rawData;
  private transient ResourceData rawData;
  private ResourceManager loader;
  private ResourceKey source;

  public ResourceFontDataInputSource( final ResourceManager loader,
                                      final ResourceKey source ) {
    if ( loader == null ) {
      throw new NullPointerException();
    }
    if ( source == null ) {
      throw new NullPointerException();
    }
    this.loader = loader;
    this.source = source;
  }

  public long getLength() {
    return rawData.getLength();
  }

  public void readFullyAt( final long position, final byte[] buffer, final int length )
    throws IOException {
    if ( rawData == null ) {
      try {
        rawData = loader.load( source );
      } catch ( ResourceLoadingException e ) {
        throw new IOException( "Failed to load the raw data." );
      }
    }


    final int iPos = (int) ( position & 0x7fffffff );
    try {
      rawData.getResource( loader, buffer, iPos, length );
    } catch ( ResourceLoadingException e ) {
      throw new IOException( "Unable to load data: " + e.getMessage() );
    }
  }
  //
  //  public int readAt(final long position, final byte[] buffer, final int offset, final int length) throws IOException
  //  {
  //    if (rawData == null)
  //    {
  //      try
  //      {
  //        rawData = loader.load(source);
  //      }
  //      catch (ResourceLoadingException e)
  //      {
  //        throw new IOException("Failed to load the raw data.");
  //      }
  //    }
  //
  //    final int iPos = (int) (position & 0x7fffffff);
  //    final int readLength = Math.min (length, rawData.length - iPos);
  //    System.arraycopy(rawData, iPos, buffer, offset, readLength);
  //    return readLength;
  //  }

  public int readAt( final long position ) throws IOException {
    if ( rawData == null ) {
      try {
        rawData = loader.load( source );
      } catch ( ResourceLoadingException e ) {
        throw new IOException( "Failed to load the raw data." );
      }
    }

    final int iPos = (int) ( position & 0x7fffffff );
    final byte[] buffer = new byte[ 1 ];

    final long length = rawData.getLength();
    if ( ( length > -1 ) && ( iPos >= length ) ) {
      return -1;
    }
    try {
      rawData.getResource( loader, buffer, iPos, 1 );
      return buffer[ 0 ];
    } catch ( IndexOutOfBoundsException e ) {
      return -1;
    } catch ( ResourceLoadingException e ) {
      throw new IOException( "Failed to read stream." );
    }
  }

  public void dispose() {
    rawData = null;
  }

  public String getFileName() {
    final Object identifier = source.getIdentifier();
    if ( identifier instanceof File ) {
      final File f = (File) identifier;
      return f.getPath();
    }
    return null;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ResourceFontDataInputSource that = (ResourceFontDataInputSource) o;

    if ( !loader.equals( that.loader ) ) {
      return false;
    }
    if ( !source.equals( that.source ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = loader.hashCode();
    result = 29 * result + source.hashCode();
    return result;
  }
}
