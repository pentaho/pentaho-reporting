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
