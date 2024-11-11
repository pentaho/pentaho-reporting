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


package org.pentaho.reporting.engine.classic.core.modules.gui.print;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the AWT printing export gui module. The module contains 2 export plugins, the page setup
 * plugin and the printing plugin.
 *
 * @author Thomas Morgner
 */
public class AWTPrintingGUIModule extends AbstractModule {
  // /**
  // * The printing export plugin preference key.
  // */
  // public static final String PRINT_ORDER_KEY =
  // "org.pentaho.reporting.engine.classic.core.modules.gui.print.Order";
  // /**
  // * The printing export plugin enable key.
  // */
  // public static final String PRINT_ENABLE_KEY =
  // "org.pentaho.reporting.engine.classic.core.modules.gui.print.Enable";
  // /**
  // * The pagesetup export plugin preference key.
  // */
  // private static final String PAGESETUP_ORDER_KEY =
  // "org.pentaho.reporting.engine.classic.core.modules.gui.print.pagesetup.Order";
  // /**
  // * The pagesetup export plugin enable key.
  // */
  // private static final String PAGESETUP_ENABLE_KEY =
  // "org.pentaho.reporting.engine.classic.core.modules.gui.print.pagesetup.Enable";
  // public static final String PRINT_SERVICE_KEY =
  // "org.pentaho.reporting.engine.classic.core.modules.gui.print.PrintService";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public AWTPrintingGUIModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
  }
}
