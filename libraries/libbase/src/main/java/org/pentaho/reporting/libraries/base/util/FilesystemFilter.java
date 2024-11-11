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


package org.pentaho.reporting.libraries.base.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;

/**
 * A filesystem filter.
 *
 * @author David Gilbert
 */
public class FilesystemFilter extends FileFilter implements FilenameFilter {

  /**
   * The file extension, which should be accepted.
   */
  private String[] fileext;
  /**
   * The filter description.
   */
  private String descr;
  /**
   * A flag indicating whether to accept directories.
   */
  private boolean accDirs;

  /**
   * Creates a new filter.
   *
   * @param fileext the file extension.
   * @param descr   the description.
   */
  public FilesystemFilter( final String fileext, final String descr ) {
    this( fileext, descr, true );
  }

  /**
   * Creates a new filter.
   *
   * @param fileext the file extension.
   * @param descr   the description.
   * @param accDirs accept directories?
   */
  public FilesystemFilter( final String fileext, final String descr,
                           final boolean accDirs ) {
    this( new String[] { fileext }, descr, accDirs );
  }

  /**
   * Creates a new filter.
   *
   * @param fileext the file extension.
   * @param descr   the description.
   * @param accDirs accept directories?
   * @throws NullPointerException if the file extensions are null.
   */
  public FilesystemFilter( final String[] fileext, final String descr,
                           final boolean accDirs ) {
    this.fileext = fileext.clone();
    this.descr = descr;
    this.accDirs = accDirs;
  }

  /**
   * Returns <code>true</code> if the file is accepted, and <code>false</code> otherwise.
   *
   * @param dir  the directory.
   * @param name the file name.
   * @return A boolean.
   */
  public boolean accept( final File dir, final String name ) {
    final File f = new File( dir, name );
    if ( f.isDirectory() && acceptsDirectories() ) {
      return true;
    }

    for ( int i = 0; i < fileext.length; i++ ) {
      if ( name.endsWith( this.fileext[ i ] ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns <code>true</code> if the specified file matches the requirements of this filter, and <code>false</code>
   * otherwise.
   *
   * @param dir the file or directory.
   * @return A boolean.
   */
  public boolean accept( final File dir ) {
    if ( dir.isDirectory() && acceptsDirectories() ) {
      return true;
    }

    for ( int i = 0; i < fileext.length; i++ ) {
      if ( dir.getName().endsWith( this.fileext[ i ] ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the filter description.
   *
   * @return The filter description.
   */
  public String getDescription() {
    return this.descr;
  }

  /**
   * Sets the flag that controls whether or not the filter accepts directories.
   *
   * @param b a boolean.
   */
  public void acceptDirectories( final boolean b ) {
    this.accDirs = b;
  }

  /**
   * Returns the flag that indicates whether or not the filter accepts directories.
   *
   * @return A boolean.
   */
  public boolean acceptsDirectories() {
    return this.accDirs;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FilesystemFilter that = (FilesystemFilter) o;

    if ( accDirs != that.accDirs ) {
      return false;
    }
    if ( descr != null ? !descr.equals( that.descr ) : that.descr != null ) {
      return false;
    }
    if ( !ObjectUtilities.equalArray( fileext, that.fileext ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = ObjectUtilities.hashCode( fileext );
    result = 31 * result + ( descr != null ? descr.hashCode() : 0 );
    result = 31 * result + ( accDirs ? 1 : 0 );
    return result;
  }
}
