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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;

public abstract class StackedObjectPool<T> {
  private static final boolean paranoidModelChecks;

  static {
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks" ) ) ) {
      paranoidModelChecks = true;
    } else {
      paranoidModelChecks = false;
    }
  }

  public static boolean isParanoidModelChecks() {
    return paranoidModelChecks;
  }

  private ArrayList<T> backend;
  private int fillSize;
  private int useSize;

  protected StackedObjectPool() {
    backend = new ArrayList<T>();
  }

  protected abstract T create();

  protected T get() {
    if ( useSize < fillSize ) {
      final T retval = backend.get( useSize );
      useSize += 1;
      return retval;
    }

    final T retval = create();
    backend.add( retval );
    fillSize += 1;
    useSize += 1;
    return retval;
  }

  public void free( final T t ) {
    if ( isParanoidModelChecks() ) {
      if ( useSize == 0 ) {
        throw new IndexOutOfBoundsException();
      }
      if ( backend.get( useSize - 1 ) != t ) {
        throw new IllegalArgumentException();
      }
    }
    useSize -= 1;
  }
}
