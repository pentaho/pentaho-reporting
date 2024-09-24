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
