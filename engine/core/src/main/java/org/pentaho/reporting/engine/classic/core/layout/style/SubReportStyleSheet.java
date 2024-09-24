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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class SubReportStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private Boolean pagebreakBefore;
  private Boolean pagebreakAfter;

  public SubReportStyleSheet( final boolean pagebeakBefore, final boolean pagebreakAfter ) {
    this.parent = ElementDefaultStyleSheet.getDefaultStyle();
    this.pagebreakAfter = pagebreakAfter;
    this.pagebreakBefore = pagebeakBefore;
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
    if ( BandStyleKeys.PAGEBREAK_AFTER.equals( key ) ) {
      return pagebreakAfter;
    }
    if ( BandStyleKeys.PAGEBREAK_BEFORE.equals( key ) ) {
      return pagebreakBefore;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[BandStyleKeys.PAGEBREAK_AFTER.getIdentifier()] = pagebreakAfter;
    objects[BandStyleKeys.PAGEBREAK_BEFORE.getIdentifier()] = pagebreakBefore;
    return objects;
  }
}
