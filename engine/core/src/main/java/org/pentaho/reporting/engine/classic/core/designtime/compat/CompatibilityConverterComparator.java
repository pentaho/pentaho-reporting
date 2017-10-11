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

package org.pentaho.reporting.engine.classic.core.designtime.compat;

import java.util.Comparator;

public class CompatibilityConverterComparator implements Comparator<CompatibilityConverter> {
  public CompatibilityConverterComparator() {
  }

  public int compare( final CompatibilityConverter o1, final CompatibilityConverter o2 ) {
    if ( o1 == o2 ) {
      return 0;
    }
    if ( o1 == null ) {
      return -1;
    }
    if ( o2 == null ) {
      return -1;
    }
    final int v1 = o1.getTargetVersion();
    final int v2 = o2.getTargetVersion();
    if ( v1 == v2 ) {
      return 0;
    }
    if ( v1 < v2 ) {
      return -1;
    }
    return +1;
  }
}
