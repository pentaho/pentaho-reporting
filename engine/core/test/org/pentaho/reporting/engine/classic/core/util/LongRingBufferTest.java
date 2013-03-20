package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class LongRingBufferTest extends TestCase
{
  public LongRingBufferTest()
  {
  }

  public void testInvalidSize()
  {
    try
    {
      new LongRingBuffer(0);
      Assert.fail();
    }
    catch (IllegalArgumentException e)
    {
      // ignore
    }
  }

  public void testAdding()
  {
    final LongRingBuffer tc = new LongRingBuffer(3);
    tc.add(10);
    tc.add(20);
    tc.add(30);
    tc.add(40);
    assertEquals(40, tc.getLastValue());
    assertEquals(20, tc.getFirstValue());
  }

  public void testAdding2()
  {
    final LongRingBuffer tc = new LongRingBuffer(3);
    tc.add(10);
    tc.add(20);
    tc.add(30);
    assertEquals(30, tc.getLastValue());
    assertEquals(10, tc.getFirstValue());
  }

  public void testReplace()
  {
    final LongRingBuffer tc = new LongRingBuffer(3);
    tc.add(10);
    tc.add(20);
    tc.add(30);
    assertEquals(30, tc.getLastValue());
    assertEquals(10, tc.getFirstValue());
    tc.replaceLastAdded(40);
    assertEquals(40, tc.getLastValue());
    assertEquals(10, tc.getFirstValue());
    tc.add(50);
    assertEquals(50, tc.getLastValue());
    assertEquals(20, tc.getFirstValue());
  }

  public void testReplaceFirst()
  {
    final LongRingBuffer tc = new LongRingBuffer(3);
    tc.replaceLastAdded(10);
    assertEquals(10, tc.getFirstValue());
    assertEquals(10, tc.getLastValue());
    tc.add(10);
    tc.add(20);
    tc.add(30);
    tc.add(40);
    assertEquals(40, tc.getLastValue());
    assertEquals(20, tc.getFirstValue());
  }

}
