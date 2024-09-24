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

package org.pentaho.reporting.libraries.resourceloader.loader.raw;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Creation-Date: 12.04.2006, 15:06:48
 *
 * @author Thomas Morgner
 */
public class RawResourceData implements ResourceData, Serializable {
  private static final long serialVersionUID = 1L;
  private ResourceKey rawKey;
  private byte[] data;
  private transient volatile Long hashCode;

  public RawResourceData( final ResourceKey rawKey ) {
    if ( rawKey == null ) {
      throw new NullPointerException();
    }
    this.rawKey = rawKey;
    final byte[] data = (byte[]) rawKey.getIdentifier();
    this.data = (byte[]) data.clone();
  }

  public byte[] getResource( final ResourceManager caller )
    throws ResourceLoadingException {
    return (byte[]) data.clone();
  }

  public long getLength() {
    return data.length;
  }

  public InputStream getResourceAsStream( final ResourceManager caller )
    throws ResourceLoadingException {
    return new ByteArrayInputStream( data );
  }

  /**
   * Tries to read data into the given byte-array.
   *
   * @param caller
   * @param target
   * @param offset
   * @param length
   * @return the number of bytes read or -1 if no more data can be read.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException
   */
  public int getResource( final ResourceManager caller,
                          final byte[] target,
                          final long offset,
                          final int length ) throws ResourceLoadingException {
    if ( offset > data.length ) {
      return -1;
    }

    final int iOffset = (int) ( 0x7FFFFFFF & offset );
    final int remaining = data.length - iOffset;
    final int maxReadable = Math.min( target.length, Math.min( remaining, length ) );

    System.arraycopy( data, iOffset, target, 0, maxReadable );
    return maxReadable;
  }

  /**
   * We do not support attributes.
   *
   * @param key
   * @return
   */
  public Object getAttribute( final String key ) {
    return null;
  }

  public ResourceKey getKey() {
    return rawKey;
  }

  public long getVersion( final ResourceManager caller )
    throws ResourceLoadingException {
    if ( hashCode == null ) {
      this.hashCode = new Long( Arrays.hashCode( data ) & 0xFFFFFFFFL );
    }
    return hashCode.longValue();
  }
}
