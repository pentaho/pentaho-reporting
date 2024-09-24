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

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.BooleanPropertyEditor;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.ColorPropertyEditor;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.FontPropertyEditor;

import java.awt.*;
import java.beans.PropertyEditorManager;

public class LibSwingBoot extends AbstractBoot {
  private static LibSwingBoot instance;

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibSwingBoot getInstance() {
    if ( LibSwingBoot.instance == null ) {
      LibSwingBoot.instance = new LibSwingBoot();
    }
    return LibSwingBoot.instance;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibSwingBoot() {
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/designtime/swing/libswing.properties",
        "/libswing.properties", true, LibSwingBoot.class );
  }

  /**
   * Performs the boot.
   */
  protected void performBoot() {
    // nothing required. Just gather the configuration.
    PropertyEditorManager.registerEditor( Boolean.class, BooleanPropertyEditor.class );
    PropertyEditorManager.registerEditor( Font.class, FontPropertyEditor.class );
    PropertyEditorManager.registerEditor( Color.class, ColorPropertyEditor.class );
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return LibSwingInfo.getInstance();
  }
}
