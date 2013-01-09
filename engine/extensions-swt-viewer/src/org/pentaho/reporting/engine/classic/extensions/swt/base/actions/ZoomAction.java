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
 * ZoomAction.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.pentaho.reporting.engine.classic.extensions.swt.base.PreviewPane;

/**
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public class ZoomAction extends Action
{
  private class PaginatedListener implements PropertyChangeListener
  {
    protected PaginatedListener()
    {
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      setEnabled(previewPane.isPaginated());
    }
  }

  private double zoom;
  private PreviewPane previewPane;

  /**
   * Defines an <code>Action</code> object with a default description string
   * and default icon.
   */
  public ZoomAction(final double zoom, final PreviewPane previewPane)
  {
    this.zoom = zoom;
    this.previewPane = previewPane;

    // this.putValue(Action.NAME, NumberFormat.getPercentInstance
    // (previewPane.getLocale()).format(zoom));
    // this.putValue(Action.SMALL_ICON,
    // ImageUtils.createTransparentIcon(16, 16));
    // this.putValue(SwingCommonModule.LARGE_ICON_PROPERTY,
    // ImageUtils.createTransparentIcon(24, 24));
    this.previewPane.addPropertyChangeListener(PreviewPane.PAGINATED_PROPERTY,
        new PaginatedListener());
    setEnabled(previewPane.getReportJob() != null);
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    previewPane.setZoom(zoom);
  }
}
