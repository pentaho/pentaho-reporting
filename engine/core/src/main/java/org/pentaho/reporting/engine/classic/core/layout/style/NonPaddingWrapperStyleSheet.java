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
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * An element, that does not have the dynamic flag set to true, is limited to its minimum size and cannot grow any
 * larger than that. This stylesheet simply enforces this policy by redefining the maximum size so that the maximum size
 * is equal to the minimum size.
 *
 * @author Thomas Morgner
 */
public class NonPaddingWrapperStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private static final Float ZERO = new Float( 0 );

  public NonPaddingWrapperStyleSheet() {
  }

  public void setParent( final StyleSheet parent ) {
    this.parent = parent;
  }

  public StyleSheet getParent() {
    return parent;
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.PADDING_TOP.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.PADDING_LEFT.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.PADDING_BOTTOM.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    if ( ElementStyleKeys.PADDING_RIGHT.equals( key ) ) {
      return NonPaddingWrapperStyleSheet.ZERO;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.PADDING_TOP.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    objects[ElementStyleKeys.PADDING_LEFT.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    objects[ElementStyleKeys.PADDING_BOTTOM.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    objects[ElementStyleKeys.PADDING_RIGHT.getIdentifier()] = NonPaddingWrapperStyleSheet.ZERO;
    return objects;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }
}
