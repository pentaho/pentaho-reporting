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
 * PreviewDrawablePanel.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.util.ArrayList;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportActionListener;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportMouseEvent;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportMouseListener;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.DrawablePanel;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class PreviewDrawablePanel extends DrawablePanel
{
  private class ReportMouseHandler extends MouseAdapter
  {
    private ReportMouseHandler()
    {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mouseDown(final MouseEvent e)
    {
      fireReportMousePressed(e);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseUp(final MouseEvent e)
    {
      fireReportMouseReleased(e);
    }
  }

  private ArrayList reportMouseListener;
  private transient ReportMouseListener[] cachedReportMouseListeners;
  private ArrayList reportActionListener;
  private transient ReportActionListener[] cachedReportActionListeners;
  
  public PreviewDrawablePanel(final Composite parent, final int style)
  {
    super(parent, style);    
    addMouseListener(new ReportMouseHandler());
//    addMouseListener(new ReportActionHandler());
  }
  
  public void addReportMouseListener (final ReportMouseListener listener)
  {
    if (listener == null)
    {
      throw new NullPointerException();
    }
    if (reportMouseListener == null)
    {
      reportMouseListener = new ArrayList();
    }
    reportMouseListener.add(listener);
    cachedReportMouseListeners = null;
  }

  public void removeReportMouseListener (final ReportMouseListener listener)
  {
    if (listener == null)
    {
      throw new NullPointerException();
    }
    if (reportMouseListener == null)
    {
      return;
    }
    reportMouseListener.remove(listener);
    cachedReportMouseListeners = null;
  }

  protected void fireReportMousePressed(final MouseEvent event)
  {
    if (reportMouseListener == null)
    {
      return;
    }

    final PageDrawable pageDrawable = getPageDrawable();
    if (pageDrawable == null)
    {
      return;
    }
    final RenderNode[] nodes = pageDrawable.getNodesAt(event.x, event.y, null, null);
    if (nodes.length == 0)
    {
      return;
    }

    if (cachedReportMouseListeners == null)
    {
      cachedReportMouseListeners = (ReportMouseListener[])
          reportMouseListener.toArray(new ReportMouseListener[reportMouseListener.size()]);
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for (int n = 0; n < nodes.length; n++)
    {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent(node, event);

      for (int i = 0; i < currentListeners.length; i++)
      {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMouseClicked(reportEvent);
      }
    }
  }

  protected void fireReportMouseReleased(final MouseEvent event)
  {
    if (reportMouseListener == null)
    {
      return;
    }

    final PageDrawable pageDrawable = getPageDrawable();
    if (pageDrawable == null)
    {
      return;
    }
    final RenderNode[] nodes = pageDrawable.getNodesAt(event.x, event.y, null, null);
    if (nodes.length == 0)
    {
      return;
    }

    if (cachedReportMouseListeners == null)
    {
      cachedReportMouseListeners = (ReportMouseListener[])
          reportMouseListener.toArray(new ReportMouseListener[reportMouseListener.size()]);
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for (int n = 0; n < nodes.length; n++)
    {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent(node, event);

      for (int i = 0; i < currentListeners.length; i++)
      {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMouseClicked(reportEvent);
      }
    }
  }

  public PageBackgroundDrawable getBackgroundDrawable()
  {
    final DrawableWrapper wrapper = getDrawable();
    if (wrapper == null)
    {
      return null;
    }
    final Object backend = wrapper.getBackend();
    if (backend instanceof PageBackgroundDrawable == false)
    {
      return null;
    }
    return (PageBackgroundDrawable) backend;
  }

  public PageDrawable getPageDrawable()
  {
    final PageBackgroundDrawable backgroundDrawable = getBackgroundDrawable();
    if (backgroundDrawable == null)
    {
      return null;
    }

    final PageDrawable physicalPageDrawable = backgroundDrawable.getBackend();
    if (physicalPageDrawable == null)
    {
      return null;
    }
    return physicalPageDrawable;
  }
}
