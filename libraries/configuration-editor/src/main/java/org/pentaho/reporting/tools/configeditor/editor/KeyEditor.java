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


package org.pentaho.reporting.tools.configeditor.editor;

import javax.swing.*;

/**
 * The key editor is used to define an editor for a single report configuration key.
 *
 * @author Thomas Morgner
 */
public interface KeyEditor {
  /**
   * Defines, whether the key editor should be enabled.
   *
   * @param b true, if the editor should be enabled, false otherwise.
   */
  public void setEnabled( boolean b );

  /**
   * Returns true, if the editor component is enabled, false otherwise.
   *
   * @return true, if the editor component is enabled, false otherwise.
   */
  public boolean isEnabled();

  /**
   * Sets the width of the label for the editor component. This is a layout hint to help the module editor to build a
   * suitable table layout.
   *
   * @param width the preferred width for the label.
   */
  public void setLabelWidth( int width );

  /**
   * Returns the width of the label component. Return 0 if no label component is used.
   *
   * @return the width of the lable component.
   */
  public int getLabelWidth();

  /**
   * Checks whether the key is defined locally in the report configuration of the editor.
   *
   * @return true, if the local configuration provides the value for the editor, false if the value is read from the
   * default configuration.
   */
  public boolean isDefined();

  /**
   * Returns the editor component used to display the key editor in the GUI.
   *
   * @return the editor component.
   */
  public JComponent getComponent();

  /**
   * Resets the value to the original value from the report configuration.
   */
  public void reset();

  /**
   * Stores the value into the report configuration.
   */
  public void store();
}
