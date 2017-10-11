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

package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;
import org.pentaho.reporting.libraries.designtime.swing.colorchooser.ColorChooserPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public class ColorPropertyEditor implements PropertyEditor {
  private class ColorSelectionChangeListener implements ChangeListener {
    private ColorSelectionChangeListener() {
    }

    public void stateChanged( final ChangeEvent e ) {
      setValue( colorChooser.getModel().getSelectedColor() );
    }
  }

  private ColorChooserPane colorChooser;
  private PropertyChangeSupport propertyChangeSupport;
  private Color value;

  public ColorPropertyEditor() {
    propertyChangeSupport = new PropertyChangeSupport( this );
  }

  public void setValue( final Object value ) {
    final Object oldValue = this.value;
    if ( value instanceof Color ) {
      if ( colorChooser != null ) {
        colorChooser.setColor( (Color) value );
      }
      this.value = (Color) value;
    } else {
      if ( colorChooser != null ) {
        colorChooser.setColor( null );
      }
      this.value = null;
    }
    propertyChangeSupport.firePropertyChange( null, oldValue, this.value );
  }

  public Object getValue() {
    return value;
  }

  public boolean isPaintable() {
    return true;
  }

  public void paintValue( final Graphics gfx, final Rectangle box ) {
    if ( value == null ) {
      return;
    }

    final Graphics graphics = gfx.create();
    graphics.setColor( value );
    graphics.fillRect( box.x, box.y, box.width, box.height );
    graphics.setColor( Color.BLACK );
    graphics.drawRect( box.x, box.y, box.width, box.height );
    graphics.dispose();
  }

  public String getJavaInitializationString() {
    return null;
  }

  public String getAsText() {
    return ColorUtility.toAttributeValue( value );
  }

  public void setAsText( final String text ) throws IllegalArgumentException {
    try {
      setValue( ColorUtility.toPropertyValue( text ) );
    } catch ( IllegalArgumentException e ) {
      // ignored exception
      setValue( null );
    }
  }

  public String[] getTags() {
    return null;
  }

  public Component getCustomEditor() {
    if ( colorChooser == null ) {
      colorChooser = new ColorChooserPane();
      colorChooser.setColor( value );
      colorChooser.getModel().addChangeListener( new ColorSelectionChangeListener() );
    }
    return colorChooser;
  }

  public boolean supportsCustomEditor() {
    return true;
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }
}
