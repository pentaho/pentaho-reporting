package org.pentaho.reporting.engine.classic.core.modules.output.pageable.base;

import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;

public class SinglePageFlowSelector implements PageFlowSelector
{
  private int acceptedPage;
  private boolean logicalPage;

  public SinglePageFlowSelector(final int acceptedPage, final boolean logicalPage)
  {
    this.acceptedPage = acceptedPage;
    this.logicalPage = logicalPage;
  }

  public SinglePageFlowSelector(final int acceptedPage)
  {
    this(acceptedPage, true);
  }

  public boolean isPhysicalPageAccepted(final PhysicalPageKey key)
  {
    if (key == null)
    {
      return false;
    }
    return logicalPage && key.getSequentialPageNumber() == acceptedPage;
  }

  public boolean isLogicalPageAccepted(final LogicalPageKey key)
  {
    if (key == null)
    {
      return false;
    }
    return logicalPage && key.getPosition() == acceptedPage;
  }
}
