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

package org.pentaho.reporting.designer.core.inspections;

/**
 * User: Martin Date: 01.02.2006 Time: 21:14:11
 */
public class LocationInfo {
  private Object reportElement;

  public LocationInfo( final Object reportElement ) {
    if ( reportElement == null ) {
      throw new NullPointerException();
    }
    this.reportElement = reportElement;
  }


  public Object getReportElement() {
    return reportElement;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final LocationInfo that = (LocationInfo) o;

    if ( !reportElement.equals( that.reportElement ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return reportElement.hashCode();
  }

  /**
   * @noinspection HardCodedStringLiteral
   */
  public String toString() {
    return "LocationInfo{" +
      "reportElement=" + reportElement +
      '}';
  }
}
