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


package org.pentaho.reporting.tools.configeditor;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class ConfigEditorBoot extends AbstractBoot {
  public static final String NAMESPACE =
    "http://jfreereport.sourceforge.net/namespaces/config-description"; //$NON-NLS-1$

  public static final String BUNDLE_NAME = "org.pentaho.reporting.tools.configeditor.messages"; //$NON-NLS-1$

  private static ConfigEditorBoot instance;

  public static synchronized ConfigEditorBoot getInstance() {
    if ( instance == null ) {
      instance = new ConfigEditorBoot();
    }
    return instance;
  }

  private ConfigEditorBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/tools/configeditor/config-editor.properties",
        "/config-editor.properties", true, ConfigEditorBoot.class );
  }

  protected void performBoot() {
  }

  protected ProjectInformation getProjectInfo() {
    return ConfigEditorInfo.getInstance();
  }

}
