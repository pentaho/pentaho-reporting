/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.repository.file;

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.repository.NameGenerator;

import java.io.File;
import java.io.IOException;

public class TempFileNameGenerator implements NameGenerator {
  private FileContentLocation fileContentLocation;

  public TempFileNameGenerator( final FileContentLocation fileContentLocation ) {
    if ( fileContentLocation == null ) {
      throw new NullPointerException();
    }
    this.fileContentLocation = fileContentLocation;
  }

  /**
   * Generates a new name for the location. The name-generator may use both the name-hint and mimetype to compute the
   * new name.
   *
   * @param nameHint the name hint, usually a identifier for the new filename (can be null).
   * @param mimeType the mime type of the new filename. Usually used to compute a suitable file-suffix.
   * @return the generated name, never null.
   * @throws org.pentaho.reporting.libraries.repository.ContentIOException if the name could not be generated for any
   *                                                                       reason.
   */
  public String generateName( final String nameHint, final String mimeType ) throws ContentIOException {
    final MimeRegistry mimeRegistry = fileContentLocation.getRepository().getMimeRegistry();
    final File targetDirectory = fileContentLocation.getBackend();
    final String suffix = mimeRegistry.getSuffix( mimeType );
    try {
      final File tempFile = File.createTempFile( nameHint, "." + suffix, targetDirectory );
      return tempFile.getName();
    } catch ( IOException e ) {
      throw new ContentIOException( "Unable to generate a name for the data file", e );
    }
  }
}
