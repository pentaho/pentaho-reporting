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


package org.pentaho.reporting.libraries.pixie;

import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Creation-Date: 13.05.2006, 09:47:26
 *
 * @author Thomas Morgner
 */
public class PixieViewer extends JFrame {
  public PixieViewer( final String filename ) throws IOException {
    final WmfFile wmf = new WmfFile( filename, 800, 600 );
    System.out.println( wmf );
    final Image img = wmf.replay();
    setContentPane( new JLabel( new ImageIcon( img ) ) );
  }

  public static void main( final String[] args )
    throws IOException {
    if ( args.length == 0 ) {
      System.err.println( "Need a file parameter." );
      System.exit( 1 );
    }

    final PixieViewer viewer = new PixieViewer( args[ 0 ] );
    viewer.pack();
    viewer.setVisible( true );
  }
}
