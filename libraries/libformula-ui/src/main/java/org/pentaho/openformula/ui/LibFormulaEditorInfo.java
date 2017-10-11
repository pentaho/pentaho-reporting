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
