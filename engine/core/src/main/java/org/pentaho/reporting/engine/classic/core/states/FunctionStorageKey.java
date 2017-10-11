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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class FunctionStorageKey {
  private ReportStateKey parentKey;
  private InstanceID reportId;
  private String reportName;

  protected FunctionStorageKey( final ReportStateKey parentKey, final InstanceID reportId, final String reportName ) {
    this.parentKey = parentKey;
    this.reportId = reportId;
    this.reportName = reportName;
  }

  public String getReportName() {
    return reportName;
  }

  public InstanceID getReportId() {
    return reportId;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final FunctionStorageKey that = (FunctionStorageKey) o;

    if ( reportId != that.reportId ) {
      return false;
    }
    if ( parentKey != null ) {
      if ( !parentKey.equals( that.parentKey ) ) {
        return false;
      }
    } else {
      if ( that.parentKey != null ) {
        return false;
      }
    }

    return true;
  }

  public int hashCode() {
    int result = ( parentKey != null ? parentKey.hashCode() : 0 );
    result = 31 * result + reportId.hashCode();
    return result;
  }

  public String toString() {
    return "FunctionStorageKey{reportId=" + reportId + ", reportName=" + reportName + " ,parentKey=" + parentKey + '}';
  }

  public static FunctionStorageKey createKey( final ReportStateKey parent, final ReportDefinition reportDefinition ) {
    final String name = reportDefinition.getName();
    return new FunctionStorageKey( parent, reportDefinition.getObjectID(), name );
  }
}
