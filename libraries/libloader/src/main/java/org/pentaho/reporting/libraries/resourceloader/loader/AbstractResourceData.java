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
