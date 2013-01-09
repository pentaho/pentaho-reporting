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

package org.pentaho.reporting.engine.classic.extensions.swt.base.internal;

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
 * PreviewPaneUtilities.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */


import org.pentaho.reporting.engine.classic.extensions.swt.Util;
import org.pentaho.reporting.engine.classic.extensions.swt.base.PreviewPane;
import org.pentaho.reporting.engine.classic.extensions.swt.common.DefaultIconTheme;
import org.pentaho.reporting.engine.classic.extensions.swt.common.IconTheme;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 17.11.2006, 15:06:51
 *
 * @author Thomas Morgner
 */
public class PreviewPaneUtilities
{
  private static final String ICON_THEME_CONFIG_KEY = "org.pentaho.reporting.engine.classic.extensions.swt.common.IconTheme"; //$NON-NLS-1$
//private static final String ICON_THEME_CONFIG_KEY = "org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme"; //$NON-NLS-1$
  private static final String ACTION_FACTORY_CONFIG_KEY = "org.pentaho.reporting.engine.classic.core.modules.gui.base.ActionFactory"; //$NON-NLS-1$
  private static final String CATEGORY_PREFIX = "org.pentaho.reporting.engine.classic.core.modules.gui.swing.category."; //$NON-NLS-1$

  private PreviewPaneUtilities()
  {
  }

  public static IconTheme createIconTheme(final Configuration config)
  {
    Util.printConfiguraiton(config);

    final String themeClass = config.getConfigProperty(ICON_THEME_CONFIG_KEY);
    final Object maybeTheme = ObjectUtilities.loadAndInstantiate(themeClass, PreviewPane.class, IconTheme.class);
    final IconTheme iconTheme;
    if (maybeTheme != null)
    {
      iconTheme = (IconTheme) maybeTheme;
    }
    else
    {
      iconTheme = new DefaultIconTheme();
    }
    iconTheme.initialize(config);
    return iconTheme;
  }

  public static double getNextZoomOut(final double zoom,
      final double[] zoomFactors)
  {
    if (zoom <= zoomFactors[0])
    {
      return (zoom * 2.0) / 3.0;
    }

    final double largestZoom = zoomFactors[zoomFactors.length - 1];
    if (zoom > largestZoom)
    {
      final double linear = (zoom * 2.0) / 3.0;
      if (linear < largestZoom)
      {
        return largestZoom;
      }
      return linear;
    }

    for (int i = zoomFactors.length - 1; i >= 0; i--)
    {
      final double factor = zoomFactors[i];
      if (factor < zoom)
      {
        return factor;
      }
    }

    return (zoom * 2.0) / 3.0;
  }

  public static double getNextZoomIn(final double zoom,
      final double[] zoomFactors)
  {
    final double largestZoom = zoomFactors[zoomFactors.length - 1];
    if (zoom >= largestZoom)
    {
      return (zoom * 1.5);
    }

    final double smallestZoom = zoomFactors[0];
    if (zoom < smallestZoom)
    {
      final double linear = (zoom * 1.5);
      if (linear > smallestZoom)
      {
        return smallestZoom;
      }
      return linear;
    }

    for (int i = 0; i < zoomFactors.length; i++)
    {
      final double factor = zoomFactors[i];
      if (factor > zoom)
      {
        return factor;
      }
    }
    return (zoom * 1.5);
  }
}
