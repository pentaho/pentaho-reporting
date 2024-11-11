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


package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A non editing cell-editor for comboboxes. This works around the obvious bug that the combobox always adds an empty
 * string to non-editable models.
 *
 * @author Thomas Morgner.
 */
public class ValuePassThroughCellEditor implements ComboBoxEditor {
  private JComboBox comboBox;
  private ListCellRenderer renderer;
  private Object item;

  public ValuePassThroughCellEditor( final JComboBox comboBox, final ListCellRenderer renderer ) {
    this.comboBox = comboBox;
    this.renderer = renderer;
  }

  /**
   * Return the component that should be added to the tree hierarchy for this editor
   */
  public Component getEditorComponent() {
    final Component listCellRendererComponent = renderer.getListCellRendererComponent
      ( new JList(), comboBox.getSelectedItem(), comboBox.getSelectedIndex(), false, comboBox.hasFocus() );
    if ( listCellRendererComponent instanceof JComponent ) {
      final JComponent jc = (JComponent) listCellRendererComponent;
      jc.setBorder( new LineBorder( Color.BLACK ) );
    }
    return listCellRendererComponent;
  }

  /**
   * Set the item that should be edited. Cancel any editing if necessary *
   */
  public void setItem( final Object item ) {
    this.item = item;
  }

  /**
   * Return the edited item *
   */
  public Object getItem() {
    return item;
  }

  /**
   * Ask the editor to start editing and to select everything *
   */
  public void selectAll() {

  }

  /**
   * Add an ActionListener. An action event is generated when the edited item changes *
   */
  public void addActionListener( final ActionListener l ) {
  }

  /**
   * Remove an ActionListener *
   */
  public void removeActionListener( final ActionListener l ) {
  }
}
