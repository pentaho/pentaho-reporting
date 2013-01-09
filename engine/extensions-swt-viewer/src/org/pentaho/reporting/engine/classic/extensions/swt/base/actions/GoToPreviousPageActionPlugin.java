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

package org.pentaho.reporting.engine.classic.extensions.swt.base.actions;

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
 * GoToPreviousPageActionPlugin.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.KeyStroke;

import org.eclipse.jface.resource.ImageDescriptor;
import org.pentaho.reporting.engine.classic.extensions.swt.base.PreviewPane;
import org.pentaho.reporting.engine.classic.extensions.swt.base.SWTPreviewModule;
import org.pentaho.reporting.engine.classic.extensions.swt.common.IconTheme;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.AbstractActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.ReportEventSource;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.SwtGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public class GoToPreviousPageActionPlugin extends AbstractActionPlugin
implements ControlActionPlugin
{
  private class PageUpdateListener implements PropertyChangeListener
  {
    protected PageUpdateListener()
    {
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      revalidate();
    }
  }

  private ResourceBundleSupport resources;
  private ReportEventSource eventSource;

  public GoToPreviousPageActionPlugin()
  {
  }

  public boolean initialize(final SwtGuiContext context)
  {
    super.initialize(context);
    resources = new ResourceBundleSupport(context.getLocale(),
        SWTPreviewModule.BUNDLE_NAME, ObjectUtilities.getClassLoader(SWTPreviewModule.class));
    eventSource = context.getEventSource();
    eventSource.addPropertyChangeListener(new PageUpdateListener());
    revalidate();
    return true;
  }

  private void revalidate()
  {
    if (eventSource.isPaginated() == false)
    {
      setEnabled(false);
      return;
    }
    if (eventSource.getPageNumber() <= 1)
    {
      setEnabled(false);
      return;
    }
    setEnabled(true);
  }

  protected String getConfigurationPrefix()
  {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.go-to-previous."; //$NON-NLS-1$
  }

  /**
   * Returns the display name for the export action.
   * 
   * @return The display name.
   */
  public String getDisplayName()
  {
    return resources.getString("action.back.name"); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   * 
   * @return The short description.
   */
  public String getShortDescription()
  {
    return resources.getString("action.back.description"); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the export action.
   * 
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey()
  {
    return resources.getKeyStroke("action.back.accelerator"); //$NON-NLS-1$
  }

  /**
   * Returns the mnemonic key code.
   * 
   * @return The code.
   */
  public Integer getMnemonicKey()
  {
    return resources.getMnemonic("action.back.mnemonic"); //$NON-NLS-1$
  }

  public boolean configure(final PreviewPane reportPane)
  {
    reportPane.setPageNumber(Math.max(1, reportPane.getPageNumber() - 1));
    return true;
  }

  /**
   * Returns the ImageDescripter for the this action.
   * 
   * @return The ImageDescripter.
   */
  public ImageDescriptor getImageDescriptor()
  {
    final Locale locale = getContext().getLocale();
    final IconTheme iconTheme = getIconTheme();
    if (iconTheme == null)
    {
      return null;
    } else
    {
      return iconTheme.getImageDescriptor(locale, "action.back.small-icon"); //$NON-NLS-1$
    }
  }
}
