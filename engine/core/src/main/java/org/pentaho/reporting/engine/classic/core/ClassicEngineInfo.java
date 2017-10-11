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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.docbundle.LibDocBundleInfo;
import org.pentaho.reporting.libraries.fonts.LibFontInfo;
import org.pentaho.reporting.libraries.formatting.LibFormatInfo;
import org.pentaho.reporting.libraries.formula.LibFormulaInfo;
import org.pentaho.reporting.libraries.repository.LibRepositoryInfo;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderInfo;
import org.pentaho.reporting.libraries.serializer.LibSerializerInfo;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

/**
 * Details about the JFreeReport project.
 *
 * @author David Gilbert
 */
public final class ClassicEngineInfo extends ProjectInformation {
  /**
   * A singleton instance of the JFreeReportInfo class.
   */
  private static ClassicEngineInfo info;

  /**
   * Returns the singleton instance of the Info-Object.
   *
   * @return te info object for this library.
   */
  public static synchronized ClassicEngineInfo getInstance() {
    if ( info == null ) {
      info = new ClassicEngineInfo();
      info.initialize();
    }
    return info;
  }

  /**
   * Constructs an object containing information about the JFreeReport project.
   */
  private ClassicEngineInfo() {
    super( "classic-core", "Pentaho Reporting Engine Classic" ); // NON-NLS
  }

  private void initialize() {
    setInfo( "http://reporting.pentaho.org/" ); // NON-NLS
    setCopyright( "(C)opyright 2000-2011, by Pentaho Corp. and Contributors" ); // NON-NLS
    setLicenseName( "LGPL" ); // NON-NLS

    addLibrary( LibBaseInfo.getInstance() );
    addLibrary( LibSerializerInfo.getInstance() );
    addLibrary( LibLoaderInfo.getInstance() );
    addLibrary( LibFormulaInfo.getInstance() );
    addLibrary( LibFontInfo.getInstance() );
    addLibrary( LibFormatInfo.getInstance() );
    addLibrary( LibDocBundleInfo.getInstance() );
    addLibrary( LibXmlInfo.getInstance() );
    addLibrary( LibRepositoryInfo.getInstance() );
    addOptionalLibrary( "org.pentaho.reporting.engine.classic.extensions.ClassicEngineExtensionsInfo" );

    setBootClass( ClassicEngineBoot.class.getName() );
  }

  /**
   * Print the library version and information about the library.
   * <p/>
   * After there seems to be confusion about which version is currently used by the user, this method will print the
   * project information to clarify this issue.
   *
   * @param args
   *          ignored
   * @noinspection UseOfSystemOutOrSystemErr
   */
  public static void main( final String[] args ) {
    final ClassicEngineInfo info = new ClassicEngineInfo();
    info.initialize();
    System.out.println( info.getName() + ' ' + info.getVersion() ); // NON-NLS
    System.out.println( "----------------------------------------------------------------" );
    System.out.println( info.getCopyright() );
    System.out.println( info.getInfo() );
    System.out.println( "----------------------------------------------------------------" );
    System.out.println( "This project is licenced under the terms of the " // NON-NLS
        + info.getLicenseName() + '.' ); // NON-NLS
    System.exit( 0 );
  }
}
