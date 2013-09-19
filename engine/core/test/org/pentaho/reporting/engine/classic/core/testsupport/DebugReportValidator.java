package org.pentaho.reporting.engine.classic.core.testsupport;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;

public interface DebugReportValidator
{
  public void processPageContent(final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage);

}
