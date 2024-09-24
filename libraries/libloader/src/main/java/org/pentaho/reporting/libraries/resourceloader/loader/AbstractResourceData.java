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

package org.pentaho.reporting.libraries.resourceloader.loader;

import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Creation-Date: 05.04.2006, 15:24:47
 *
 * @author Thomas Morgner
 */
public abstract class AbstractResourceData implements ResourceData, Serializable {
  private static final long serialVersionUID = -2578855461270413802L;

  protected AbstractResourceData() {
  }

  public byte[] getResource( final ResourceManager caller )
    throws ResourceLoadingException {
    try {
      final InputStream in = getResourceAsStream( caller );
      if ( in == null ) {
        throw new ResourceLoadingException( "Unable to read Stream: No input stream: " + getKey() );
      }
      try {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        IOUtils.getInstance().copyStreams( in, bout );
        return bout.toByteArray();
      } finally {
        in.close();
      }
    } catch ( ResourceLoadingException rle ) {
      throw rle;
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Unable to read Stream: ", e );
    }
  }

  public long getLength() {
    final Object attribute = getAttribute( CONTENT_LENGTH );
    if ( attribute instanceof Number ) {
      final Number length = (Number) attribute;
      return length.longValue();
    }
    return -1;
  }

  public int getResource( final ResourceManager caller,
                          final byte[] target,
                          final long offset,
                          final int length ) throws ResourceLoadingException {
    try {
      if ( target == null ) {
        throw new NullPointerException();
      }
      if ( target.length < length ) {
        throw new IndexOutOfBoundsException( "Requested end-position is greater than " );
      }

      final InputStream in = getResourceAsStream( caller );
      if ( in == null ) {
        throw new ResourceLoadingException( "Unable to read Stream: No input stream: " + getKey() );
      }
      try {
        if ( offset > 0 ) {
          long toBeSkipped = offset;
          long skipResult = in.skip( toBeSkipped );
          toBeSkipped -= skipResult;
          while ( skipResult > 0 && toBeSkipped > 0 ) {
            skipResult = in.skip( offset );
            toBeSkipped -= skipResult;
          }

          if ( toBeSkipped > 0 ) {
            // failed to read up to the offset ..
            throw new ResourceLoadingException
              ( "Unable to read Stream: Skipping content failed: " + getKey() );
          }
        }

        int bytesToRead = length;
        // the input stream does not supply accurate available() data
        // the zip entry does not know the size of the data
        int bytesRead = in.read( target, length - bytesToRead, bytesToRead );
        while ( bytesRead > -1 && bytesToRead > 0 ) {
          bytesToRead -= bytesRead;
          bytesRead = in.read( target, length - bytesToRead, bytesToRead );
        }
        return length - bytesRead;
      } finally {
        in.close();
      }
    } catch ( ResourceLoadingException rle ) {
      throw rle;
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Unable to read Stream: ", e );
    }
  }
}
