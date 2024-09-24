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
