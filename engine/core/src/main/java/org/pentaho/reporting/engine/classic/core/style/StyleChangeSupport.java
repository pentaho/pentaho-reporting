/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.style;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * A utility class for managing a collection of {@link StyleChangeListener} objects.
 *
 * @author Thomas Morgner.
 */
public class StyleChangeSupport {
  /**
   * Storage for the listeners.
   */
  private ArrayList listeners;

  /**
   * The source.
   */
  private final ElementStyleSheet source;

  /**
   * Creates a new support object.
   *
   * @param source
   *          the source of change events.
   */
  public StyleChangeSupport( final ElementStyleSheet source ) {
    this.source = source;
  }

  /**
   * Adds a listener.
   *
   * @param l
   *          the listener.
   */
  public void addListener( final StyleChangeListener l ) {
    if ( l == null ) {
      throw new NullPointerException( "Listener == null" );
    }
    if ( listeners == null ) {
      listeners = new ArrayList( 5 );
    }
    listeners.add( new WeakReference( l ) );
  }

  /**
   * Removes a listener.
   *
   * @param l
   *          the listener.
   */
  public void removeListener( final StyleChangeListener l ) {
    if ( l == null ) {
      throw new NullPointerException( "Listener == null" );
    }
    if ( listeners == null ) {
      return;
    }
    listeners.remove( l );
  }

  /**
   * Notifies all listeners that a style has changed.
   *
   * @param key
   *          the style key.
   * @param value
   *          the new style value.
   */
  public void fireStyleChanged( final StyleKey key, final Object value ) {
    if ( listeners == null ) {
      return;
    }
    ArrayList removeList = null;

    for ( int i = 0; i < listeners.size(); i++ ) {
      final WeakReference ref = (WeakReference) listeners.get( i );
      final StyleChangeListener l = (StyleChangeListener) ref.get();
      if ( l != null ) {
        l.styleChanged( source, key, value );
      } else {
        if ( removeList == null ) {
          removeList = new ArrayList( 5 );
        }
        removeList.add( ref );
      }
    }
    if ( removeList != null ) {
      listeners.removeAll( removeList );
    }
  }

  /**
   * Notifies all listeners that a style has been removed.
   *
   * @param key
   *          the style key.
   */
  public void fireStyleRemoved( final StyleKey key ) {
    if ( listeners == null ) {
      return;
    }
    ArrayList removeList = null;

    for ( int i = 0; i < listeners.size(); i++ ) {
      final WeakReference ref = (WeakReference) listeners.get( i );
      final StyleChangeListener l = (StyleChangeListener) ref.get();
      if ( l != null ) {
        l.styleRemoved( source, key );
      } else {
        if ( removeList == null ) {
          removeList = new ArrayList( 5 );
        }
        removeList.add( ref );
      }
    }
    if ( removeList != null ) {
      listeners.removeAll( removeList );
    }
  }
}
