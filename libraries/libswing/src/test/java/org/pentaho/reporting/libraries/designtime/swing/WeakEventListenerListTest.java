/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.designtime.swing;

import junit.framework.TestCase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WeakEventListenerListTest extends TestCase {
  private static class TestEventListener implements PropertyChangeListener {
    private TestEventListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {

    }
  }

  public WeakEventListenerListTest() {
  }

  public WeakEventListenerListTest( final String s ) {
    super( s );
  }

  public void testAddStaticRefs() {
    WeakEventListenerList list = new WeakEventListenerList();
    final TestEventListener listener = new TestEventListener();
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    assertEquals( "Size", list.getListenerCount( TestEventListener.class ), 5 );
  }

  public void testAddRemoveStaticRefs() {
    WeakEventListenerList list = new WeakEventListenerList();
    final TestEventListener listener = new TestEventListener();
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, listener );
    assertEquals( "Size", list.getListenerCount( TestEventListener.class ), 5 );

    list.remove( TestEventListener.class, listener );
    assertEquals( "Size", list.getListenerCount( TestEventListener.class ), 4 );

    list.add( PropertyChangeListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    assertEquals( "Size", list.getListenerCount( TestEventListener.class ), 0 );
    assertEquals( "Size", list.getListenerCount( PropertyChangeListener.class ), 1 );
  }

  public void testAddRemoveWeakRefs() {
    final WeakEventListenerList list = new WeakEventListenerList();
    final TestEventListener listener = new TestEventListener();
    list.add( TestEventListener.class, new TestEventListener() );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, new TestEventListener() );
    list.add( TestEventListener.class, listener );
    list.add( TestEventListener.class, new TestEventListener() );
    assertEquals( "Size", list.getListenerCount( TestEventListener.class ), 5 );

    list.remove( TestEventListener.class, listener );
    assertEquals( "Size", list.getListenerCount( TestEventListener.class ), 4 );

    list.add( PropertyChangeListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    list.remove( TestEventListener.class, listener );
    assertTrue( "Size is 3 or less", list.getListenerCount( TestEventListener.class ) <= 3 );
    assertEquals( "Size", list.getListenerCount( PropertyChangeListener.class ), 1 );
    System.gc();
    System.gc();
    System.gc();
    assertEquals( "Size", list.getListenerCount( PropertyChangeListener.class ), 1 );
  }


}
