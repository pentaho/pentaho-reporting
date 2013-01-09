package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics;

import org.pentaho.reporting.libraries.base.config.Configuration;

public class PngReportProcessTask extends Graphics2DReportProcessTask
{
  public PngReportProcessTask()
  {
  }

  protected String computeMimeType(final Configuration configuration)
  {
    return "image/png";
  }
}
