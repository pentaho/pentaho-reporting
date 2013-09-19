package org.pentaho.reporting.engine.classic.extensions.toc.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SubReportReadHandler;
import org.pentaho.reporting.engine.classic.extensions.toc.TocElement;
import org.pentaho.reporting.engine.classic.extensions.toc.TocElementType;

public class TocReadHandler extends SubReportReadHandler
{
  public TocReadHandler()
  {
    super(TocElementType.INSTANCE, TocElement.class);
  }
}

