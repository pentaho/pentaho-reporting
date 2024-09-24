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
package org.pentaho.reporting.ui.datasources.kettle.parameter;

public class FormulaParameterFilterStrategy implements FilterStrategy<FormulaParameterTableModel> {
  private FormulaParameterEntity.Type type;

  public FormulaParameterFilterStrategy( final FormulaParameterEntity.Type type ) {
    this.type = type;
  }

  public boolean isAcceptedRow( final int row, final FormulaParameterTableModel parentModel ) {
    return parentModel.getParameterType( row ) == this.type;
  }
}
