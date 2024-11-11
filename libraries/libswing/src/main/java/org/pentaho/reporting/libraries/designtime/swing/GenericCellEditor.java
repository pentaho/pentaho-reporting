/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Constructor;

/**
 * This editor can handle all objects that have a single argument String constructor.
 *
 * @author Thomas Morgner
 */
public class GenericCellEditor extends DefaultCellEditor {
  private transient Constructor constructor;
  private transient Object value;
  private Class fallbackType;
  private boolean allowEmptyString;

  public GenericCellEditor( final Class fallbackType ) {
    this( fallbackType, false );
  }

  public GenericCellEditor( final Class fallbackType,
                            final boolean allowEmptyString ) {
    super( new JTextField() );
    this.fallbackType = fallbackType;
    this.allowEmptyString = allowEmptyString;
    getComponent().setName( "Table.editor" );
  }

  public boolean stopCellEditing() {
    final String s = (String) super.getCellEditorValue();
    if ( allowEmptyString == false && "".equals( s ) ) {
      return super.stopCellEditing();
    }
    if ( constructor == null ) {
      return super.stopCellEditing();
    }

    try {
      value = constructor.newInstance( new Object[] { s } );
    } catch ( final Exception e ) {
      // ignore the exception
      final JComponent editorComponent = (JComponent) getComponent();
      editorComponent.setBorder( new LineBorder( Color.red ) );
      return false;
    }
    return super.stopCellEditing();
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    this.value = null;
    final JComponent editorComponent = (JComponent) getComponent();
    editorComponent.setBorder( new LineBorder( Color.black ) );

    Class type = table.getColumnClass( column );
    // Since our obligation is to produce a value which is
    // assignable for the required fallbackType it is OK to use the
    // String constructor for columns which are declared
    // to contain Objects. A String is an Object.
    if ( type == Object.class ) {
      type = this.fallbackType;
    }

    constructor = lookupConstructor( type );
    if ( constructor == null ) {
      constructor = lookupConstructor( this.fallbackType );
    }
    return super.getTableCellEditorComponent( table, value, isSelected, row, column );
  }

  private Constructor lookupConstructor( final Class type ) {

    try {
      return type.getConstructor( new Class[] { String.class } );
    } catch ( final Exception e ) {
      // ignore exception
      return null;
    }
  }

  public Object getCellEditorValue() {
    return value;
  }
}
