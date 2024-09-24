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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pixie.wmf.bitmap;

import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DIBReader {
  private GDIPalette palette; // as GDI Color value
  private BitmapHeader header;

  public DIBReader() {
  }

  public BufferedImage setRecord( final MfRecord record )
    throws IOException {
    return setRecord( record, 0 );
  }

  public BufferedImage setRecord( final MfRecord record, final int offset )
    throws IOException {
    header = new BitmapHeader();
    header.setRecord( record, offset );
    palette = new GDIPalette();
    palette.setNoOfColors( header.getNoOfColors() );

    final int width = header.getWidth();
    final int height = header.getHeight();

    final int paletteStart = MfRecord.RECORD_HEADER_SIZE + header.getHeaderSize() + 4 + offset;
    final InputStream dataIn = record.getInputStream( paletteStart );
    palette.readPalette( dataIn );

    final int compression = header.getCompression();
    final BitmapCompression comHandler = BitmapCompressionFactory.getHandler( compression );
    comHandler.setDimension( width, height );
    comHandler.setBpp( header.getBitsPerPixel() );
    final int[] data = comHandler.decompress( dataIn, palette );

    final BufferedImage retval = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
    retval.setRGB( 0, 0, width, height, data, 0, width );
    return retval;
  }
}
