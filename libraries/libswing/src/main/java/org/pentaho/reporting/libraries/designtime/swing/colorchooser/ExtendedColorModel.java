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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;

public class ExtendedColorModel {
  private EventListenerList eventListenerList;

  private int hue;
  private int saturation;
  private int value;
  private int red;
  private int green;
  private int blue;
  private boolean traceEvents;

  public ExtendedColorModel() {
    eventListenerList = new EventListenerList();
  }

  public void setTraceEvents( final boolean traceEvents ) {
    this.traceEvents = traceEvents;
  }

  public int getHue() {
    return hue;
  }

  public int getSaturation() {
    return saturation;
  }

  public int getValue() {
    return value;
  }

  public int getRed() {
    return red;
  }

  public int getGreen() {
    return green;
  }

  public int getBlue() {
    return blue;
  }

  public void fireChangeEvent() {
    final ChangeListener[] listeners = eventListenerList.getListeners( ChangeListener.class );
    if ( listeners.length == 0 ) {
      return;
    }
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final ChangeListener listener = listeners[ i ];
      listener.stateChanged( event );
    }

    if ( traceEvents ) {
      new Exception().printStackTrace();
    }
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    eventListenerList.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    eventListenerList.remove( ChangeListener.class, changeListener );
  }

  public Color getSelectedColor() {
    return new Color( red, green, blue );
  }

  public void setSelectedColor( final Color color ) {
    if ( color == null ) {
      return;
    }

    if ( this.red == color.getRed() && this.green == color.getGreen() && this.blue == color.getBlue() ) {
      return;
    }

    this.red = color.getRed();
    this.green = color.getGreen();
    this.blue = color.getBlue();

    final float[] hsb = Color.RGBtoHSB( red, green, blue, null );
    hue = (int) ( hsb[ 0 ] * 360f );
    saturation = (int) ( hsb[ 1 ] * 100f );
    value = (int) ( hsb[ 2 ] * 100f );

    fireChangeEvent();
  }

  public void setHSB( final int hue, final int saturation, final int value ) {
    if ( this.hue == hue && this.saturation == saturation && this.value == value ) {
      return;
    }

    this.hue = hue;
    this.saturation = saturation;
    this.value = value;

    final Color color = Color.getHSBColor( hue / 360f, saturation / 100f, value / 100f );
    this.red = color.getRed();
    this.green = color.getGreen();
    this.blue = color.getBlue();

    fireChangeEvent();
  }

  public void setRGB( final int red, final int green, final int blue ) {
    if ( this.red == red && this.green == green && this.blue == blue ) {
      return;
    }

    this.red = red;
    this.green = green;
    this.blue = blue;

    final float[] hsb = Color.RGBtoHSB( red, green, blue, null );
    hue = (int) ( hsb[ 0 ] * 360f );
    saturation = (int) ( hsb[ 1 ] * 100f );
    value = (int) ( hsb[ 2 ] * 100f );

    fireChangeEvent();

  }

  public void copyInto( final ExtendedColorModel colorModel ) {
    if ( this.red == colorModel.red && this.green == colorModel.green && this.blue == colorModel.blue &&
      this.hue == colorModel.hue && this.saturation == colorModel.saturation && this.value == colorModel.value ) {
      return;
    }

    this.red = colorModel.red;
    this.green = colorModel.green;
    this.blue = colorModel.blue;
    this.hue = colorModel.hue;
    this.saturation = colorModel.saturation;
    this.value = colorModel.value;
    fireChangeEvent();
  }
}
