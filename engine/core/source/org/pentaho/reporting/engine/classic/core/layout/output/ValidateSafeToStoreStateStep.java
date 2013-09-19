package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;

public class ValidateSafeToStoreStateStep extends IterateSimpleStructureProcessStep
{
  private boolean safeToStore;

  public ValidateSafeToStoreStateStep()
  {
  }

  public boolean isSafeToStore(final LogicalPageBox box)
  {
    safeToStore = true;
    // ModelPrinter.print(box);
    processBoxChilds(box);
    return safeToStore;
  }

  protected boolean startBox(final RenderBox box)
  {
    if (safeToStore == false)
    {
      return false;
    }

    if (box.getStaticBoxLayoutProperties().getPlaceholderBox() == StaticBoxLayoutProperties.PlaceholderType.COMPLEX)
    {
      // inline subreport or other complex content that gets assembled over several states.
      safeToStore = false;
      return false;
    }
    return true;
  }
}
