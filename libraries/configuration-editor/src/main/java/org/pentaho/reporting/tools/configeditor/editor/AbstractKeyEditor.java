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

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.tools.configeditor.Messages;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This key editor class is the base class for all key editor components. It provides common services usable for most
 * key editor implementation.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractKeyEditor extends JComponent implements KeyEditor {
  /**
   * A constant for the "validInput" property name.
   */
  public static final String VALID_INPUT_PROPERTY = "validInput"; //$NON-NLS-1$
  /**
   * The report configuration that provides the values for this editor.
   */
  private final HierarchicalConfiguration config;
  /**
   * The config description entry that provides the definition for this key.
   */
  private final ConfigDescriptionEntry entry;
  /**
   * A label that holds the error indicator icons.
   */
  private final JLabel stateLabel;
  /**
   * the resource bundle instance used to translate the text.
   */
  private final ResourceBundleSupport resources;
  /**
   * The error icon used for the key editors.
   */
  private Icon errorIcon;
  /**
   * The empty icon used for the key editors.
   */
  private Icon emptyIcon;
  /**
   * A flag indicating whether the input is valid.
   */
  private boolean validInput;

  /**
   * Creates a new key editor for the given report configuration and key entry.
   *
   * @param config the report configuration that supplies the value for the editor
   * @param entry  the entry description provides the meta data for the edited key.
   */
  protected AbstractKeyEditor( final HierarchicalConfiguration config,
                               final ConfigDescriptionEntry entry ) {
    this.resources = Messages.getInstance();
    this.setLayout( new BorderLayout() );
    this.config = config;
    this.entry = entry;
    stateLabel = new JLabel( getEmptyIcon() );
  }

  /**
   * Returns the empty icon for this an all derived editors.
   *
   * @return the empty icon.
   */
  protected Icon getEmptyIcon() {
    if ( emptyIcon == null ) {
      final Icon errorIcon = getErrorIcon();
      final int width = errorIcon.getIconWidth();
      final int height = errorIcon.getIconHeight();

      final BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
      emptyIcon = new ImageIcon( bi );
    }
    return emptyIcon;
  }

  /**
   * Returns the error icon for this an all derived editors.
   *
   * @return the error icon.
   */
  protected Icon getErrorIcon() {
    if ( errorIcon == null ) {
      errorIcon = resources.getIcon( "default-editor.error-icon" ); //$NON-NLS-1$
    }
    return errorIcon;
  }

  /**
   * Defines the content pane for this editor.
   *
   * @param contentPane the new content pane
   */
  protected void setContentPane( final JPanel contentPane ) {
    removeAll();
    add( contentPane, BorderLayout.CENTER );
    add( stateLabel, BorderLayout.EAST );
  }

  /**
   * Returns the report configuration instance used for this editor.
   *
   * @return the report configuration instance of this editor.
   */
  public Configuration getConfig() {
    return config;
  }

  /**
   * Returns the config description entry of this editor.
   *
   * @return the config description entry.
   */
  public ConfigDescriptionEntry getEntry() {
    return entry;
  }

  /**
   * Loads the value from the configuration.
   *
   * @return the value of the edited key from the configuration.
   */
  protected String loadValue() {
    return config.getConfigProperty( entry.getKeyName() );
  }

  /**
   * Stores the value to the configuration.
   *
   * @param o the new value for the key of the editor.
   */
  protected void storeValue( final String o ) {
    config.setConfigProperty( entry.getKeyName(), o );
  }

  /**
   * Removes the value from the configuration; the configuration will fall back to the default value from the global
   * configuration.
   * <p/>
   * Deleting the value triggers the <code>isDefined</code> property.
   */
  protected void deleteValue() {
    config.setConfigProperty( entry.getKeyName(), null );
  }

  /**
   * Returns true, if the component validated the entered values, false otherwise.
   *
   * @return true, if the input is valid, false otherwise.
   */
  public boolean isValidInput() {
    return validInput;
  }

  /**
   * Defines, whether the input is valid. This should be called after the value of the component changed.
   *
   * @param validInput true, if the input should be considered valid, false otherwise.
   */
  protected void setValidInput( final boolean validInput ) {
    if ( this.validInput != validInput ) {
      final boolean oldValue = this.validInput;
      this.validInput = validInput;
      firePropertyChange( AbstractKeyEditor.VALID_INPUT_PROPERTY, oldValue, validInput );
      if ( this.validInput == false ) {
        stateLabel.setIcon( getErrorIcon() );
      } else {
        stateLabel.setIcon( getEmptyIcon() );
      }
    }
  }

  /**
   * Checks whether the local key has a defined value in the local report configuration.
   *
   * @return true, if the key is defined, false otherwise.
   */
  public boolean isDefined() {
    return config.isLocallyDefined( entry.getKeyName() );
  }

  /**
   * Returns the editor component; this implementation returns the "this" reference.
   *
   * @return a reference to this object.
   */
  public JComponent getComponent() {
    return this;
  }
}
