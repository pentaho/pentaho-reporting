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
