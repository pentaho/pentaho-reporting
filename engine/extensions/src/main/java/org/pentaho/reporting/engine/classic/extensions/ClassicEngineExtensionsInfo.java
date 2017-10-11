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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions;

import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Creation-Date: 21.05.2007, 15:26:09
 *
 * @author Thomas Morgner
 */
public class ClassicEngineExtensionsInfo extends ProjectInformation {
  private static ClassicEngineExtensionsInfo info;

  /**
   * Constructs an empty project info object.
   */
  private ClassicEngineExtensionsInfo() {
    super( "classic-extensions", "Pentaho Reporting Classic Extensions" );
  }

  private void initialize() {
    setInfo( "http://reporting.pentaho.org/" );
    setCopyright( "(C)opyright 2000-2011, by Pentaho Corp. and Contributors" );
    setLicenseName( "LGPL" );
    addLibrary( ClassicEngineInfo.getInstance() );
  }

  public static synchronized ProjectInformation getInstance() {
    if ( info == null ) {
      info = new ClassicEngineExtensionsInfo();
      info.initialize();
    }
    return info;
  }
}
