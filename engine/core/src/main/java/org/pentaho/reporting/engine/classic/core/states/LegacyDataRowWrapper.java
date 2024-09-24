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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.DataRow;

import java.util.Arrays;

/**
 * This data-row wrapper supports the full interface as it was defined in version 0.8.9. This class makes sure that
 * scripts and introspection code does not break. However, this class should not be used outside of that scope or evil
 * things will happen.
 *
 * @author Thomas Morgner
 */
public final class LegacyDataRowWrapper implements DataRow {
  private DataRow parent;

  public LegacyDataRowWrapper() {
  }

  public DataRow getParent() {
    return parent;
  }

  public void setParent( final DataRow parent ) {
    this.parent = parent;
  }

  public Object get( final int col ) {
    if ( parent == null ) {
      return null;
    }
    final String name = getColumnName( col );
    if ( name == null ) {
      return null;
    }
    return parent.get( name );
  }

  public Object get( final String col ) {
    if ( parent == null ) {
      return null;
    }
    return parent.get( col );
  }

  public String getColumnName( final int col ) {
    if ( parent == null ) {
      return null;
    }
    final String[] columnNames = parent.getColumnNames();
    return columnNames[col];
  }

  public int findColumn( final String name ) {
    if ( parent == null ) {
      return -1;
    }
    return Arrays.asList( parent.getColumnNames() ).indexOf( name );
  }

  public int getColumnCount() {
    if ( parent == null ) {
      return 0;
    }
    return parent.getColumnNames().length;
  }

  public boolean isChanged( final String name ) {
    if ( parent == null ) {
      return false;
    }
    return parent.isChanged( name );
  }

  public boolean isChanged( final int index ) {
    if ( parent == null ) {
      return false;
    }
    final String name = getColumnName( index );
    if ( name == null ) {
      return false;
    }
    return parent.isChanged( name );
  }

  public String[] getColumnNames() {
    return parent.getColumnNames();
  }
}
