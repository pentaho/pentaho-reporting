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

package org.pentaho.reporting.designer.core.status;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;

import javax.swing.*;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class MemoryStatusGadget extends JLabel {

  private static final Color BG = SystemColor.textHighlight;

  private double tm;
  private double fm;

  private Timer timer;

  public MemoryStatusGadget() {
    if ( MacOSXIntegration.MAC_OS_X ) {
      setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 10 ),
        BorderFactory.createLineBorder( SystemColor.controlShadow ) ) );
    } else {
      setBorder( BorderFactory.createLineBorder( SystemColor.controlShadow ) );
    }
    setFont(
      StyleContext.getDefaultStyleContext().getFont( getFont().getName(), Font.PLAIN, getFont().getSize() - 2 ) );
    setHorizontalAlignment( JLabel.CENTER );

    timer = new Timer( 500, new MemoryStatusUpdateAction() );
    timer.setRepeats( true );
    timer.start();

    addMouseListener( new GarbageCollectorAction() );
  }


  @Override
  protected void paintComponent( final Graphics g ) {
    final Color origColor = g.getColor();

    g.setColor( SystemColor.control );
    g.fillRect( 0, 0, getWidth(), getHeight() );
    final int w = (int) ( getWidth() * ( ( tm - fm ) / tm ) );
    g.setColor( BG );
    g.fillRect( 0, 0, w, getHeight() );

    g.setColor( origColor );
    super.paintComponent( g );
  }


  public void dispose() {
    timer.stop();
  }

  private static class GarbageCollectorAction extends MouseAdapter {
    @Override
    public void mouseClicked( final MouseEvent e ) {
      System.gc();
    }
  }

  private class MemoryStatusUpdateAction implements ActionListener {
    public void actionPerformed( final ActionEvent e ) {
      final long totalMemory = Runtime.getRuntime().totalMemory();
      final long freeMemory = Runtime.getRuntime().freeMemory();
      tm = ( totalMemory / ( 1024. * 1024 ) );
      fm = ( freeMemory / ( 1024. * 1024 ) );

      setText( Messages.getString( "MemoryStatusGadget.Text", Double.valueOf( tm - fm ), Double.valueOf( tm ) ) );
    }
  }

}
