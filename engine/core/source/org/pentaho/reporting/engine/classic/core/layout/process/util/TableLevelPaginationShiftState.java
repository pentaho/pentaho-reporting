package org.pentaho.reporting.engine.classic.core.layout.process.util;

public class TableLevelPaginationShiftState implements PaginationShiftState
{
  public TableLevelPaginationShiftState()
  {
  }

  public long getShiftForNextChild()
  {
    return 0;
  }

  public PaginationShiftState pop()
  {
    return null;
  }

  public void updateShiftFromChild(final long absoluteValue)
  {

  }

  public void increaseShift(final long increment)
  {

  }

  public void setShift(final long absoluteValue)
  {

  }

  public boolean isManualBreakSuspended()
  {
    return false;
  }

  public void suspendManualBreaks()
  {

  }
}
