package org.pentaho.reporting.engine.classic.core.util;

import java.util.ArrayList;

public class RingBuffer<T>
{
  private ArrayList<T> values;
  private int index;
  private int count;
  private int size;

  public RingBuffer(final int size)
  {
    if (size < 1)
    {
      throw new IllegalArgumentException();
    }
    this.size = size;
    this.values = new ArrayList<T>(size);
    for (int i = 0; i < size; i += 1)
    {
      values.add(null);
    }
  }

  public void add(final T value)
  {
    values.set(index, value);
    index += 1;
    count += 1;
    if (index == size)
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
      index = size - 1;
    }
    else
    {
      index -= 1;
    }
    add(value);
  }

  public T getFirstValue()
  {
    if (count < size)
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
      lastIndex = size - 1;
    }
    else
    {
      lastIndex = index - 1;
    }
    return values.get(lastIndex);
  }

  public int size()
  {
    return size;
  }

  public T get(final int index)
  {
    return values.get(index);
  }

  public void set(final int index, final T value)
  {
    values.set(index, value);
  }

  public void resize(final int newSize)
  {
    if (newSize <= 0)
    {
      throw new IllegalStateException();
    }

    if (newSize > values.size())
    {
      values.clear();
      for (int i = 0; i < newSize; i += 1)
      {
        values.add(null);
      }
    }

    size = newSize;
  }
}
