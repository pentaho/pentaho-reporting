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

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.engine.classic.core.DataFactory;

/**
 * Todo: Document Me
 *
 * @author Michael D'Amour
 */
public class ReportQueryNode {
  private String queryName;
  private DataFactory dataFactory;
  private boolean allowEdit;

  public ReportQueryNode( final DataFactory dataFactory,
                          final String queryName,
                          final boolean allowEdit ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    if ( queryName == null ) {
      throw new NullPointerException();
    }
    this.dataFactory = dataFactory;
    this.queryName = queryName;
    this.allowEdit = allowEdit;
  }

  public boolean isAllowEdit() {
    return allowEdit;
  }

  public String toString() {
    return queryName;
  }

  public String getQueryName() {
    return queryName;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ReportQueryNode that = (ReportQueryNode) o;

    if ( allowEdit != that.allowEdit ) {
      return false;
    }
    if ( !dataFactory.equals( that.dataFactory ) ) {
      return false;
    }
    if ( !queryName.equals( that.queryName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = queryName.hashCode();
    result = 31 * result + dataFactory.hashCode();
    result = 31 * result + ( allowEdit ? 1 : 0 );
    return result;
  }
}
