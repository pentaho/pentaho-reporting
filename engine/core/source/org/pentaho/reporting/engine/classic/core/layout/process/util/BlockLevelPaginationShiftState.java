package org.pentaho.reporting.engine.classic.core.layout.process.util;

public class BlockLevelPaginationShiftState implements PaginationShiftState
{
  private PaginationShiftState parent;
  private long shift;
  private long initialShift;
  private boolean breakSuspended;

  public BlockLevelPaginationShiftState(final PaginationShiftState parent)
  {
    if (parent == null)
    {
      throw new NullPointerException();
    }
    this.parent = parent;
    this.initialShift = parent.getShiftForNextChild();
    this.shift = initialShift;
    this.breakSuspended = parent.isManualBreakSuspendedForChilds();
  }

  public void suspendManualBreaks()
  {
    breakSuspended = true;
  }

  public boolean isManualBreakSuspended()
  {
    return parent.isManualBreakSuspendedForChilds();
  }

  public boolean isManualBreakSuspendedForChilds()
  {
    return breakSuspended;
  }

  public void updateShiftFromChild(final long absoluteValue)
  {
    setShift(absoluteValue);
  }

  public void increaseShift(final long value)
  {
    if (value < 0)
    {
      throw new IllegalStateException();
    }
    this.shift += value;
  }

  public long getShiftForNextChild()
  {
    return shift;
  }

  public void setShift(final long value)
  {
    if (value < shift)
    {
      throw new IllegalStateException("Cannot shift backwards");
    }

    this.shift = value;
  }

  public PaginationShiftState pop()
  {
    parent.updateShiftFromChild(this.shift);
    return parent;
  }
}
