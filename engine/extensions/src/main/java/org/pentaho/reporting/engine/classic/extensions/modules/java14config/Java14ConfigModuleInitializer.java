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

import java.util.prefs.Preferences;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;

/**
 * An initializer for the Java 1.4 configuration provider.
 *
 * @author Thomas Morgner
 */
public class Java14ConfigModuleInitializer implements ModuleInitializer {
  /**
   * Default Constructor.
   */
  public Java14ConfigModuleInitializer() {
  }

  /**
   * Initializes the module and defines the storage implementation.
   *
   * @throws ModuleInitializeException
   *           if an error occurred.
   */
  public void performInit() throws ModuleInitializeException {
    final ConfigFactory factory = ConfigFactory.getInstance();
    factory.defineUserStorage( new Java14ConfigStorage( Preferences.userNodeForPackage( MasterReport.class ) ) );
    factory.defineSystemStorage( new Java14ConfigStorage( Preferences.systemNodeForPackage( MasterReport.class ) ) );
  }
}
