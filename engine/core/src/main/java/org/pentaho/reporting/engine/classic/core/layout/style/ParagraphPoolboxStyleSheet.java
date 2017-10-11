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

import java.util.List;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class ParagraphPoolboxStyleSheet extends AbstractStyleSheet {
  private StyleSheet parentStyleSheet;
  private StyleSheet defaultStyleSheet;

  public ParagraphPoolboxStyleSheet( final StyleSheet parentStyleSheet ) {
    if ( parentStyleSheet == null ) {
      throw new NullPointerException();
    }
    this.parentStyleSheet = parentStyleSheet;
    this.defaultStyleSheet = ElementDefaultStyleSheet.getDefaultStyle();
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE.equals( key ) ) {
      return Boolean.TRUE;
    }

    if ( key.isInheritable() ) {
      return parentStyleSheet.getStyleProperty( key, defaultValue );
    }
    return defaultStyleSheet.getStyleProperty( key, defaultValue );
  }

  public StyleSheet getParent() {
    return parentStyleSheet;
  }

  public InstanceID getId() {
    return parentStyleSheet.getId();
  }

  public long getChangeTracker() {
    return parentStyleSheet.getChangeTracker();
  }

  public Object[] toArray() {
    final Object[] objects = defaultStyleSheet.toArray();
    final List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();
    for ( int i = 0, len = keys.size(); i < len; i++ ) {
      final StyleKey key = keys.get( i );
      if ( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE.equals( key ) ) {
        objects[ i ] = Boolean.TRUE;
      } else if ( key.isInheritable() ) {
        objects[ i ] = parentStyleSheet.getStyleProperty( key, null );
      }
    }
    return objects;
  }
}
