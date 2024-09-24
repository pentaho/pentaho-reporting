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

package org.pentaho.reporting.libraries.pixie.wmf;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Implements the ImageProducer interface for the MetaFiles
 */
public class WmfImageProducer implements ImageProducer {
  private WmfFile metafile;
  private ArrayList consumers;

  public WmfImageProducer( final String inName, final int width, final int height )
    throws IOException {
    consumers = new ArrayList();
    metafile = new WmfFile( inName, width, height );
    //    metafile.replay ();
  }

  public WmfImageProducer( final URL inName, final int width, final int height )
    throws IOException {
    consumers = new ArrayList();
    metafile = new WmfFile( inName, width, height );
    //    metafile.replay ();
  }

  public WmfImageProducer( final URL inName )
    throws IOException {
    consumers = new ArrayList();
    metafile = new WmfFile( inName );
    //    metafile.replay ();
  }

  public synchronized void addConsumer( final ImageConsumer ic ) {
    if ( isConsumer( ic ) ) {
      return;
    }

    consumers.add( ic );
  }


  public synchronized boolean isConsumer( final ImageConsumer ic ) {
    return consumers.contains( ic );
  }


  public synchronized void removeConsumer( final ImageConsumer ic ) {
    consumers.remove( ic );
  }


  public synchronized void requestTopDownLeftRightResend( final ImageConsumer ic ) {
    startProduction( ic );
  }


  public synchronized void startProduction( final ImageConsumer pic ) {
    if ( pic != null ) {
      addConsumer( pic );
    }

    final ImageConsumer[] cons = (ImageConsumer[]) consumers.toArray( new ImageConsumer[ consumers.size() ] );
    final BufferedImage image = metafile.replay();

    final int w = image.getWidth();
    final int h = image.getHeight();
    final ColorModel model = image.getColorModel();

    for ( int i = 0; i < cons.length; i++ ) {
      final ImageConsumer ic = cons[ i ];
      ic.setHints( ImageConsumer.TOPDOWNLEFTRIGHT );
      ic.setHints( ImageConsumer.SINGLEFRAME );
      ic.setHints( ImageConsumer.SINGLEPASS );
      ic.setHints( ImageConsumer.COMPLETESCANLINES );
      ic.setDimensions( w, h );
      ic.setColorModel( model );
    }

    final int LINES = 10;
    int[] pixels = new int[ w * LINES ];

    for ( int i = 0; i < h; i += LINES ) {
      final int rows;
      if ( ( i + LINES ) > h ) {
        rows = h - i;
      } else {
        rows = LINES;
      }

      pixels = image.getRGB( 0, i, w, rows, pixels, 0, w );
      for ( int j = 0; j < cons.length; j++ ) {
        final ImageConsumer ic = cons[ j ];
        ic.setPixels( 0, i, w, rows, model, pixels, 0, w );
      }
    }

    for ( int i = 0; i < cons.length; i++ ) {
      final ImageConsumer ic = cons[ i ];
      ic.imageComplete( ImageConsumer.STATICIMAGEDONE );
    }

    if ( pic != null ) {
      removeConsumer( pic );
    }
  }
}
