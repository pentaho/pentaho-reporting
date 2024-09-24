/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
