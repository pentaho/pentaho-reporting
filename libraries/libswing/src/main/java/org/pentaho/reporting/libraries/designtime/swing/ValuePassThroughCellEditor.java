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
