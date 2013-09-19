package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

public class TableExportRenderBoxState
{
  private long backgroundDefinitionAge;
  private SheetLayoutTableCellDefinition backgroundDefinition;
  private TableRectangle cellBackgroundHint;
  private long cellBackgroundHintAge;
  
  public TableExportRenderBoxState()
  {
  }

  public long getBackgroundDefinitionAge()
  {
    return backgroundDefinitionAge;
  }

  public long getCellBackgroundLayoutShift()
  {
    return cellBackgroundHintAge;
  }

  public SheetLayoutTableCellDefinition getBackgroundDefinition()
  {
    return backgroundDefinition;
  }

  public void setBackgroundDefinition(final SheetLayoutTableCellDefinition backgroundDefinition, final long age)
  {
    this.backgroundDefinition = backgroundDefinition;
    this.backgroundDefinitionAge = age;
  }

  public TableRectangle getCellBackgroundHint()
  {
    return cellBackgroundHint;
  }

  public void setCellBackgroundHint(final TableRectangle cellBackgroundHint, final long age)
  {
    this.cellBackgroundHint = cellBackgroundHint;
    this.cellBackgroundHintAge = age;
  }
}
