package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.AbstractSectionType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

public class IndexElementType extends AbstractSectionType
{
  public static final IndexElementType INSTANCE = new IndexElementType();

  public IndexElementType()
  {
    super("index", true);
  }

  public ReportElement create()
  {
    return new IndexElement();
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    return "Index";
  }
}