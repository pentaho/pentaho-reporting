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

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * This image observer blocks until the image is completely loaded. AWT defers the loading of images until they are
 * painted on a graphic.
 * <p/>
 * While printing reports it is not very nice, not to know whether a image was completely loaded, so this observer
 * forces the loading of the image until a final state (either ALLBITS, ABORT or ERROR) is reached.
 *
 * @author Thomas Morgner
 */
public class WaitingImageObserver implements ImageObserver {
  /**
   * A logger.
   */
  private static final Log LOGGER = LogFactory.getLog( WaitingImageObserver.class );

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -807204410581383550L;

  /**
   * The lock.
   */
  private boolean lock;

  /**
   * The image.
   */
  private Image image;

  /**
   * A flag that signals an error.
   */
  private boolean error;

  private long lastUpdate;

  // we better dont wait longer than two seconds for the image. This denotes the maximum time between two
  // updates, not the total loading time.
  private static final long MAX_LOADTIME_DEFAULT = 2000;
  private long maxLoadTime;

  /**
   * Creates a new <code>ImageObserver<code> for the given <code>Image<code>. The observer has to be started by an
   * external thread.
   *
   * @param image the image to observe (<code>null</code> not permitted).
   */
  public WaitingImageObserver( final Image image ) {
    this( image, MAX_LOADTIME_DEFAULT );
  }

  /**
   * Creates a new <code>ImageObserver<code> for the given <code>Image<code>. The observer has to be started by an
   * external thread.
   *
   * @param image the image to observe (<code>null</code> not permitted).
   */
  public WaitingImageObserver( final Image image, final long maxLoadTime ) {
    if ( image == null ) {
      throw new NullPointerException();
    }
    this.image = image;
    this.lock = true;
    this.maxLoadTime = maxLoadTime;
  }

  /**
   * Callback function used by AWT to inform that more data is available. The observer waits until either all data is
   * loaded or AWT signals that the image cannot be loaded.
   *
   * @param img       the image being observed.
   * @param infoflags the bitwise inclusive OR of the following flags:  <code>WIDTH</code>, <code>HEIGHT</code>,
   *                  <code>PROPERTIES</code>, <code>SOMEBITS</code>, <code>FRAMEBITS</code>, <code>ALLBITS</code>,
   *                  <code>ERROR</code>, <code>ABORT</code>.
   * @param x         the <i>x</i> coordinate.
   * @param y         the <i>y</i> coordinate.
   * @param width     the width.
   * @param height    the height.
   * @return <code>false</code> if the infoflags indicate that the image is completely loaded; <code>true</code>
   * otherwise.
   */
  public synchronized boolean imageUpdate( final Image img,
                                           final int infoflags,
                                           final int x,
                                           final int y,
                                           final int width,
                                           final int height ) {
    if ( img == null ) {
      throw new NullPointerException();
    }

    lastUpdate = System.currentTimeMillis();
    if ( ( infoflags & ImageObserver.ALLBITS ) == ImageObserver.ALLBITS ) {
      this.lock = false;
      this.error = false;
      notifyAll();
      return false;
    } else if ( ( infoflags & ImageObserver.FRAMEBITS ) == ImageObserver.FRAMEBITS ) {
      this.lock = false;
      this.error = false;
      notifyAll();
      return false;
    } else if ( ( infoflags & ImageObserver.ABORT ) == ImageObserver.ABORT
      || ( infoflags & ImageObserver.ERROR ) == ImageObserver.ERROR ) {
      this.lock = false;
      this.error = true;
      notifyAll();
      return false;
    }

    // maybe it is enough already to draw the image ..
    notifyAll();
    return true;
  }

  /**
   * The workerthread. Simply draws the image to a BufferedImage's Graphics-Object and waits for the AWT to load the
   * image.
   */
  public synchronized void waitImageLoaded() {

    if ( this.lock == false ) {
      return;
    }

    final BufferedImage img = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB );
    final Graphics g = img.getGraphics();

    try {
      while ( this.lock && error == false ) {
        lastUpdate = System.currentTimeMillis();
        if ( g.drawImage( this.image, 0, 0, img.getWidth( this ), img.getHeight( this ), this ) ) {
          return;
        }

        try {
          wait( 500 );
        } catch ( InterruptedException e ) {
          LOGGER.info( "WaitingImageObserver.waitImageLoaded(): InterruptedException thrown", e );
        }

        if ( lock == false ) {
          return;
        }

        if ( maxLoadTime > 0 && lastUpdate < ( System.currentTimeMillis() - maxLoadTime ) ) {
          error = true;
          lock = false;
          LOGGER.info( "WaitingImageObserver.waitImageLoaded(): Image loading reached timeout." );
          return;
        }
      }
    } finally {
      g.dispose();
    }
  }

  /**
   * Checks whether the loading is complete.
   *
   * @return true, if the loading is complete, false otherwise.
   */
  public boolean isLoadingComplete() {
    return this.lock == false;
  }

  /**
   * Returns true if there is an error condition, and false otherwise.
   *
   * @return A boolean.
   */
  public boolean isError() {
    return this.error;
  }
}
