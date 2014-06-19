/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class CrosstabHeaderStyleSheet extends AbstractStyleSheet
{
  private StyleSheet parent;
  private static final Float ZERO = new Float(0);

  public CrosstabHeaderStyleSheet()
  {
    this.parent = BandDefaultStyleSheet.getBandDefaultStyle();
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
      // this is *auto* mode
      return ZERO;
    }
    if (ElementStyleKeys.VALIGNMENT.equals(key))
    {
      return ElementAlignment.BOTTOM;
    }
    if (ElementStyleKeys.USE_MIN_CHUNKWIDTH.equals(key))
    {
      return Boolean.TRUE;
    }
    if (ElementStyleKeys.OVERFLOW_X.equals(key))
    {
    	return Boolean.TRUE;
    }
    if (ElementStyleKeys.OVERFLOW_Y.equals(key))
    {
    	return Boolean.TRUE;
    }
    return parent.getStyleProperty(key, defaultValue);
  }

  public Object[] toArray()
  {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()] = ZERO;
    objects[ElementStyleKeys.USE_MIN_CHUNKWIDTH.getIdentifier()] = Boolean.TRUE;
    objects[ElementStyleKeys.VALIGNMENT.getIdentifier()] = ElementAlignment.BOTTOM;
    objects[ElementStyleKeys.OVERFLOW_X.getIdentifier()] = Boolean.TRUE;
    objects[ElementStyleKeys.OVERFLOW_Y.getIdentifier()] = Boolean.TRUE;
    return objects;
  }
}
