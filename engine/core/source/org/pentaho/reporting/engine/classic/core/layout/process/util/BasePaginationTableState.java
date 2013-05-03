package org.pentaho.reporting.engine.classic.core.layout.process.util;

public interface BasePaginationTableState
{
  long getPageOffset();

  boolean isOnPageStart(long position);

  boolean isTableProcessing();
}
