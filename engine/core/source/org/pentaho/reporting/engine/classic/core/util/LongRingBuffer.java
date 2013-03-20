package org.pentaho.reporting.engine.classic.core.util;

public class LongRingBuffer
{
  private long[] values;
  private int index;
  private int count;

  public LongRingBuffer(final int size)
  {
    if (size < 1)
    {
      throw new IllegalArgumentException();
    }
    values = new long[size];
  }

  public void add (final long value)
  {
    values[index] = value;
    index += 1;
    count += 1;
    if (index == values.length)
    {
      index = 0;
    }
  }

  public void replaceLastAdded(final long value)
  {
    if (count == 0)
    {
      add(value);
      return;
    }

    count -= 1;
    if (index == 0)
    {
      index = values.length - 1;
    }
    else
    {
      index -= 1;
    }
    add (value);
  }

  public long getFirstValue()
  {
    if (count < values.length)
    {
      return values[0];
    }
    return values[index];
  }

  public long getLastValue()
  {
    final int lastIndex;
    if (index == 0)
    {
      lastIndex = values.length - 1;
    }
    else
    {
      lastIndex = index - 1;
    }
    return values[lastIndex];
  }
}
