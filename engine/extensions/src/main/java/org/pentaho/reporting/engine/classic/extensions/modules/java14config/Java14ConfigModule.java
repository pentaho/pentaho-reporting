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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.java14config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the Java1.4 configuration target support module.
 *
 * @author Thomas Morgner
 */
public class Java14ConfigModule extends AbstractModule {
  private static final Log logger = LogFactory.getLog( Java14ConfigModule.class );

  /**
   * The class name of the storage module.
   */
  private static final String JAVA14_CONFIG_STORE_CLASS =
      "org.pentaho.reporting.engine.classic.extensions.modules.java14config.Java14ConfigStorage";
  /**
   * The class name of the initializer class.
   */
  private static final String JAVA14_CONFIG_STORE_INITIALIZER =
      "org.pentaho.reporting.engine.classic.extensions.modules.java14config.Java14ConfigModuleInitializer";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occurred.
   */
  public Java14ConfigModule() throws ModuleInitializeException {
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
   *           if an error occurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    final String value =
        ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.ConfigStore", "<not defined>" );
    if ( value.equals( JAVA14_CONFIG_STORE_CLASS ) == false ) {
      logger.debug( "Java 1.4 Config module not active." );
      return;
    }
    // this will result in an caught exception if JDK 1.4 is not available.
    performExternalInitialize( JAVA14_CONFIG_STORE_INITIALIZER, Java14ConfigModule.class );
  }
}
