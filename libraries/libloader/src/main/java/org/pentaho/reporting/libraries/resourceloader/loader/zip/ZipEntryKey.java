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
