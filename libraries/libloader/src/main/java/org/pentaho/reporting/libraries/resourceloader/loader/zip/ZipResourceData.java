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

package org.pentaho.reporting.libraries.resourceloader.loader.zip;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.AbstractResourceData;
import org.pentaho.reporting.libraries.resourceloader.loader.LoaderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Creation-Date: 05.04.2006, 15:44:07
 *
 * @author Thomas Morgner
 */
public class ZipResourceData extends AbstractResourceData {
  private ResourceKey key;
  private static final long serialVersionUID = -7432641415119820243L;

  public ZipResourceData( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    this.key = key;
  }

  public InputStream getResourceAsStream( final ResourceManager caller ) throws ResourceLoadingException {
    // again, this is going to hurt the performance.
    final ResourceKey parentKey = key.getParent();
    final ResourceData data = caller.load( parentKey );

    final ZipInputStream zin = new ZipInputStream( data.getResourceAsStream( caller ) );
    try {
      try {
        ZipEntry zipEntry = zin.getNextEntry();
        while ( zipEntry != null ) {
          if ( zipEntry.getName().equals( key.getIdentifier() ) == false ) {
            zipEntry = zin.getNextEntry();
            continue;
          }
          // read from here ..
          return zin;
        }
      } finally {
        zin.close();
      }
    } catch ( IOException e ) {
      throw new ResourceLoadingException
        ( "Reading the zip-file failed.", e );
    }
    throw new ResourceLoadingException
      ( "The zip-file did not contain the specified entry" );
  }

  public Object getAttribute( final String key ) {
    if ( key.equals( ResourceData.FILENAME ) ) {
      return LoaderUtils.getFileName( (String) this.key.getIdentifier() );
    }
    return null;
  }

  public ResourceKey getKey() {
    return key;
  }

  public long getVersion( final ResourceManager caller )
    throws ResourceLoadingException {
    final ResourceKey parentKey = key.getParent();
    final ResourceData data = caller.load( parentKey );
    return data.getVersion( caller );
  }
}
