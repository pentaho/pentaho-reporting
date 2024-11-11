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


package org.pentaho.reporting.designer.core;

import org.pentaho.openformula.ui.LibFormulaEditorInfo;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingInfo;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class ReportDesignerInfo extends ProjectInformation {
  /**
   * A singleton instance of the JFreeReportInfo class.
   */
  private static ReportDesignerInfo info;

  /**
   * Returns the singleton instance of the Info-Object.
   *
   * @return te info object for this library.
   */
  public static synchronized ReportDesignerInfo getInstance() {
    if ( info == null ) {
      info = new ReportDesignerInfo();
      info.initialize();
    }
    return info;
  }

  /**
   * Constructs an object containing information about the JFreeReport project.
   */
  private ReportDesignerInfo() {
    super( "report-designer", "Pentaho Report Designer" );// NON-NLS
  }


  private void initialize() {
    setInfo( "http://reporting.pentaho.org/" );// NON-NLS
    setCopyright( "(C)opyright 2000-2011, by Pentaho Corp. and Contributors" );// NON-NLS
    setLicenseName( "LGPL" );// NON-NLS

    addLibrary( ClassicEngineInfo.getInstance() );
    addLibrary( LibFormulaEditorInfo.getInstance() );
    addLibrary( LibSwingInfo.getInstance() );

    setBootClass( ReportDesignerBoot.class.getName() );
  }

  /**
   * Print the library version and information about the library.
   * <p/>
   * After there seems to be confusion about which version is currently used by the user, this method will print the
   * project information to clarify this issue.
   *
   * @param args ignored
   * @noinspection UseOfSystemOutOrSystemErr
   */
  public static void main( final String[] args ) {
    final ReportDesignerInfo info = new ReportDesignerInfo();
    info.initialize();
    System.out.println( info.getName() + ' ' + info.getVersion() );
    System.out.println( "----------------------------------------------------------------" );// NON-NLS
    System.out.println( info.getCopyright() );
    System.out.println( info.getInfo() );
    System.out.println( "----------------------------------------------------------------" );// NON-NLS
    System.out.println( "This project is licenced under the terms of the "// NON-NLS
      + info.getLicenseName() + '.' );
    System.exit( 0 );
  }

}
