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
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

/**
 * An external zip key.
 *
 * @author Thomas Morgner
 */
public class ZipEntryKey implements Serializable {
  private ResourceData zipFile;
  private String entryName;

  public ZipEntryKey( final ResourceData zipFile,
                      final String entryName ) {
    if ( zipFile == null ) {
      throw new NullPointerException();
    }
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    this.zipFile = zipFile;
    this.entryName = entryName;
  }

  public ZipEntryKey( final ResourceManager manager,
                      final String zipFile,
                      final String entryName )
    throws ResourceKeyCreationException, ResourceLoadingException {
    if ( zipFile == null ) {
      throw new NullPointerException();
    }
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    this.zipFile = manager.load( manager.createKey( zipFile ) );
    this.entryName = entryName;
  }


  public ZipEntryKey( final ResourceManager manager,
                      final URL zipFile,
                      final String entryName )
    throws ResourceKeyCreationException, ResourceLoadingException {
    if ( zipFile == null ) {
      throw new NullPointerException();
    }
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    this.zipFile = manager.load( manager.createKey( zipFile ) );
    this.entryName = entryName;
  }

  public ZipEntryKey( final ResourceManager manager,
                      final File zipFile,
                      final String entryName )
    throws ResourceKeyCreationException, ResourceLoadingException {
    if ( zipFile == null ) {
      throw new NullPointerException();
    }
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    this.zipFile = manager.load( manager.createKey( zipFile ) );
    this.entryName = entryName;
  }

  public ResourceData getZipFile() {
    return zipFile;
  }

  public String getEntryName() {
    return entryName;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ZipEntryKey that = (ZipEntryKey) o;

    if ( !entryName.equals( that.entryName ) ) {
      return false;
    }
    if ( !zipFile.getKey().equals( that.zipFile.getKey() ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = zipFile.getKey().hashCode();
    result = 29 * result + entryName.hashCode();
    return result;
  }
}
