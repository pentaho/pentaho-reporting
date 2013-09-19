package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.AbstractSectionType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

public class TocElementType extends AbstractSectionType
{
  public static final TocElementType INSTANCE = new TocElementType();

  public TocElementType()
  {
    super("toc", true);
  }

  public ReportElement create()
  {
    return new TocElement();
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    return "Table-Of-Contents";
  }
}
