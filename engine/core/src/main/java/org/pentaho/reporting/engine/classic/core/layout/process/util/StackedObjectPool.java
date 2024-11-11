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
