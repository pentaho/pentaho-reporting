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

package org.pentaho.reporting.libraries.repository;

import java.security.SecureRandom;
import java.util.Random;

public class DefaultNameGenerator implements NameGenerator {
  private ContentLocation location;
  private String defaultNameHint;
  private String defaultSuffix;
  private Random randomGenerator;

  public DefaultNameGenerator( final ContentLocation location ) {
    this( location, "file", null );
  }

  public DefaultNameGenerator( final ContentLocation location,
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

  public DefaultNameGenerator( final ContentLocation location,
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

    final String firstFileName = name + '.' + suffix;
    if ( location.exists( firstFileName ) == false ) {
      return firstFileName;
    }

    if ( randomGenerator == null ) {
      randomGenerator = new SecureRandom();
    }
    while ( true ) {
      final int counter = randomGenerator.nextInt() & 0x7FFFFFFF;
      final String filename = name + counter + '.' + suffix;
      if ( location.exists( filename ) == false ) {
        return filename;
      }
    }
  }

  private String getSuffixForType( final String mimeType,
                                   final ContentLocation location ) {
    final Repository repository = location.getRepository();
    final MimeRegistry mimeRegistry = repository.getMimeRegistry();
    return mimeRegistry.getSuffix( mimeType );
  }
}
