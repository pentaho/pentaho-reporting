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


package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;

public class DesignTimeDataFactory extends AbstractDataFactory {
  public DesignTimeDataFactory() {
  }

  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    final TypedTableModel tableModel = new TypedTableModel();
    tableModel.addRow();
    return tableModel;
  }

  public DataFactory derive() {
    return clone();
  }

  public DataFactory clone() {
    return super.clone();
  }

  public void close() {

  }

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return true;
  }

  public String[] getQueryNames() {
    return new String[0];
  }
}
