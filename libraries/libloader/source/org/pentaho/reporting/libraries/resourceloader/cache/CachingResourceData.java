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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.cache;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A very simple implementation which is suitable for smaller objects. The complete data is read into memory.
 *
 * @author Thomas Morgner
 */
public class CachingResourceData implements ResourceData, Serializable
{
  private static final int CACHE_THRESHOLD = 512 * 1024;

  private ResourceData data;
  private HashMap attributes;
  // The cached raw data. This is stored on the serialized stream as well
  // so that we can cache that stuff.
  private transient byte[] rawData;
  private static final long serialVersionUID = -1749415356208356705L;

  public CachingResourceData(final ResourceData data)
  {
    if (data == null)
    {
      throw new NullPointerException();
    }
    this.data = data;
  }

  public InputStream getResourceAsStream(final ResourceManager caller) throws ResourceLoadingException
  {
    final byte[] data = getResource(caller);
    return new ByteArrayInputStream(data);
  }

  public long getLength()
  {
    if (rawData != null)
    {
      return rawData.length;
    }
    return data.getLength();
  }

  public synchronized byte[] getResource(final ResourceManager caller) throws ResourceLoadingException
  {
    if (rawData == null)
    {
      rawData = data.getResource(caller);
    }
    return (byte[]) rawData.clone();
  }

  public synchronized int getResource
      (final ResourceManager caller, final byte[] target, final long offset, final int length)
      throws ResourceLoadingException
  {
    if (target == null)
    {
      throw new NullPointerException();
    }
    if (target.length < (offset + length))
    {
      throw new IndexOutOfBoundsException();
    }

    if (rawData == null)
    {
      rawData = data.getResource(caller);
    }

    final int iOffset = (int) (0x7FFFFFFF & offset);
    final int maxLength = Math.min(rawData.length - iOffset, length);
    if (maxLength <= 0)
    {
      return -1;
    }

    System.arraycopy(rawData, iOffset, target, 0, maxLength);
    return maxLength;
  }

  public synchronized Object getAttribute(final String key)
  {
    if (attributes == null)
    {
      attributes = new HashMap();
    }
    else
    {
      final Object cached = attributes.get(key);
      if (cached != null)
      {
        return cached;
      }
    }

    final Object value = data.getAttribute(key);
    if (value != null)
    {
      attributes.put(key, value);
    }
    return value;
  }

  public ResourceKey getKey()
  {
    return data.getKey();
  }

  public long getVersion(final ResourceManager caller)
      throws ResourceLoadingException
  {
    return data.getVersion(caller);
  }

  public static ResourceData createCached(final ResourceData data)
  {
    // this relieves the pain of having to re-open the same stream more than
    // once. This is no real long term caching, but at least a caching during
    // the current request.
    final Object rawCl = data.getAttribute(ResourceData.CONTENT_LENGTH);
    if (rawCl instanceof Number)
    {
      final Number contentLength = (Number) rawCl;
      if (contentLength.intValue() < CACHE_THRESHOLD)
      {
        // only buffer all data if the content is less than 512kb.
        // Else, we may run into trouble if we try to load a huge item into memory ..
        return new CachingResourceData(data);
      }
    }
    return data;
  }
}
