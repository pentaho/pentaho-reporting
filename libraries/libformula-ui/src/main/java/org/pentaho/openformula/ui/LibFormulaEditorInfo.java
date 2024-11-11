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


package org.pentaho.openformula.ui;

import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.formula.LibFormulaInfo;

public class LibFormulaEditorInfo extends ProjectInformation {
  private static LibFormulaEditorInfo instance;

  public static synchronized LibFormulaEditorInfo getInstance() {
    if ( instance == null ) {
      instance = new LibFormulaEditorInfo();
    }
    return instance;
  }

  public LibFormulaEditorInfo() {
    super( "libformula-ui", "LibFormula-UI" ); // NON-NLS

    setLicenseName( "LGPL" ); // NON-NLS

    setInfo( "http://reporting.pentaho.org/libformula/" ); // NON-NLS
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" ); // NON-NLS

    setBootClass( "org.pentaho.reporting.libraries.formula.LibFormulaBoot" );

    addLibrary( LibFormulaInfo.getInstance() );
  }
}
