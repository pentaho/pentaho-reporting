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

package org.pentaho.reporting.designer.core.editor.drilldown;

import java.util.Comparator;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 14:18:08
 *
 * @author Thomas Morgner.
 */
public class DrillDownUiProfileComparator implements Comparator {
  public DrillDownUiProfileComparator() {
  }

  public int compare( final Object o1, final Object o2 ) {
    if ( o1 == null && o2 == null ) {
      return 0;
    }
    if ( o1 == null ) {
      return -1;
    }
    if ( o2 == null ) {
      return +1;
    }

    final DrillDownUiProfile p1 = (DrillDownUiProfile) o1;
    final DrillDownUiProfile p2 = (DrillDownUiProfile) o2;

    if ( p1.getOrderKey() < p2.getOrderKey() ) {
      return -1;
    }
    if ( p1.getOrderKey() > p2.getOrderKey() ) {
      return +1;
    }
    return p1.getDisplayName().compareTo( p2.getDisplayName() );
  }
}
