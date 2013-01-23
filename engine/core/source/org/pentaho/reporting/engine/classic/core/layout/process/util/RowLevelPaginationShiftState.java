package org.pentaho.reporting.engine.classic.core.layout.process.util;

public class RowLevelPaginationShiftState implements PaginationShiftState
{
  private PaginationShiftState parent;
  private long shift;
  private long shiftForChilds;

  public RowLevelPaginationShiftState(final PaginationShiftState parent)
  {
    this.parent = parent;
    this.shiftForChilds = parent.getShiftForNextChild();
    this.shift = this.shiftForChilds;
  }

  public void suspendManualBreaks()
  {
  }

  public boolean isManualBreakSuspended()
  {
    return true;
  }

  public void updateShiftFromChild(final long absoluteValue)
  {
    this.shift = Math.max (shift, absoluteValue);
  }

  public long getShiftForNextChild()
  {
    return shiftForChilds;
  }

  public PaginationShiftState pop()
  {
    parent.updateShiftFromChild(shift);
    return parent;
  }

  public void increaseShift(final long value)
  {
    this.shiftForChilds = Math.max (shiftForChilds, this.shiftForChilds + value);
    this.shift = Math.max (shift, shiftForChilds);
  }

  public void setShift(final long value)
  {
    this.shiftForChilds = Math.max (shiftForChilds, value);
    this.shift = Math.max (shift, shiftForChilds);
  }
}
