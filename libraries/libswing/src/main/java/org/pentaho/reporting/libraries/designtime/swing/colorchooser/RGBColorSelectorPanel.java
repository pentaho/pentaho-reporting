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

package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class RGBColorSelectorPanel extends JComponent {
  public enum ColorComponents {
    RED, GREEN, BLUE

  }


  private class MouseHandler extends MouseAdapter {
    private MouseHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      handleMouse( e );
    }

    private void handleMouse( final MouseEvent e ) {
      if ( e.getButton() != MouseEvent.BUTTON1 ) {
        return;
      }

      if ( getWidth() == 0 || getHeight() == 0 ) {
        return;
      }

      if ( e.getX() > getWidth() ) {
        return;
      }
      if ( e.getY() > getHeight() ) {
        return;
      }

      if ( getWidth() == 0 || getHeight() == 0 ) {
        return;
      }

      if ( colorSelectionModel == null ) {
        return;
      }

      final float x = e.getX() / (float) getWidth();
      final float y = ( e.getY() / (float) getHeight() );
      final int color = computeColor( getSelectedComponent(), (int) ( x * 255 ), (int) ( y * 255 ) );
      colorSelectionModel.setSelectedColor( new Color( color ) );
    }

    public void mouseDragged( final MouseEvent e ) {
      handleMouse( e );
    }
  }

  private class ColorSelectionHandler implements ChangeListener {
    private ColorSelectionHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      updateImage();
      repaint();
    }
  }

  private ColorSelectionHandler colorSelectionHandler;
  private ExtendedColorModel colorSelectionModel;
  private BufferedImage backend;
  private ColorComponents component;

  public RGBColorSelectorPanel() {
    backend = new BufferedImage( 256, 256, BufferedImage.TYPE_INT_RGB );
    component = ColorComponents.RED;
    colorSelectionHandler = new ColorSelectionHandler();
    addMouseListener( new MouseHandler() );
    addMouseMotionListener( new MouseHandler() );
    updateImage();
  }

  public ColorComponents getComponent() {
    return component;
  }

  public void setComponent( final ColorComponents component ) {
    if ( component == null ) {
      throw new NullPointerException();
    }
    this.component = component;
    updateImage();
    repaint();
  }

  private int computeColor( final int selectedValue, final int x, final int y ) {
    if ( component == ColorComponents.RED ) {
      return ( selectedValue & 0xFF ) << 16 | ( y & 0xFF ) << 8 | x & 0xFF;
    }
    if ( component == ColorComponents.GREEN ) {
      return ( x & 0xFF ) << 16 | ( selectedValue & 0xFF ) << 8 | y & 0xFF;
    }
    if ( component == ColorComponents.BLUE ) {
      return ( x & 0xFF ) << 16 | ( y & 0xFF ) << 8 | selectedValue & 0xFF;
    }
    throw new IllegalStateException();
  }

  private void updateImage() {
    final int selectedComponent = getSelectedComponent();
    for ( int x = 0; x < 256; x++ ) {
      for ( int y = 0; y < 256; y++ ) {
        backend.setRGB( x, y, computeColor( selectedComponent, x, y ) );
      }
    }
  }

  private int getSelectedComponent() {
    final int selectedComponent;
    final Color selectedValue = getSelectedColor();
    if ( selectedValue == null ) {
      selectedComponent = 128;
    } else {
      switch( component ) {
        case RED:
          selectedComponent = selectedValue.getRed();
          break;
        case GREEN:
          selectedComponent = selectedValue.getGreen();
          break;
        case BLUE:
          selectedComponent = selectedValue.getBlue();
          break;
        default:
          selectedComponent = 128;
      }
    }
    return selectedComponent;
  }

  private Color getSelectedColor() {
    if ( colorSelectionModel == null ) {
      return null;
    }
    return colorSelectionModel.getSelectedColor();
  }

  public ExtendedColorModel getColorSelectionModel() {
    return colorSelectionModel;
  }

  public void setColorSelectionModel( final ExtendedColorModel colorSelectionModel ) {
    if ( this.colorSelectionModel != null ) {
      this.colorSelectionModel.removeChangeListener( colorSelectionHandler );
    }
    this.colorSelectionModel = colorSelectionModel;
    if ( this.colorSelectionModel != null ) {
      this.colorSelectionModel.addChangeListener( colorSelectionHandler );
    }
    repaint();
  }

  protected void paintComponent( final Graphics g ) {
    g.drawImage( backend, 0, 0, getWidth(), getHeight(), this );
    final Point color = computeSelectedPosition();
    if ( color != null ) {
      final Color selectedColor = getSelectedColor();
      final float[] floats = Color.RGBtoHSB
        ( selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null );
      floats[ 2 ] = 1 - floats[ 2 ];
      floats[ 1 ] = 0;

      final Graphics graphics = g.create();
      graphics.setColor( Color.getHSBColor( floats[ 0 ], floats[ 1 ], floats[ 2 ] ) );
      graphics.drawOval( color.x - 2, color.y - 2, 4, 4 );
      graphics.dispose();
    }
  }

  private Point computeSelectedPosition() {
    final Color selectedColor = getSelectedColor();
    if ( selectedColor == null ) {
      return null;
    }
    final float[] floats = selectedColor.getRGBColorComponents( null );
    switch( component ) {
      case RED:
        return new Point( (int) ( floats[ 1 ] * getWidth() ), (int) ( ( floats[ 2 ] ) * getHeight() ) );
      case GREEN:
        return new Point( (int) ( floats[ 0 ] * getWidth() ), (int) ( ( floats[ 2 ] ) * getHeight() ) );
      case BLUE:
        return new Point( (int) ( floats[ 0 ] * getWidth() ), (int) ( ( floats[ 1 ] ) * getHeight() ) );
      default:
        return null;
    }
  }

  public Dimension getPreferredSize() {
    return new Dimension( 255, 255 );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 255, 255 );
  }
}
