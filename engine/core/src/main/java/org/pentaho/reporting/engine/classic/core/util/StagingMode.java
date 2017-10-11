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

package org.pentaho.reporting.engine.classic.core.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class StagingMode implements Serializable {
  public static final StagingMode MEMORY = new StagingMode( "memory" );
  public static final StagingMode THRU = new StagingMode( "thru" );
  public static final StagingMode TMPFILE = new StagingMode( "tmpfile" );

  private String type;

  private StagingMode( final String type ) {
    this.type = type;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final StagingMode that = (StagingMode) o;

    if ( type != null ? !type.equals( that.type ) : that.type != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return ( type != null ? type.hashCode() : 0 );
  }

  public String toString() {
    return type;
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws java.io.ObjectStreamException
   *           if the element could not be resolved.
   * @noinspection UNUSED_SYMBOL
   */
  protected Object readResolve() throws ObjectStreamException {
    if ( this.type.equals( StagingMode.MEMORY.type ) ) {
      return StagingMode.MEMORY;
    }
    if ( this.type.equals( StagingMode.THRU.type ) ) {
      return StagingMode.THRU;
    }
    if ( this.type.equals( StagingMode.TMPFILE.type ) ) {
      return StagingMode.TMPFILE;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

  public static StagingMode valueOf( final String s ) {
    if ( s == null ) {
      throw new NullPointerException();
    }
    if ( s.equals( StagingMode.MEMORY.type ) ) {
      return StagingMode.MEMORY;
    }
    if ( s.equals( StagingMode.THRU.type ) ) {
      return StagingMode.THRU;
    }
    if ( s.equals( StagingMode.TMPFILE.type ) ) {
      return StagingMode.TMPFILE;
    }
    // unknown element alignment...
    throw new IllegalArgumentException();
  }
}
