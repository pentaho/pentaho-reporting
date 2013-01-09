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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 05.08.2007, 13:23:36
 *
 * @author Thomas Morgner
 */
public class ParagraphPoolboxStyleSheet extends AbstractStyleSheet
{
  private StyleSheet parentStyleSheet;
  private StyleSheet defaultStyleSheet;

  public ParagraphPoolboxStyleSheet(final StyleSheet parentStyleSheet)
  {
    if (parentStyleSheet == null)
    {
      throw new NullPointerException();
    }
    this.parentStyleSheet = parentStyleSheet;
    this.defaultStyleSheet = ElementDefaultStyleSheet.getDefaultStyle();
  }

  public Object getStyleProperty(final StyleKey key, final Object defaultValue)
  {
    if (key.isInheritable())
    {
      return parentStyleSheet.getStyleProperty(key, defaultValue);
    }
    return defaultStyleSheet.getStyleProperty(key, defaultValue);
  }

  public StyleSheet getParent()
  {
    return parentStyleSheet;
  }

  public InstanceID getId()
  {
    return parentStyleSheet.getId();
  }

  public long getChangeTracker()
  {
    return parentStyleSheet.getChangeTracker();
  }

  public Object[] toArray()
  {
    final Object[] objects = defaultStyleSheet.toArray();
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    for (int i = 0; i < keys.length; i++)
    {
      final StyleKey key = keys[i];
      if (key.isInheritable())
      {
        objects[i] = parentStyleSheet.getStyleProperty(key, null);
      }
    }
    return objects;
  }
}
