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
