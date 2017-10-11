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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;

import java.io.Serializable;
import java.util.HashMap;

public class DataFactoryManager implements Serializable {
  private HashMap<Object, CachingDataFactory> storage;

  public DataFactoryManager() {
    this.storage = new HashMap<Object, CachingDataFactory>();
  }

  public void store( final FunctionStorageKey key, final CachingDataFactory expressions, final boolean perDeclaration )
    throws ReportProcessingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( expressions == null ) {
      throw new NullPointerException();
    }

    if ( perDeclaration ) {
      storage.put( key.getReportId(), expressions );
    } else {
      storage.put( key, expressions );
    }
  }

  public CachingDataFactory restore( final FunctionStorageKey key, final boolean perDeclaration )
    throws ReportProcessingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    final CachingDataFactory expressions;
    if ( perDeclaration ) {
      expressions = storage.get( key.getReportId() );
    } else {
      expressions = storage.get( key );
    }
    if ( expressions == null ) {
      return null;
    }

    return expressions;
  }

  public void close() {
    final DataFactory[] objects = storage.values().toArray( new DataFactory[storage.size()] );
    for ( int i = 0; i < objects.length; i++ ) {
      final DataFactory object = objects[i];
      object.close();
    }
  }
}
