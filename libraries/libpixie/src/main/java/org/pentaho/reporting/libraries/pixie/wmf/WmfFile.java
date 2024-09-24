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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.pixie.wmf.records.CommandFactory;
import org.pentaho.reporting.libraries.pixie.wmf.records.MfCmd;
import org.pentaho.reporting.libraries.pixie.wmf.records.MfCmdSetWindowExt;
import org.pentaho.reporting.libraries.pixie.wmf.records.MfCmdSetWindowOrg;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Parses and replays the WmfFile.
 */
public class WmfFile {
  private static final Log logger = LogFactory.getLog( WmfFile.class );

  public static final int QUALITY_NO = 0;    // Can't convert.
  public static final int QUALITY_MAYBE = 1; // Might be able to convert.
  public static final int QUALITY_YES = 2;   // Can convert.

  // Maximal picture size is 1200x1200. A average wmf file scales easily
  // to 20000 and more, so we have to limit the pixel image's size.

  private static final int MAX_PICTURE_SIZE = getMaxPictureSize();

  private static int getMaxPictureSize() {
    return 1200;
  }

  private WmfObject[] objects;
  private FastStack dcStack;
  private MfPalette palette;

  //private String inName;
  private InputStream in;
  private MfHeader header;
  private int fileSize;
  private int filePos;

  private ArrayList records;
  private Graphics2D graphics;

  private int maxWidth;
  private int maxHeight;
  private int imageWidth;
  private int imageHeight;

  private int minX;
  private int minY;
  private int imageX;
  private int imageY;

  /**
   * Initialize metafile for reading from an URL. Width and height will be computed automatically.
   *
   * @param input the URL from where to read.
   * @throws IOException if any other error occured.
   */
  public WmfFile( final URL input )
    throws IOException {
    this( input, -1, -1 );
  }

  /**
   * Initialize metafile for reading from file. Width and height will be computed automatically.
   *
   * @param input the name of the file from where to read.
   * @throws IOException if any other error occured.
   */
  public WmfFile( final String input )
    throws IOException {
    this( input, -1, -1 );
  }

  /**
   * Initialize metafile for reading from an URL.
   *
   * @param imageWidth  the target width of the image or -1 for automatic mode.
   * @param imageHeight the target height of the image or -1 for automatic mode.
   * @param input       the URL from where to read.
   * @throws IOException if any other error occured.
   */
  public WmfFile( final URL input, final int imageWidth, final int imageHeight )
    throws IOException {
    this( new BufferedInputStream( input.openStream() ), imageWidth, imageHeight );
  }

  /**
   * Initialize metafile for reading from filename.
   *
   * @param imageWidth  the target width of the image or -1 for automatic mode.
   * @param imageHeight the target height of the image or -1 for automatic mode.
   * @param inName      the file name from where to read.
   * @throws FileNotFoundException if the file was not found.
   * @throws IOException           if any other error occured.
   */
  public WmfFile( final String inName, final int imageWidth, final int imageHeight )
    throws FileNotFoundException, IOException {
    this( new BufferedInputStream( new FileInputStream( inName ) ), imageWidth, imageHeight );
  }

  /**
   * Initialize metafile for reading from the given input stream.
   *
   * @param imageWidth  the target width of the image or -1 for automatic mode.
   * @param imageHeight the target height of the image or -1 for automatic mode.
   * @param in          the stream from where to read.
   * @throws IOException if any other error occured.
   */
  public WmfFile( final InputStream in, final int imageWidth,
                  final int imageHeight )
    throws IOException {
    this.in = in;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    records = new ArrayList();
    dcStack = new FastStack( 100 );
    palette = new MfPalette();
    readHeader();
    parseRecords();
    resetStates();
  }

  public Dimension getImageSize() {
    return new Dimension( maxWidth, maxHeight );
  }

  private void resetStates() {
    Arrays.fill( objects, null );
    dcStack.clear();
    dcStack.push( new MfDcState( this ) );
  }

  public MfPalette getPalette() {
    return palette;
  }

  /**
   * Return Placeable and Windows headers that were read earlier.
   *
   * @return the meta-file header.
   */
  public MfHeader getHeader() {
    return header;
  }

  public Graphics2D getGraphics2D() {
    return graphics;
  }

  /**
   * Check class invariant.
   */
  private void assertValid() {
    if ( filePos < 0 || filePos > fileSize ) {
      throw new IllegalStateException( "WmfFile is not valid" );
    }
  }

  /**
   * Read Placeable and Windows headers.
   */
  private MfHeader readHeader()
    throws IOException {
    header = new MfHeader();
    header.read( in );
    if ( header.isValid() ) {
      fileSize = header.getFileSize();
      objects = new WmfObject[ header.getObjectsSize() ];
      filePos = header.getHeaderSize();
      return header;
    } else {
      throw new IOException( "The given file is not a real metafile" );
    }
  }

  /**
   * Fetch a record.
   *
   * @return the next record read or null, if the end-of-file has been reached.
   * @throws IOException if an IO-Error occurs.
   */
  private MfRecord readNextRecord()
    throws IOException {
    if ( filePos >= fileSize ) {
      return null;
    }

    assertValid();

    final MfRecord record = new MfRecord( in );
    filePos += record.getLength();
    return record;
  }

  /**
   * Read and interpret the body of the metafile.
   *
   * @throws IOException if an IO-Error occurs.
   */
  private void parseRecords() throws IOException {
    minX = Integer.MAX_VALUE;
    minY = Integer.MAX_VALUE;
    maxWidth = 0;
    maxHeight = 0;

    final CommandFactory cmdFactory = CommandFactory.getInstance();
    MfRecord mf;
    while ( ( mf = readNextRecord() ) != null ) {
      final MfCmd cmd = cmdFactory.getCommand( mf.getType() );
      if ( cmd == null ) {
        logger.info( "Failed to parse record " + mf.getType() );
      } else {
        cmd.setRecord( mf );

        if ( cmd.getFunction() == MfType.SET_WINDOW_ORG ) {
          final MfCmdSetWindowOrg worg = (MfCmdSetWindowOrg) cmd;
          final Point p = worg.getTarget();
          minX = Math.min( p.x, minX );
          minY = Math.min( p.y, minY );
        } else if ( cmd.getFunction() == MfType.SET_WINDOW_EXT ) {
          final MfCmdSetWindowExt worg = (MfCmdSetWindowExt) cmd;
          final Dimension d = worg.getDimension();
          maxWidth = Math.max( maxWidth, d.width );
          maxHeight = Math.max( maxHeight, d.height );
        }
        records.add( cmd );
      }
    }
    in.close();
    in = null;

    // make sure that we don't have invalid values in case no
    // setWindow records were found ...
    if ( minX == Integer.MAX_VALUE ) {
      minX = 0;
    }
    if ( minY == Integer.MAX_VALUE ) {
      minY = 0;
    }

    //System.out.println(records.size() + " records read");
    //System.out.println("Image Extends: " + maxWidth + " " + maxHeight);
    if ( imageWidth < 1 || imageHeight < 1 ) {
      scaleToFit( MAX_PICTURE_SIZE, MAX_PICTURE_SIZE );
    } else {
      scaleToFit( imageWidth, imageHeight );
    }
  }

  /**
   * Scales the WMF-image to the given width and height while preserving the aspect ration.
   *
   * @param fitWidth  the target width.
   * @param fitHeight the target height.
   */
  public void scaleToFit( final float fitWidth, final float fitHeight ) {
    final float percentX = ( fitWidth * 100 ) / maxWidth;
    final float percentY = ( fitHeight * 100 ) / maxHeight;
    scalePercent( percentX < percentY ? percentX : percentY );
  }

  /**
   * Scale the image to a certain percentage.
   *
   * @param percent the scaling percentage <!-- Yes, this is from iText lib -->
   */
  public void scalePercent( final float percent ) {
    scalePercent( percent, percent );
  }

  /**
   * Scale the width and height of an image to a certain percentage.
   *
   * @param percentX the scaling percentage of the width
   * @param percentY the scaling percentage of the height <!-- Yes, this is from iText lib -->
   */
  public void scalePercent( final float percentX, final float percentY ) {
    imageWidth = (int) ( ( maxWidth * percentX ) / 100f );
    imageHeight = (int) ( ( maxHeight * percentY ) / 100f );
    imageX = (int) ( ( minX * percentX ) / 100f );
    imageY = (int) ( ( minY * percentY ) / 100f );
  }

  public static void main( final String[] args )
    throws Exception {
    final WmfFile wmf = new WmfFile( "./head/pixie/res/a0.wmf", 800, 600 );
    wmf.replay();
    // System.out.println(wmf.imageWidth + ", " + wmf.imageHeight);
  }

  public MfDcState getCurrentState() {
    return (MfDcState) dcStack.peek();
  }

  // pushes a state on the stack
  public void saveDCState() {
    final MfDcState currentState = getCurrentState();
    dcStack.push( new MfDcState( currentState ) );

  }

  public int getStateCount() {
    return dcStack.size();
  }

  /**
   * Restores a state. The stateCount specifies the number of states to discard to find the correct one.
   *
   * @param stateCount the state count.
   */
  public void restoreDCState( final int stateCount ) {
    if ( ( stateCount > 0 ) == false ) {
      throw new IllegalArgumentException();
    }
    // this is contrary to Caolans description of the WMF file format, but
    // Batik also ignores the stateCount parameter.
    dcStack.pop();
    getCurrentState().restoredState();
  }

  /**
   * Return the next free slot from the objects table.
   *
   * @return the next new free slot in the objects-registry
   */
  protected int findFreeSlot() {
    for ( int slot = 0; slot < objects.length; slot++ ) {
      if ( objects[ slot ] == null ) {
        return slot;
      }
    }

    throw new IllegalStateException( "No free slot" );
  }


  public void storeObject( final WmfObject o ) {
    final int idx = findFreeSlot();
    objects[ idx ] = o;
  }

  public void deleteObject( final int slot ) {
    if ( ( slot < 0 ) || ( slot >= objects.length ) ) {
      throw new IllegalArgumentException( "Range violation" );
    }

    objects[ slot ] = null;
  }

  public WmfObject getObject( final int slot ) {
    if ( ( slot < 0 ) || ( slot >= objects.length ) ) {
      throw new IllegalStateException( "Range violation" );
    }

    return objects[ slot ];
  }

  public MfLogBrush getBrushObject( final int slot ) {
    final WmfObject obj = getObject( slot );
    if ( obj.getType() == WmfObject.OBJ_BRUSH ) {
      return (MfLogBrush) obj;
    }
    throw new IllegalStateException( "Object " + slot + " was no brush" );
  }

  public MfLogPen getPenObject( final int slot ) {
    final WmfObject obj = getObject( slot );
    if ( obj.getType() == WmfObject.OBJ_PEN ) {
      return (MfLogPen) obj;
    }
    throw new IllegalStateException( "Object " + slot + " was no pen" );
  }

  public MfLogRegion getRegionObject( final int slot ) {
    final WmfObject obj = getObject( slot );
    if ( obj.getType() == WmfObject.OBJ_REGION ) {
      return (MfLogRegion) obj;
    }
    throw new IllegalStateException( "Object " + slot + " was no region" );
  }

  public synchronized BufferedImage replay() {
    return replay( imageWidth, imageHeight );
  }


  public synchronized BufferedImage replay( final int imageX, final int imageY ) {
    final BufferedImage image = new BufferedImage( imageX, imageY, BufferedImage.TYPE_INT_ARGB );
    final Graphics2D graphics = image.createGraphics();

    // clear the image area ...
    graphics.setPaint( new Color( 0, 0, 0, 0 ) );
    graphics.fill( new Rectangle( 0, 0, imageX, imageY ) );

    draw( graphics, new Rectangle2D.Float( 0, 0, imageX, imageY ) );
    graphics.dispose();
    return image;
  }

  public synchronized void draw( final Graphics2D graphics, final Rectangle2D bounds ) {

    // this adjusts imageWidth and imageHeight
    scaleToFit( (float) bounds.getWidth(), (float) bounds.getHeight() );
    // adjust translation if needed ...
    graphics.translate( bounds.getX(), bounds.getY() );
    // adjust to the image origin
    graphics.translate( -imageX, -imageY );

    this.graphics = graphics;

    for ( int i = 0; i < records.size(); i++ ) {
      try {
        final MfCmd command = (MfCmd) records.get( i );
        command.setScale( (float) imageWidth / (float) maxWidth, (float) imageHeight / (float) maxHeight );
        command.replay( this );
      } catch ( Exception e ) {
        logger.warn( "Error while processing image record #" + i, e );
      }
    }
    resetStates();
  }

  /**
   * Returns the preferred size of the drawable. If the drawable is aspect ratio aware, these bounds should be used to
   * compute the preferred aspect ratio for this drawable.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize() {
    return new Dimension( imageWidth, imageHeight );
  }

  /**
   * Returns true, if this drawable will preserve an aspect ratio during the drawing.
   *
   * @return true, if an aspect ratio is preserved, false otherwise.
   */
  public boolean isPreserveAspectRatio() {
    return true;
  }

  public String toString() {
    final StringBuffer bo = new StringBuffer( 500 );
    bo.append( "WmfFile={width=" );
    bo.append( imageWidth );
    bo.append( ", height=" );
    bo.append( imageHeight );
    bo.append( ", recordCount=" );
    bo.append( records.size() );
    bo.append( ", records={\n" );
    for ( int i = 0; i < records.size(); i++ ) {
      final MfCmd cmd = (MfCmd) records.get( i );
      bo.append( i );
      bo.append( ',' );
      bo.append( cmd.toString() );
      bo.append( '\n' );
    }
    bo.append( "}\n" );
    return bo.toString();
  }
}
