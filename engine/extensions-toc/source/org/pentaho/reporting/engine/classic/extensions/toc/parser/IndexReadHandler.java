package org.pentaho.reporting.engine.classic.extensions.toc.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SubReportReadHandler;
import org.pentaho.reporting.engine.classic.extensions.toc.IndexElement;
import org.pentaho.reporting.engine.classic.extensions.toc.IndexElementType;

public class IndexReadHandler extends SubReportReadHandler
{
  public IndexReadHandler()
  {
    super(IndexElementType.INSTANCE, IndexElement.class);
  }
}