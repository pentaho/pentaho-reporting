package org.pentaho.reporting.engine.classic.core.layout.process.util;

public interface PaginationShiftState
{
  public PaginationShiftState pop();

  long getShiftForNextChild();

  void updateShiftFromChild (long absoluteValue);
  void increaseShift(long increment);
  void setShift (long absoluteValue);

  boolean isManualBreakSuspended();

  /**
   * Defines whether any child will have its break suspended. Note that if you want to query whether it is
   * ok to handle breaks defined on the current context, you have to ask "isManualBreakSuspended()"
   *
   * @return
   */
  boolean isManualBreakSuspendedForChilds();
  void suspendManualBreaks();
}
