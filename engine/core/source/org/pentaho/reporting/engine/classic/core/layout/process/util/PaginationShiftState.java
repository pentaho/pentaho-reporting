package org.pentaho.reporting.engine.classic.core.layout.process.util;

public interface PaginationShiftState
{
  public PaginationShiftState pop();

  long getShiftForNextChild();

  void updateShiftFromChild (long absoluteValue);
  void increaseShift(long increment);
  void setShift (long absoluteValue);

  boolean isManualBreakSuspended();
  void suspendManualBreaks();
}
