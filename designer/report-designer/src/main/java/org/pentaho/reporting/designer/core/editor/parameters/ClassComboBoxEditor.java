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


package org.pentaho.reporting.designer.core.editor.parameters;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Todo: Document me!
 * <p/>
 * Date: 14.05.2009 Time: 16:58:04
 *
 * @author Thomas Morgner.
 */
public class ClassComboBoxEditor implements ComboBoxEditor {
  private static class BorderlessTextField extends JTextField {
    private BorderlessTextField() {
    }

    public void setText( final String s ) {
      if ( getText().equals( s ) ) {
        return;
      }
      super.setText( s );
    }

    public void setBorder( final Border b ) {
      // ignore any request to change the border.
    }
  }

  private EventListenerList listenerList;
  private JTextField textField;
  private Class value;
  private Class[] classes;

  public ClassComboBoxEditor() {
    this( false, null );
  }

  public ClassComboBoxEditor( final boolean withBorder, final Class[] classes ) {
    this.listenerList = new EventListenerList();
    if ( withBorder ) {
      this.textField = new JTextField();
    } else {
      this.textField = new BorderlessTextField();
    }
    this.textField.setDisabledTextColor( textField.getForeground() );
    this.classes = classes;
  }


  /**
   * Return the component that should be added to the tree hierarchy for this editor
   */
  public Component getEditorComponent() {
    return textField;
  }

  /**
   * Set the item that should be edited. Cancel any editing if necessary *
   */
  public void setItem( final Object anObject ) {
    if ( anObject == null ) {
      this.value = null;
      this.textField.setText( null );
    } else {
      final Class fe = (Class) anObject;
      this.value = fe;
      this.textField.setText( ClassListCellRenderer.getSimpleName( fe ) );
    }
  }

  /**
   * Return the edited item *
   */
  public Object getItem() {
    final String inputVal = textField.getText();
    final Class inputClass = findClass( inputVal );

    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( ClassComboBoxEditor.class );
      return Class.forName( inputClass != null ? inputClass.getName() : inputVal, false, loader );
    } catch ( ClassNotFoundException e ) {
      // ignore, return old value
      if ( value != null ) {
        textField.setText( value.getName() );
      } else {
        textField.setText( null );
      }
    }
    return value;
  }

  /**
   * Ask the editor to start editing and to select everything *
   */
  public void selectAll() {
    this.textField.requestFocus();
    this.textField.select( 0, this.textField.getText().length() );
  }

  /**
   * Add an ActionListener. An action event is generated when the edited item changes *
   */
  public void addActionListener( final ActionListener l ) {
    listenerList.add( ActionListener.class, l );
  }

  /**
   * Remove an ActionListener *
   */
  public void removeActionListener( final ActionListener l ) {
    listenerList.remove( ActionListener.class, l );
  }

  private Class findClass( final String className ) {
    if ( classes == null ) {
      return null;
    }
    if ( className == null ) {
      return null;
    }
    for ( final Class aClass : classes ) {
      final String simpleName = ClassListCellRenderer.getSimpleName( aClass );
      if ( className.equals( simpleName ) || className.equals( aClass.getName() ) ) {
        return aClass;
      }
    }
    return null;
  }
}
