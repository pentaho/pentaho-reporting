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


package org.pentaho.reporting.libraries.resourceloader;

import java.io.InputStream;

/**
 * A resource data object encapsulates the raw data of an resource at a given point in the past.
 * <p/>
 * Any change to the resource increases the version number. Version numbers are not needed to be checked regulary, but
 * must be checked on each call to 'getVersion()'.
 * <p/>
 * This definitly does *not* solve the problem of concurrent modifications; if you need to be sure that the resource has
 * not been altered between the last call to 'getVersion' and 'getResource..' external locking mechanism have to be
 * implemented.
 *
 * @author Thomas Morgner
 */
public interface ResourceData {
  public static final String CONTENT_LENGTH = "content-length";
  public static final String CONTENT_TYPE = "content-type";
  public static final String FILENAME = "filename";

  public InputStream getResourceAsStream( ResourceManager caller ) throws ResourceLoadingException;

  /**
   * This is dangerous, especially if the resource is large.
   *
   * @param caller
   * @return
   * @throws ResourceLoadingException
   */
  public byte[] getResource( ResourceManager caller ) throws ResourceLoadingException;

  /**
   * Tries to read data into the given byte-array.
   *
   * @param caller
   * @param target
   * @param offset
   * @param length
   * @return the number of bytes read or -1 if no more data can be read.
   * @throws ResourceLoadingException
   */
  public int getResource( ResourceManager caller, byte[] target, long offset, int length )
    throws ResourceLoadingException;

  public long getLength();

  public Object getAttribute( String key );

  public ResourceKey getKey();

  public long getVersion( ResourceManager caller ) throws ResourceLoadingException;

}
