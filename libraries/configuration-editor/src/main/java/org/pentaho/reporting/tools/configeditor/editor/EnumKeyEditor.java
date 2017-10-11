/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor.editor;

import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.tools.configeditor.model.EnumConfigDescriptionEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

/**
 * The enumeration key editor is used to edit configuration keys, which accept a closed set of values. The possible
 * values are defined in the config-description.
 *
 * @author Thomas Morgner
 */
public class EnumKeyEditor extends AbstractKeyEditor {
  /**
   * Handles the selection event from the combobox and validates the input.
   */
  private class ComboBoxSelectionHandler implements ItemListener {
    /**
     * Default-Constructor.
     */
    private ComboBoxSelectionHandler() {
    }

    /**
     * Invoked when an item has been selected or deselected. The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     *
     * @param e not used
     */
    public void itemStateChanged( final ItemEvent e ) {
      validateInput();
    }
  }

  /**
   * The editor component.
   */
  private final JComboBox content;
  /**
   * The label to name the editor component.
   */
  private final JLabel entryLabel;
  /**
   * A list of selectable options.
   */
  private final List options;
  /**
   * the content pane.
   */
  private final JPanel entryLabelCarrier;

  /**
   * Creates a new enumeration key editor for the given configuration and key definition. The given displayname will be
   * used as label.
   *
   * @param config      the report configuration used to read the values.
   * @param entry       the metadata for the edited key.
   * @param displayName the text for the label.
   */
  public EnumKeyEditor( final HierarchicalConfiguration config,
                        final EnumConfigDescriptionEntry entry, final String displayName ) {
    super( config, entry );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout( 5, 0 ) );
    entryLabel = new JLabel( displayName );
    entryLabel.setToolTipText( entry.getDescription() );

    entryLabelCarrier = new JPanel();
    entryLabelCarrier.setLayout( new BorderLayout() );
    entryLabelCarrier.add( entryLabel );
    contentPane.add( entryLabelCarrier, BorderLayout.WEST );


    this.options = Arrays.asList( entry.getOptions() );

    content = new JComboBox( entry.getOptions() );
    content.addItemListener( new ComboBoxSelectionHandler() );
    contentPane.add( content, BorderLayout.CENTER );
    setContentPane( contentPane );
    reset();
  }

  /**
   * Restores the original value as read from the report configuration.
   */
  public void reset() {
    content.setSelectedItem( loadValue() );
  }

  /**
   * Checks whether the input from the combobox is a valid option.
   */
  protected void validateInput() {
    setValidInput( options.contains( content.getSelectedItem() ) );
  }

  /**
   * Saves the currently selected option as new value in the report configuration.
   */
  public void store() {
    if ( isValidInput() ) {
      if ( isEnabled() ) {
        storeValue( (String) content.getSelectedItem() );
      } else {
        deleteValue();
      }
    }
  }

  /**
   * Sets whether or not this component is enabled. A component which is enabled may respond to user input, while a
   * component which is not enabled cannot respond to user input.  Some components may alter their visual representation
   * when they are disabled in order to provide feedback to the user that they cannot take input.
   *
   * @param enabled defines, whether this editor is enabled.
   * @see java.awt.Component#isEnabled
   */
  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    content.setEnabled( enabled );
  }

  /**
   * Defines the preferred width of the label.
   *
   * @param width the new preferred width.
   */
  public void setLabelWidth( final int width ) {
    final Dimension prefSize = entryLabel.getPreferredSize();
    entryLabelCarrier.setPreferredSize( new Dimension( width, prefSize.height ) );
  }

  /**
   * Returns the preferred width of the label.
   *
   * @return the preferred width.
   */
  public int getLabelWidth() {
    final Dimension prefSize = entryLabel.getPreferredSize();
    if ( prefSize != null ) {
      return prefSize.width;
    }
    return 0;
  }

}
