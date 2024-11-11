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

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Creation-Date: 31.10.2006, 12:31:15
 *
 * @author Thomas Morgner
 */
public class LibFormulaInfo extends ProjectInformation {
  private static LibFormulaInfo instance;

  public static synchronized LibFormulaInfo getInstance() {
    if ( instance == null ) {
      instance = new LibFormulaInfo();
    }
    return instance;
  }

  public LibFormulaInfo() {
    super( "libformula", "LibFormula" );

    setLicenseName( "LGPL" );

    setInfo( "http://reporting.pentaho.org/libformula/" );
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" );

    setBootClass( "org.pentaho.reporting.libraries.formula.LibFormulaBoot" );

    addLibrary( LibBaseInfo.getInstance() );
  }
}
