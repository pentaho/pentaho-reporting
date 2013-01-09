package org.pentaho.reporting.engine.classic.extensions.toc;

/**
 * A data-collector that collects table-of-contents items at group-starts. The function
 * collects these items accross subreport boundaries.
 *
 * @author Thomas Morgner.
 */
public class IndexTextGeneratorFunction extends IndexNumberGeneratorFunction
{
  private String indexSeparator;

  private boolean condensedStyle;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public IndexTextGeneratorFunction()
  {
    this.indexSeparator = ".";
  }

  public String getIndexSeparator()
  {
    return indexSeparator;
  }

  public void setIndexSeparator(final String indexSeparator)
  {
    this.indexSeparator = indexSeparator;
  }

  public boolean isCondensedStyle()
  {
    return condensedStyle;
  }

  public void setCondensedStyle(final boolean condensedStyle)
  {
    this.condensedStyle = condensedStyle;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    final Integer[] groupCount = (Integer[]) super.getValue();
    if (condensedStyle)
    {
      return IndexUtility.getCondensedIndexText(groupCount, indexSeparator);
    }
    return IndexUtility.getIndexText(groupCount, indexSeparator);
  }
}
