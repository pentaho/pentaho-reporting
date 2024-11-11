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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import java.util.LinkedHashSet;

public class LegacyBundleResourceRegistry {
  private static final LegacyBundleResourceRegistry INSTANCE = new LegacyBundleResourceRegistry();
  private LinkedHashSet<String> registeredFiles;

  public static LegacyBundleResourceRegistry getInstance() {
    return INSTANCE;
  }

  private LegacyBundleResourceRegistry() {
    this.registeredFiles = new LinkedHashSet<String>();
  }

  public synchronized void register( final String name ) {
    this.registeredFiles.add( name );
  }

  public synchronized String[] getRegisteredFiles() {
    return this.registeredFiles.toArray( new String[registeredFiles.size()] );
  }
}
