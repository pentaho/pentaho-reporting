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

package org.pentaho.openformula.ui;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;


/**
 * Creation-Date: 31.10.2006, 12:30:43
 *
 * @author Thomas Morgner
 */
public class LibFormulaEditorBoot extends AbstractBoot {
  private static LibFormulaEditorBoot instance;

  public static synchronized LibFormulaEditorBoot getInstance() {
    if ( instance == null ) {
      instance = new LibFormulaEditorBoot();
    }
    return instance;
  }

  private LibFormulaEditorBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/formula/libformula-ui.properties",
        "/libformula-ui.properties", true, LibFormulaEditorBoot.class );
  }

  protected void performBoot() {
  }

  protected ProjectInformation getProjectInfo() {
    return LibFormulaEditorInfo.getInstance();
  }
}
