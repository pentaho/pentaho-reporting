/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
