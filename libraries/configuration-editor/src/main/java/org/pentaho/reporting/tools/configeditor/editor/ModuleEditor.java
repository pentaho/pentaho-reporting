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

package org.pentaho.reporting.tools.configeditor.editor;

import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

import javax.swing.*;

/**
 * The module editor is used to provide a customizable editor component for a JfreeReport module.
 * <p/>
 * At the moment, there is only one common module editor known, which provides an on-the-fly editor for all defined
 * properties of the module. Specialized editors may be added in the future.
 *
 * @author Thomas Morgner
 */
public interface ModuleEditor {
  /**
   * Creates a new instance of the module editor. This instance will be used to edit the specific module.
   * <p/>
   * Editors are free to ignore the list of keys given as builder hints.
   *
   * @param module   the module that should be edited.
   * @param config   the report configuration used to fill the values of the editors.
   * @param keyNames the list of keynames this module editor should handle.
   * @return the created new editor instance.
   */
  public ModuleEditor createInstance
  ( Module module, HierarchicalConfiguration config, ConfigDescriptionEntry[] keyNames );

  /**
   * Checks whether this module editor can handle the given module.
   *
   * @param module the module to be edited.
   * @return true, if this editor may be used to edit the module, false otherwise.
   */
  public boolean canHandle( Module module );

  /**
   * Returns the editor component of the module. Calling this method is only valid on instances created with
   * createInstance.
   *
   * @return the editor component for the GUI.
   */
  public JComponent getComponent();

  /**
   * Resets all keys to the values from the report configuration.
   */
  public void reset();

  /**
   * Stores all values for the editor's keys into the report configuration.
   */
  public void store();
}
