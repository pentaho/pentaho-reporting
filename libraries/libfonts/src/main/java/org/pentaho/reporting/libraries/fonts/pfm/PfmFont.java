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

package org.pentaho.reporting.libraries.fonts.pfm;

import org.pentaho.reporting.libraries.fonts.encoding.EncodingUtility;
import org.pentaho.reporting.libraries.fonts.io.FileFontDataInputSource;
import org.pentaho.reporting.libraries.fonts.io.FontDataInputSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Creation-Date: 21.07.2007, 15:27:07
 *
 * @author Thomas Morgner
 */
public class PfmFont {
  private String filename;
  private FontDataInputSource input;
  private transient byte[] readBuffer;
  private PfmFontHeader header;
  private PfmExtension extension;
  private String fontName;
  private String familyName;
  private boolean embeddable;


  public PfmFont( final File font, final boolean embeddable ) throws IOException {
    this( new FileFontDataInputSource( font ), embeddable );
  }

  public PfmFont( final FontDataInputSource input, final boolean embeddable ) throws IOException {
    this.embeddable = embeddable;
    this.input = input;
    this.filename = input.getFileName();
    this.header = new PfmFontHeader( readFully( 0, PfmFontHeader.LENGTH ) );
    this.extension = new PfmExtension( readFully( PfmFontHeader.LENGTH, PfmExtension.LENGTH ) );

    final int facePtr = this.header.getFacePtr();
    if ( facePtr == 0 ) {
      throw new IOException( "This font is not valid, it does not define a font-name" );
    }
    final byte[] familyNameRaw = readZString( facePtr );
    this.familyName = EncodingUtility.encode( familyNameRaw, header.getEncoding() );

    final int driverInfo = this.extension.getDriverInfo();
    if ( driverInfo > 0 ) {
      final byte[] fontName = readZString( driverInfo );
      this.fontName = EncodingUtility.encode( fontName, "ASCII" );
    } else {
      this.fontName = familyName;
    }

  }

  /**
   * IText's PFM reader is buggy. We have to check for the bug or we will run into troubles.
   *
   * @return
   */
  public boolean isItextCompatible() {
    final int driverInfo = this.extension.getDriverInfo();
    if ( driverInfo < 75 || driverInfo > 512 ) {
      embeddable = false;
      return false;
    }
    return true;
  }

  public String getFontName() {
    return fontName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public PfmFontHeader getHeader() {
    return header;
  }

  public String getFilename() {
    return filename;
  }

  public FontDataInputSource getInput() {
    return input;
  }

  public void dispose() {
    input.dispose();
  }

  protected byte[] readFully( final long offset, final int length )
    throws IOException {
    if ( readBuffer == null ) {
      readBuffer = new byte[ Math.max( 8192, length ) ];
    } else if ( readBuffer.length < length ) {
      readBuffer = new byte[ length ];
    }

    input.readFullyAt( offset, readBuffer, length );
    if ( ( readBuffer.length - length ) > 0 ) {
      Arrays.fill( readBuffer, length, readBuffer.length, (byte) 0 );
    }
    return readBuffer;
  }

  protected byte[] readZString( final long offset ) throws IOException {
    long position = offset;
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    int data;
    while ( ( data = input.readAt( position ) ) > 0 ) {
      bout.write( data );
      position += 1;
    }
    return bout.toByteArray();
  }

  public boolean isEmbeddable() {
    return embeddable;
  }
}
