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
import javax.swing.event.ListDataEvent;
import java.awt.event.ActionEvent;

/**
 * A combobox that does not check whether a selected item is part of the model when the box is not editable.
 *
 * @author Thomas Morgner.
 */
public class SmartComboBox<T> extends JComboBox {
  private boolean selectingItem;

  /**
   * Creates a <code>JComboBox</code> that takes it's items from an existing <code>ComboBoxModel</code>.  Since the
   * <code>ComboBoxModel</code> is provided, a combo box created using this constructor does not create a default combo
   * box model and may impact how the insert, remove and add methods behave.
   *
   * @param aModel the <code>ComboBoxModel</code> that provides the displayed list of items
   * @see javax.swing.DefaultComboBoxModel
   */
  public SmartComboBox( final ComboBoxModel aModel ) {
    super( aModel );
  }

  /**
   * Creates a <code>JComboBox</code> that contains the elements in the specified array.  By default the first item in
   * the array (and therefore the data model) becomes selected.
   *
   * @param items an array of objects to insert into the combo box
   * @see javax.swing.DefaultComboBoxModel
   */
  public SmartComboBox( final T[] items ) {
    super( items );
  }


  /**
   * Creates a <code>JComboBox</code> with a default data model. The default data model is an empty list of objects. Use
   * <code>addItem</code> to add items.  By default the first item in the data model becomes selected.
   *
   * @see javax.swing.DefaultComboBoxModel
   */
  public SmartComboBox() {
  }

  /**
   * Sets the selected item in the combo box display area to the object in the argument. If <code>anObject</code> is in
   * the list, the display area shows <code>anObject</code> selected.
   * <p/>
   * If <code>anObject</code> is <i>not</i> in the list and the combo box is uneditable, it will not change the current
   * selection. For editable combo boxes, the selection will change to <code>anObject</code>.
   * <p/>
   * If this constitutes a change in the selected item, <code>ItemListener</code>s added to the combo box will be
   * notified with one or two <code>ItemEvent</code>s. If there is a current selected item, an <code>ItemEvent</code>
   * will be fired and the state change will be <code>ItemEvent.DESELECTED</code>. If <code>anObject</code> is in the
   * list and is not currently selected then an <code>ItemEvent</code> will be fired and the state change will be
   * <code>ItemEvent.SELECTED</code>.
   * <p/>
   * <code>ActionListener</code>s added to the combo box will be notified with an <code>ActionEvent</code> when this
   * method is called.
   *
   * @param anObject the list object to select; use <code>null</code> to clear the selection
   */
  public void setSelectedItem( final Object anObject ) {
    final Object oldSelection = selectedItemReminder;
    if ( oldSelection == null || !oldSelection.equals( anObject ) ) {
      // Must toggle the state of this flag since this method
      // call may result in ListDataEvents being fired.
      selectingItem = true;
      dataModel.setSelectedItem( anObject );
      selectingItem = false;

      if ( selectedItemReminder != dataModel.getSelectedItem() ) {
        // in case a users implementation of ComboBoxModel
        // doesn't fire a ListDataEvent when the selection
        // changes.
        selectedItemChanged();
      }
    }
    fireActionEvent();
  }

  /**
   * This method is public as an implementation side effect. do not call or override.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( isEditable() ) {
      final Object newItem = getEditor().getItem();
      setPopupVisible( false );
      getModel().setSelectedItem( newItem );
    } else {
      setPopupVisible( false );
    }
    final String oldCommand = getActionCommand();
    setActionCommand( "comboBoxEdited" );
    fireActionEvent();
    setActionCommand( oldCommand );
  }

  /**
   * This method is public as an implementation side effect. do not call or override.
   */
  public void contentsChanged( final ListDataEvent e ) {
    final Object oldSelection = selectedItemReminder;
    final Object newSelection = dataModel.getSelectedItem();
    if ( oldSelection == null || !oldSelection.equals( newSelection ) ) {
      selectedItemChanged();
      if ( !selectingItem ) {
        fireActionEvent();
      }
    }
  }

}
