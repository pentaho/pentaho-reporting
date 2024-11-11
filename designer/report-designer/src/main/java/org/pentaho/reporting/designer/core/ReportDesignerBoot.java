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

import org.pentaho.reporting.designer.core.settings.ProxySettings;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.boot.PackageManager;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class ReportDesignerBoot extends AbstractBoot {
  public static final String DESIGNER_NAMESPACE =
    "http://reporting.pentaho.org/namespaces/report-designer/2.0";
  public static final String LAST_FILENAME = "report-save-path";

  public static final String ZOOM = "zoom";
  public static final String VISUAL_HEIGHT = "visual-height";
  public static final String DESIGNER_LINEAL_MODEL_OBJECT = "lineal-model-object";
  public static final String DESIGNER_POSITIONS_MODEL_OBJECT = "positions-model-object";
  public static final String SELECTION_OVERLAY_INFORMATION = "selection-overlay-information";
  /**
   * The singleton instance of the Boot class.
   */
  private static ReportDesignerBoot instance;
  /**
   * The project info contains all meta data about the project.
   */
  private ProjectInformation projectInfo;

  /**
   * Creates a new instance.
   */
  private ReportDesignerBoot() {
    projectInfo = ReportDesignerInfo.getInstance();
  }

  /**
   * Returns the singleton instance of the boot utility class.
   *
   * @return the boot instance.
   */
  public static synchronized ReportDesignerBoot getInstance() {
    if ( instance == null ) {
      instance = new ReportDesignerBoot();
    }
    return instance;
  }

  /**
   * Returns the current global configuration as modifiable instance. This is exactly the same as casting the global
   * configuration into a ModifableConfiguration instance.
   * <p/>
   * This is a convinience function, as all programmers are lazy.
   *
   * @return the global config as modifiable configuration.
   */
  public ModifiableConfiguration getEditableConfig() {
    return (ModifiableConfiguration) getGlobalConfig();
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo() {
    return projectInfo;
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration
      ( "/org/pentaho/reporting/designer/core/report-designer.properties",// NON-NLS
        "/report-designer.properties", true, ReportDesignerBoot.class );// NON-NLS
  }

  protected void performBoot() {
    final PackageManager packageManager = getPackageManager();
    packageManager.load( "org.pentaho.reporting.designer.core." );// NON-NLS
    packageManager.load( "org.pentaho.reporting.designer.modules." );// NON-NLS
    packageManager.initializeModules();

    try {
      // Fixes browser-launcher on OpenJDK 1.7 for MacOS
      if ( MacOSXIntegration.MAC_OS_X ) {
        if ( System.getProperty( "mrj.version" ) == null ) {
          System.setProperty( "mrj.version", "999999" );
        }
      }
    } catch ( Error e ) {
      // ignored.
    }

    ProxySettings.getInstance().installAuthenticator();
    ProxySettings.getInstance().applySettings();
  }
}
