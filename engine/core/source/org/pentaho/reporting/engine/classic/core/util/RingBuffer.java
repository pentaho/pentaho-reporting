package org.pentaho.reporting.engine.classic.core.util;

import java.util.ArrayList;

public class RingBuffer<T>
{
  private ArrayList<T> values;
  private int index;
  private int count;

  public RingBuffer(final int size)
  {
    if (size < 1)
    {
      throw new IllegalArgumentException();
    }
    values = new ArrayList<T>(size);
    for (int i = 0; i < size; i+= 1)
    {
      values.add(null);
    }
  }

  public void add (final T value)
  {
    values.set(index, value);
    index += 1;
    count += 1;
    if (index == values.size())
    {
      index = 0;
    }
  }

  public void replaceLastAdded(final T value)
  {
    if (count == 0)
    {
      add(value);
      return;
    }

    count -= 1;
    if (index == 0)
    {
      index = values.size() - 1;
    }
    else
    {
      index -= 1;
    }
    add (value);
  }

  public T getFirstValue()
  {
    if (count < values.size())
    {
      return values.get(0);
    }
    return values.get(index);
  }

  public T getLastValue()
  {
    final int lastIndex;
    if (index == 0)
    {
      lastIndex = values.size() - 1;
    }
    else
    {
      lastIndex = index - 1;
    }
    return values.get(lastIndex);
  }

  public int size()
  {
    return values.size();
  }

  public T get (int index)
  {
    return values.get(index);
  }

  public void set (int index, T value)
  {
    values.set(index, value);
  }
}
