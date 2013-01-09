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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.event.ChangeListener;

import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface ElementRenderer
{
  public void setVisualHeight(final double visualHeight);

  public double getVisualHeight();

  public double getComputedHeight();

  public long[] getHorizontalEdgePositionKeys();

  public ElementType getElementType();

  public InstanceID getRepresentationId();

  public boolean isHideInLayout();

  public void addChangeListener(ChangeListener changeListener);

  public void removeChangeListener(ChangeListener changeListener);

  public LinealModel getVerticalLinealModel();

  public double getLayoutHeight();

  public Rectangle2D getBounds();

  public boolean draw(Graphics2D g2);

  public StrictBounds getRootElementBounds();
}
