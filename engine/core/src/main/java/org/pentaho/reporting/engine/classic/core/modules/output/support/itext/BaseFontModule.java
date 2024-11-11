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


package org.pentaho.reporting.engine.classic.core.modules.output.support.itext;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontRegistry;

/**
 * The module definition for the itext font processing module.
 *
 * @author Thomas Morgner
 */
public class BaseFontModule extends AbstractModule {
  private static ITextFontRegistry fontRegistry;

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public BaseFontModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  public static synchronized ITextFontRegistry getFontRegistry() {
    if ( fontRegistry == null ) {
      fontRegistry = new ITextFontRegistry();
      fontRegistry.initialize();
    }
    return fontRegistry;
  }

  /**
   * Initialialize the font factory when this class is loaded and the system property of
   * <code>"org.pentaho.reporting.engine.classic.core.modules.output.pageable.itext.PDFOutputTarget.AutoInit"</code> is
   * set to <code>true</code>.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    if ( AbstractModule.isClassLoadable( "com.lowagie.text.Document", BaseFontModule.class ) == false ) {
      throw new ModuleInitializeException( "Unable to load iText classes. " + "Check your classpath configuration." );
    }

    if ( "onInit".equals( subSystem.getGlobalConfig().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.support.itext.AutoInit" ) ) ) {
      BaseFontModule.getFontRegistry();
    }
  }
}
