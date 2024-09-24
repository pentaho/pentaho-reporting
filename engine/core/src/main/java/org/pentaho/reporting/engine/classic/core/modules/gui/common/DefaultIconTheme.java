/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.gui.common;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 13.11.2006, 19:27:51
 *
 * @author Thomas Morgner
 */
public class DefaultIconTheme implements IconTheme {
  private String bundleName;

  public DefaultIconTheme() {
    initialize( ClassicEngineBoot.getInstance().getGlobalConfig() );
  }

  public void initialize( final Configuration configuration ) {
    this.bundleName =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.common.IconThemeBundle" ); //$NON-NLS-1$
  }

  public Icon getSmallIcon( final Locale locale, final String id ) {
    return getResourceBundleSupport( locale ).getIcon( id, false );
  }

  public Icon getLargeIcon( final Locale locale, final String id ) {
    return getResourceBundleSupport( locale ).getIcon( id, true );
  }

  private ResourceBundleSupport getResourceBundleSupport( final Locale locale ) {
    if ( bundleName == null ) {
      throw new IllegalStateException( "DefaultIconTheme.ERROR_0001_NO_RESOURCE_BUNDLE" ); //$NON-NLS-1$
    }
    return new ResourceBundleSupport( locale, ResourceBundle.getBundle( bundleName, locale ), ObjectUtilities
        .getClassLoader( DefaultIconTheme.class ) );
  }
}
