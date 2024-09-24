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
