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

import org.pentaho.reporting.engine.classic.core.util.StagingMode;

public class StagingModePropertyEditor implements PropertyEditor {
  private StagingMode value;
  private PropertyChangeSupport propertyChangeSupport;

  public StagingModePropertyEditor() {
    propertyChangeSupport = new PropertyChangeSupport( this );
  }

  /**
   * Set (or change) the object that is to be edited. Primitive types such as "int" must be wrapped as the corresponding
   * object type such as "java.lang.Integer".
   *
   * @param value
   *          The new target object to be edited. Note that this object should not be modified by the PropertyEditor,
   *          rather the PropertyEditor should create a new object to hold any modified value.
   */
  public void setValue( final Object value ) {
    final Object oldValue = this.value;
    if ( StagingMode.MEMORY.equals( value ) || StagingMode.TMPFILE.equals( value ) || StagingMode.THRU.equals( value ) ) {
      this.value = (StagingMode) value;
    } else {
      this.value = null;
    }

    propertyChangeSupport.firePropertyChange( null, oldValue, this.value );
  }

  /**
   * Gets the property value.
   *
   * @return The value of the property. Primitive types such as "int" will be wrapped as the corresponding object type
   *         such as "java.lang.Integer".
   */

  public Object getValue() {
    return value;
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
   * Gets the property value as text.
   *
   * @return The property value as a human editable string.
   *         <p>
   *         Returns null if the value can't be expressed as an editable string.
   *         <p>
   *         If a non-null value is returned, then the PropertyEditor should be prepared to parse that string back in
   *         setAsText().
   */
  public String getAsText() {
    if ( value == null ) {
      return null;
    }
    return value.toString();
  }

  /**
   * Set the property value by parsing a given String. May raise java.lang.IllegalArgumentException if either the String
   * is badly formatted or if this kind of property can't be expressed as text.
   *
   * @param text
   *          The string to be parsed.
   */
  public void setAsText( final String text ) throws IllegalArgumentException {
    if ( text == null ) {
      setValue( null );
    } else {
      setValue( StagingMode.valueOf( text ) );
    }
  }

  /**
   * If the property value must be one of a set of known tagged values, then this method should return an array of the
   * tags. This can be used to represent (for example) enum values. If a PropertyEditor supports tags, then it should
   * support the use of setAsText with a tag value as a way of setting the value and the use of getAsText to identify
   * the current value.
   *
   * @return The tag values for this property. May be null if this property cannot be represented as a tagged value.
   */
  public String[] getTags() {
    return new String[] { StagingMode.MEMORY.toString(), StagingMode.TMPFILE.toString(), StagingMode.THRU.toString(), };
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
