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
