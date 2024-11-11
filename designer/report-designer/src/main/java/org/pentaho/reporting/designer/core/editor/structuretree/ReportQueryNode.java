/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
