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
