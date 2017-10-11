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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A replaced content element that is contained in a 'canvas' box (which is the default for all non-inline replaced
 * content elements) must have a minimum width and height of 100% so that it fills the whole box.
 *
 * @author Thomas Morgner
 */
public class ManualBreakIndicatorStyleSheet extends AbstractStyleSheet {
  private static final Float WIDTH = new Float( -100 );
  private static final Float ZERO = new Float( 0 );
  private StyleSheet parent;

  public ManualBreakIndicatorStyleSheet() {
    this.parent = BandDefaultStyleSheet.getBandDefaultStyle();
  }

  public StyleSheet getParent() {
    return parent;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.MIN_WIDTH.equals( key ) ) {
      return ManualBreakIndicatorStyleSheet.WIDTH;
    }
    if ( ElementStyleKeys.MIN_HEIGHT.equals( key ) ) {
      return ManualBreakIndicatorStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.POS_X.equals( key ) ) {
      return ManualBreakIndicatorStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.POS_Y.equals( key ) ) {
      return ManualBreakIndicatorStyleSheet.ZERO;
    }
    if ( BandStyleKeys.PAGEBREAK_BEFORE.equals( key ) ) {
      return Boolean.TRUE;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()] = ManualBreakIndicatorStyleSheet.WIDTH;
    objects[ElementStyleKeys.MIN_HEIGHT.getIdentifier()] = ManualBreakIndicatorStyleSheet.WIDTH;
    objects[ElementStyleKeys.POS_X.getIdentifier()] = ManualBreakIndicatorStyleSheet.ZERO;
    objects[ElementStyleKeys.POS_Y.getIdentifier()] = ManualBreakIndicatorStyleSheet.ZERO;
    objects[BandStyleKeys.PAGEBREAK_BEFORE.getIdentifier()] = Boolean.TRUE;
    return objects;
  }
}
