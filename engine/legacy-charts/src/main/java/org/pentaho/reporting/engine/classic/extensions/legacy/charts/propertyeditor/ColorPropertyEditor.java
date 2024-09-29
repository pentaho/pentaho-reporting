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


package org.pentaho.reporting.engine.classic.extensions.legacy.charts.propertyeditor;

import org.pentaho.plugin.jfreereport.reportcharts.ColorHelper;
import org.pentaho.reporting.libraries.base.util.StringUtils;
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
    } else if ( value instanceof String ) {
      if ( !StringUtils.isEmpty( (String) value ) ) {
        setAsText( value.toString() );
      } else {
        this.value = null;
      }
    } else {
      if ( colorChooser != null ) {
        colorChooser.setColor( null );
      }
      this.value = null;
    }
    propertyChangeSupport.firePropertyChange( null, oldValue, this.value );
  }

  public Object getValue() {
    return getAsText();
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
    return ColorHelper.lookupName( value );
  }

  public void setAsText( final String text ) throws IllegalArgumentException {
    if ( text == null ) {
      setValue( null );
    } else {
      try {
        setValue( Color.decode( text ) );
      } catch ( NumberFormatException ex ) {
        setValue( ColorHelper.lookupColor( text ) );
      }
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
