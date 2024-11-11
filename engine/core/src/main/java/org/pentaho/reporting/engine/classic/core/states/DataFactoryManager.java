/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
