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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

@Deprecated
public class CanvasMinWidthStyleSheet extends AbstractStyleSheet
{
  private StyleSheet parent;
  private Float minWidth;

  public CanvasMinWidthStyleSheet(final StyleSheet parent)
  {
    this.parent = parent;
    this.minWidth = (Float) parent.getStyleProperty(ElementStyleKeys.MIN_WIDTH);
    if (minWidth.floatValue() == 0)
    {
      minWidth = new Float(-100);
    }
  }

  public StyleSheet getParent()
  {
    return parent;
  }

  public InstanceID getId()
  {
    return parent.getId();
  }

  public long getChangeTracker()
  {
    return parent.getChangeTracker();
  }


  public Object getStyleProperty(final StyleKey key, final Object defaultValue)
  {
    if (ElementStyleKeys.MIN_WIDTH.equals(key))
    {
      return minWidth;
    }
    return parent.getStyleProperty(key, defaultValue);
  }

  public Object[] toArray()
  {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()] = minWidth;
    return objects;
  }
}
