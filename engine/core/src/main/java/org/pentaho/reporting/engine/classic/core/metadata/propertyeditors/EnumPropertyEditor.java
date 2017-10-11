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

package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public abstract class EnumPropertyEditor implements PropertyEditor {
  private boolean allowNull;
  private Enum value;
  private PropertyChangeSupport propertyChangeSupport;
  private Class<? extends Enum> baseClass;

  protected EnumPropertyEditor( final Class<? extends Enum> baseClass, final boolean allowNull ) {
    this.baseClass = baseClass;
    this.allowNull = allowNull;
    this.propertyChangeSupport = new PropertyChangeSupport( this );
  }

  public Object getValue() {
    return value;
  }

  public void setValue( final Object value ) {
    if ( baseClass.isInstance( value ) ) {
      final Object oldVal = this.value;
      this.value = (Enum) value;
      propertyChangeSupport.firePropertyChange( null, oldVal, value );
    } else {
      final Object oldVal = this.value;
      this.value = null;
      propertyChangeSupport.firePropertyChange( null, oldVal, value );
    }
  }

  public String getAsText() {
    if ( value == null ) {
      return null;
    }
    return value.name();
  }

  public void setAsText( final String text ) throws IllegalArgumentException {
    if ( text == null ) {
      setValue( null );
    } else {
      setValue( Enum.valueOf( baseClass, text ) );
    }
  }

  public String[] getTags() {
    final Enum[] enumConstants = baseClass.getEnumConstants();
    final String[] retval;
    final int offset;
    if ( allowNull ) {
      offset = 1;
      retval = new String[enumConstants.length + 1];
    } else {
      offset = 0;
      retval = new String[enumConstants.length];
    }
    for ( int i = 0; i < enumConstants.length; i++ ) {
      final Enum enumConstant = enumConstants[i];
      retval[i + offset] = enumConstant.name();
    }
    return retval;
  }

  /**
   * Determines whether this property editor is paintable.
   *
   * @return True if the class will honor the paintValue method.
   */

  public boolean isPaintable() {
    return false;
  }

  /**
   * Paint a representation of the value into a given area of screen real estate. Note that the propertyEditor is
   * responsible for doing its own clipping so that it fits into the given rectangle.
   * <p/>
   * If the PropertyEditor doesn't honor paint requests (see isPaintable) this method should be a silent noop.
   * <p/>
   * The given Graphics object will have the default font, color, etc of the parent container. The PropertyEditor may
   * change graphics attributes such as font and color and doesn't need to restore the old values.
   *
   * @param gfx
   *          Graphics object to paint into.
   * @param box
   *          Rectangle within graphics object into which we should paint.
   */
  public void paintValue( final Graphics gfx, final Rectangle box ) {

  }

  /**
   * Returns a fragment of Java code that can be used to set a property to match the editors current state. This method
   * is intended for use when generating Java code to reflect changes made through the property editor.
   * <p/>
   * The code fragment should be context free and must be a legal Java expression as specified by the JLS.
   * <p/>
   * Specifically, if the expression represents a computation then all classes and static members should be fully
   * qualified. This rule applies to constructors, static methods and non primitive arguments.
   * <p/>
   * Caution should be used when evaluating the expression as it may throw exceptions. In particular, code generators
   * must ensure that generated code will compile in the presence of an expression that can throw checked exceptions.
   * <p/>
   * Example results are:
   * <ul>
   * <li>Primitive expresssion: <code>2</code>
   * <li>Class constructor: <code>new
   * java.awt.Color(127,127,34)</code>
   * <li>Static field: <code>java.awt.Color.orange</code>
   * <li>Static method: <code>javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5))</code>
   * </ul>
   *
   * @return a fragment of Java code representing an initializer for the current value. It should not contain a
   *         semi-colon ('<code>;</code>') to end the expression.
   */
  public String getJavaInitializationString() {
    return null;
  }

  /**
   * A PropertyEditor may choose to make available a full custom Component that edits its property value. It is the
   * responsibility of the PropertyEditor to hook itself up to its editor Component itself and to report property value
   * changes by firing a PropertyChange event.
   * <p/>
   * The higher-level code that calls getCustomEditor may either embed the Component in some larger property sheet, or
   * it may put it in its own individual dialog, or ...
   *
   * @return A java.awt.Component that will allow a human to directly edit the current property value. May be null if
   *         this is not supported.
   */

  public Component getCustomEditor() {
    return null;
  }

  /**
   * Determines whether this property editor supports a custom editor.
   *
   * @return True if the propertyEditor can provide a custom editor.
   */
  public boolean supportsCustomEditor() {
    return false;
  }

  /**
   * Register a listener for the PropertyChange event. When a PropertyEditor changes its value it should fire a
   * PropertyChange event on all registered PropertyChangeListeners, specifying the null value for the property name and
   * itself as the source.
   *
   * @param listener
   *          An object to be invoked when a PropertyChange event is fired.
   */
  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  /**
   * Remove a listener for the PropertyChange event.
   *
   * @param listener
   *          The PropertyChange listener to be removed.
   */
  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }
}
