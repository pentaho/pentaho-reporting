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

package org.pentaho.reporting.engine.classic.extensions.swt.commonSWT;

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
 * DrawablePanel.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.reporting.engine.classic.extensions.swt.demo.util.SWTGraphics2D;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Creation-Date: 8/17/2008
 * 
 * @author Baochuan Lu
 */
public class DrawablePanel extends Composite implements PaintListener
{
  private DrawableWrapper drawable;
  private Image imageBuffer;

  private boolean refreshBuffer;

  public DrawablePanel(final Composite parent, final int style)
  {
    super(parent, style);
    // setLayout(new FillLayout());
    this.addPaintListener(this);
  }

  public DrawableWrapper getDrawable()
  {
    return drawable;
  }

  public void setDrawableAsRawObject(final Object o)
  {
    if (o == null)
    {
      setDrawable(null);
    } else if (o instanceof DrawableWrapper)
    {
      setDrawable((DrawableWrapper) o);
    } else
    {
      setDrawable(new DrawableWrapper(o));
    }
  }

  public void setDrawable(final DrawableWrapper drawable)
  {
    this.drawable = drawable;
    if (getDisplay().getThread() != Thread.currentThread())
    {
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          DrawablePanel.this.redraw();
        }
      });
    } else
    {
      redraw();
    }
  }

  public Point computeSize(final int wHint, final int hHint, final boolean changed)
  {
    if (drawable == null)
    {
      return new Point(0, 0);
    } else
    {
      final Dimension size = drawable.getPreferredSize();
      return new Point(size.width, size.height);
    }
  }

  public void paintControl(final PaintEvent e)
  {
    if (drawable == null)
    {
      return;
    }

    final Rectangle available = this.getClientArea();

    final Graphics2D g2 = (Graphics2D) new SWTGraphics2D(e.gc);

    drawable.draw(g2, new Rectangle2D.Double(0, 0, available.width,
        available.height));
    g2.dispose();
  }

  public void dispose()
  {
    if (this.imageBuffer != null)
    {
      this.imageBuffer.dispose();
    }
    super.dispose();
  }
}
