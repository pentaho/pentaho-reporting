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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.common;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * DefaultIconTheme.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 13.11.2006, 19:27:51
 *
 * @author Thomas Morgner
 */
public class DefaultIconTheme implements IconTheme
{
  private String bundleName;

  public DefaultIconTheme()
  {
    initialize(ClassicEngineBoot.getInstance().getGlobalConfig());
  }

  public void initialize(final Configuration configuration)
  {
//    this.bundleName = configuration.getConfigProperty("org.pentaho.reporting.engine.classic.extensions.swt.common.IconThemeBundle"); //$NON-NLS-1$
    this.bundleName = "org/pentaho/reporting/engine/classic/extensions/swt/themes/default/theme";
    // hardcoded for now, blu
  }

  public ImageDescriptor getImageDescriptor(final Locale locale, final String id)
  {
    final URL url = getResourceBundleSupport(locale).getResourceURL(id);
    return ImageDescriptor.createFromURL(url);
  }

  private ResourceBundleSupport getResourceBundleSupport(final Locale locale)
  {
    if (bundleName == null)
    {
      throw new IllegalStateException("DefaultIconTheme.ERROR_0001_NO_RESOURCE_BUNDLE"); //$NON-NLS-1$
    }
    return new ResourceBundleSupport(locale, ResourceBundle.getBundle(bundleName, locale),
        ObjectUtilities.getClassLoader(DefaultIconTheme.class));
  }
}
