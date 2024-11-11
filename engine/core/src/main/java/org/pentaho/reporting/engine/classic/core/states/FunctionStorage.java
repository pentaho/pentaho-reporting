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

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creation-Date: Dec 15, 2006, 2:24:30 PM
 *
 * @author Thomas Morgner
 */
public class FunctionStorage implements Serializable {
  private static final boolean PARANOID_CHECK = false;
  private HashMap storage;

  public FunctionStorage() {
    storage = new HashMap();
  }

  /**
   * Stores expressions at the end of a run.
   *
   * @param key
   * @param expressions
   */
  public void store( final FunctionStorageKey key, final Expression[] expressions, final int length )
    throws ReportProcessingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( expressions == null ) {
      throw new NullPointerException();
    }
    try {
      final Expression[] copy = new Expression[length];
      for ( int i = 0; i < length; i++ ) {
        copy[i] = (Expression) expressions[i].clone();
      }
      storage.put( key, copy );
    } catch ( CloneNotSupportedException e ) {
      throw new ReportProcessingException( "Storing expressions failed." );
    }
  }

  public Expression[] restore( final FunctionStorageKey key ) throws ReportProcessingException {
    if ( key == null ) {
      throw new NullPointerException();
    }

    try {
      final Expression[] expressions = (Expression[]) storage.get( key );
      if ( expressions == null ) {
        return null;
      }

      final Expression[] copy = (Expression[]) expressions.clone();
      for ( int i = 0; i < expressions.length; i++ ) {
        copy[i] = (Expression) expressions[i].clone();
      }

      if ( PARANOID_CHECK ) {
        final Iterator iterator = storage.entrySet().iterator();
        while ( iterator.hasNext() ) {
          final Map.Entry entry = (Map.Entry) iterator.next();
          if ( key.equals( entry.getKey() ) ) {
            final FunctionStorageKey o = (FunctionStorageKey) entry.getKey();
            if ( key.getReportId() != o.getReportId() ) {
              throw new IllegalStateException( "key.getReportId() != o.getReportId() : " + key.getReportId() + " != "
                  + o.getReportId() );
            }
          }
        }
      }

      return copy;
    } catch ( CloneNotSupportedException e ) {
      throw new ReportProcessingException( "Restoring expressions failed." );
    }
  }
}
