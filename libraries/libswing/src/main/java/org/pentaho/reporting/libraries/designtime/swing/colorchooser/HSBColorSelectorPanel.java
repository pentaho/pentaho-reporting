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

public class HSBColorSelectorPanel extends JComponent {
  public enum ColorComponents {
    HUE, SATURATION, BRIGHTNESS
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

      if ( colorSelectionModel == null ) {
        return;
      }

      final float x = e.getX() / (float) getWidth();
      final float y = e.getY() / (float) getHeight();
      final float[] color = computeColor( getSelectedComponent(), (int) ( x * 360 ), (int) ( 360 - ( y * 360 ) ) );
      colorSelectionModel.setHSB( (int) ( color[ 0 ] * 360 ), (int) ( color[ 1 ] * 100 ), (int) ( color[ 2 ] * 100 ) );
      lastPointX = x;
      lastPointY = y;
      repaint();
    }

    public void mouseDragged( final MouseEvent e ) {
      handleMouse( e );
    }
  }

  private class ColorSelectionHandler implements ChangeListener {
    private ColorSelectionHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      if ( selection != null ) {

        if ( ( selection[ 0 ] == colorSelectionModel.getHue() ) &&
          ( selection[ 1 ] == colorSelectionModel.getSaturation() ) &&
          ( selection[ 2 ] == colorSelectionModel.getValue() ) ) {
          return;
        }
      }

      selection =
        new int[] { colorSelectionModel.getHue(), colorSelectionModel.getSaturation(), colorSelectionModel.getValue() };
      final float[] point = computeSelectedPosition();
      lastPointX = point[ 0 ];
      lastPointY = point[ 1 ];

      updateImage();
      repaint();
    }

  }

  private ColorSelectionHandler colorSelectionHandler;
  private ExtendedColorModel colorSelectionModel;
  private BufferedImage backend;
  private ColorComponents component;
  private int[] selection;
  private float lastPointX;
  private float lastPointY;

  public HSBColorSelectorPanel() {
    backend = new BufferedImage( 360, 360, BufferedImage.TYPE_INT_RGB );
    component = ColorComponents.HUE;
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

  private float[] computeColor( final float selectedValue, final int x, final int y ) {

    if ( component == ColorComponents.HUE ) {
      return new float[] { selectedValue, x / 360f, y / 360f };
    } else if ( component == ColorComponents.SATURATION ) {
      return new float[] { x / 360f, selectedValue, y / 360f };
    } else if ( component == ColorComponents.BRIGHTNESS ) {
      return new float[] { x / 360f, y / 360f, selectedValue };
    }
    throw new IllegalStateException();
  }

  private void updateImage() {
    final float selectedComponent = getSelectedComponent();
    for ( int x = 0; x < 360; x++ ) {
      for ( int y = 0; y < 360; y++ ) {
        final float[] floats = computeColor( selectedComponent, x, y );
        backend.setRGB( x, 359 - y, Color.HSBtoRGB( floats[ 0 ], floats[ 1 ], floats[ 2 ] ) );
      }
    }
  }

  private float getSelectedComponent() {
    final float selectedComponent;
    if ( selection == null ) {
      selectedComponent = 0.5f;
    } else {
      final float[] hsb = new float[] {
        selection[ 0 ] / 360f, selection[ 1 ] / 100f, selection[ 2 ] / 100f };
      switch( component ) {
        case HUE:
          selectedComponent = hsb[ 0 ];
          break;
        case SATURATION:
          selectedComponent = hsb[ 1 ];
          break;
        case BRIGHTNESS:
          selectedComponent = hsb[ 2 ];
          break;
        default:
          selectedComponent = 0.5f;
      }
    }
    return selectedComponent;
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
      this.selection = new int[]
        { colorSelectionModel.getHue(), colorSelectionModel.getSaturation(), colorSelectionModel.getValue() };
      final float[] point = computeSelectedPosition();
      this.lastPointX = point[ 0 ];
      this.lastPointY = point[ 1 ];
      updateImage();
    }
  }

  protected void paintComponent( final Graphics g ) {
    g.drawImage( backend, 0, 0, getWidth(), getHeight(), this );
    if ( selection != null ) {
      final float[] floats = new float[] {
        selection[ 0 ] / 360f, selection[ 1 ] / 100f, selection[ 2 ] / 100f };
      floats[ 1 ] = 0;
      if ( floats[ 2 ] < 0.5f ) {
        floats[ 2 ] = 1;
      } else {
        floats[ 2 ] = 0;
      }

      final Graphics graphics = g.create();
      graphics.setColor( Color.getHSBColor( floats[ 0 ], floats[ 1 ], floats[ 2 ] ) );
      graphics.drawOval( (int) ( ( lastPointX * getWidth() ) - 2 ), (int) ( ( lastPointY * getHeight() ) - 2 ), 4, 4 );
      graphics.dispose();
    }
  }

  private float[] computeSelectedPosition() {
    if ( selection == null ) {
      return null;
    }
    final float[] floats = new float[] {
      selection[ 0 ] / 360f, selection[ 1 ] / 100f, selection[ 2 ] / 100f };
    switch( component ) {
      case HUE:
        return new float[] { floats[ 1 ], ( 1 - floats[ 2 ] ) };
      case SATURATION:
        return new float[] { floats[ 0 ], ( 1 - floats[ 2 ] ) };
      case BRIGHTNESS:
        return new float[] { floats[ 0 ], ( 1 - floats[ 1 ] ) };
      default:
        return null;
    }
  }

  public Dimension getPreferredSize() {
    return new Dimension( 360, 360 );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 360, 360 );
  }
}
