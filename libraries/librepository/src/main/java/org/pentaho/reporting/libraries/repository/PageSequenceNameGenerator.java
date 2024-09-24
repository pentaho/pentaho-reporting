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

package org.pentaho.reporting.libraries.repository;

public class PageSequenceNameGenerator implements NameGenerator {
  private ContentLocation location;
  private String defaultNameHint;
  private String defaultSuffix;
  private int counter;

  public PageSequenceNameGenerator( final ContentLocation location ) {
    this( location, "file", null );
  }

  public PageSequenceNameGenerator( final ContentLocation location,
                                    final String defaultNameHint ) {
    if ( location == null ) {
      throw new NullPointerException();
    }
    if ( defaultNameHint == null ) {
      throw new NullPointerException();
    }

    this.location = location;

    // a leading point is not a sufix!
    final int pos = defaultNameHint.lastIndexOf( '.' );
    if ( defaultSuffix == null && pos > 0 ) {
      if ( pos < ( defaultNameHint.length() - 1 ) ) {
        this.defaultNameHint = defaultNameHint.substring( 0, pos );
        this.defaultSuffix = defaultNameHint.substring( pos + 1 );
      } else {
        this.defaultNameHint = defaultNameHint.substring( 0, pos );
        this.defaultSuffix = null;
      }
    } else {
      this.defaultNameHint = defaultNameHint;
      this.defaultSuffix = null;
    }
  }

  public PageSequenceNameGenerator( final ContentLocation location,
                                    final String defaultNameHint,
                                    final String defaultSuffix ) {
    if ( location == null ) {
      throw new NullPointerException();
    }
    if ( defaultNameHint == null ) {
      throw new NullPointerException();
    }

    this.location = location;
    this.defaultNameHint = defaultNameHint;
    this.defaultSuffix = defaultSuffix;
  }

  /**
   * Generates a new, unique name for storing resources in the output repository. Assuming that proper synchronization
   * has been applied, the generated name will be unique within that repository.
   *
   * @param nameHint a user defined name for that resource.
   * @param mimeType the mime type of the resource to be stored in the repository.
   * @return the generated, fully qualified name.
   */
  public String generateName( final String nameHint, final String mimeType )
    throws ContentIOException {
    final String name;
    if ( nameHint != null ) {
      name = nameHint;
    } else {
      name = defaultNameHint;
    }

    final String suffix;
    if ( defaultSuffix != null ) {
      suffix = defaultSuffix;
    } else {
      suffix = getSuffixForType( mimeType, location );
    }

    final String filename = name + '-' + counter + '.' + suffix;
    if ( location.exists( filename ) == false ) {
      counter += 1;
      return filename;
    }
    throw new ContentIOException(
      "Unable to generate page-sequence: A file with the next expected name already exists." );
  }

  private String getSuffixForType( final String mimeType,
                                   final ContentLocation location ) {
    final Repository repository = location.getRepository();
    final MimeRegistry mimeRegistry = repository.getMimeRegistry();
    return mimeRegistry.getSuffix( mimeType );
  }
}
