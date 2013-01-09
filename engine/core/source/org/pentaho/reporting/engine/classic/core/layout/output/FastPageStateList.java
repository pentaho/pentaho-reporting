package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

public class FastPageStateList implements PageStateList
{
  private int size;
  private PageState initialState;
  private ReportProcessor reportProcessor;

  public FastPageStateList(final ReportProcessor reportProcessor)
  {
    if (reportProcessor == null)
    {
      throw new NullPointerException();
    }
    this.reportProcessor = reportProcessor;
  }

  public int size()
  {
    return size;
  }

  public void add(final PageState state)
  {
    if (size == 0)
    {
      state.prepareStorage();
      initialState = state;
    }
    size += 1;
  }

  public void clear()
  {
    initialState = null;
    size = 0;

  }

  public PageState get(final int index)
  {
    if (index == 0)
    {
      return initialState;
    }

    try
    {
      PageState state = initialState;
      for (int i = 0; i <= index; i++)
      {
        state = reportProcessor.processPage(state, false);
        if (state == null)
        {
          throw new IllegalStateException("State returned is null: Report processing reached premature end-point.");
        }
      }
      return state;
    }
    catch (ReportProcessingException e)
    {
      throw new IllegalStateException("State restoration failed.");
    }
  }
}
