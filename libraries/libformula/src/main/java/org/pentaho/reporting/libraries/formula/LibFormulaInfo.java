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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
