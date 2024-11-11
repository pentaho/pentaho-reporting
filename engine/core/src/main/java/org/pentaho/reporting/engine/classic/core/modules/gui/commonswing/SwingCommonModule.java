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


package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * Creation-Date: 17.11.2006, 14:40:24
 *
 * @author Thomas Morgner
 */
public class SwingCommonModule extends AbstractModule {
  public static final String BUNDLE_NAME =
      "org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.messages.messages"; //$NON-NLS-1$
  public static final String LARGE_ICON_PROPERTY = "Icon24"; //$NON-NLS-1$

  public SwingCommonModule() throws ModuleInitializeException {
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
    if ( subSystem.getExtendedConfig().getBoolProperty(
        "org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingDialogTranslation" ) ) { //$NON-NLS-1$
      final ResourceBundle resources = ResourceBundle.getBundle( SwingCommonModule.BUNDLE_NAME );
      final UIDefaults defaults = UIManager.getDefaults();
      final Enumeration en = resources.getKeys();
      while ( en.hasMoreElements() ) {
        try {
          final String keyName = (String) en.nextElement();
          defaults.put( keyName, resources.getObject( keyName ) );
        } catch ( Exception e ) {
          // Ignored; if it happens, we would not care that much ..
        }
      }
    }
  }
}
