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

package org.pentaho.reporting.designer.core.util.table;

import java.io.Serializable;

public class GroupingHeader implements Serializable {
  private String headerText;
  private boolean collapsed;

  public GroupingHeader( final String headerText ) {
    this.headerText = headerText;
  }

  public String getHeaderText() {
    return headerText;
  }

  public String toString() {
    return headerText;
  }

  public boolean isCollapsed() {
    return collapsed;
  }

  public void setCollapsed( final boolean collapsed ) {
    this.collapsed = collapsed;
  }
}
