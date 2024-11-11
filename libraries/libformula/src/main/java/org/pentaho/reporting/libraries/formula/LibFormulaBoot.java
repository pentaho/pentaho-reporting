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


package org.pentaho.reporting.libraries.formula;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;


/**
 * Creation-Date: 31.10.2006, 12:30:43
 *
 * @author Thomas Morgner
 */
public class LibFormulaBoot extends AbstractBoot {
  public static final int GLOBAL_SCALE = 40;
  private static LibFormulaBoot instance;

  public static synchronized LibFormulaBoot getInstance() {
    if ( instance == null ) {
      instance = new LibFormulaBoot();
    }
    return instance;
  }

  private LibFormulaBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/libraries/formula/libformula.properties",
        "/libformula.properties", true, LibFormulaBoot.class );
  }

  protected void performBoot() {
  }

  protected ProjectInformation getProjectInfo() {
    return LibFormulaInfo.getInstance();
  }
}
