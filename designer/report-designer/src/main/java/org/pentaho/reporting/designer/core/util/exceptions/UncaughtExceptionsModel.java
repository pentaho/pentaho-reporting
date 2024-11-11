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


package org.pentaho.reporting.designer.core.util.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.libraries.designtime.swing.WeakEventListenerList;

import java.util.ArrayList;

/**
 * User: Martin Date: 24.02.2006 Time: 09:36:43
 */
public class UncaughtExceptionsModel {
  private static final Log LOG = LogFactory.getLog( UncaughtExceptionsModel.class );
  private static final int MAXIMUM_SIZE = 100;
  private static final UncaughtExceptionsModel instance = new UncaughtExceptionsModel();

  public static UncaughtExceptionsModel getInstance() {
    return instance;
  }

  private WeakEventListenerList uncaughtExceptionModelListeners;
  private ArrayList<Throwable> throwables;

  public UncaughtExceptionsModel() {
    uncaughtExceptionModelListeners = new WeakEventListenerList();
    throwables = new ArrayList<Throwable>();
  }

  public void addException( final Throwable throwable ) {
    LOG.error( UtilMessages.getInstance().getString( "UncaughtExcpetionsModel.AddException" ), throwable );

    throwables.add( throwable );
    if ( throwables.size() > MAXIMUM_SIZE ) {
      //noinspection ThrowableResultOfMethodCallIgnored
      throwables.remove( 0 );
    }

    fireExceptionAdded( throwable );
  }

  private void fireExceptionAdded( final Throwable throwable ) {
    final UncaughtExceptionModelListener[] listeners =
      uncaughtExceptionModelListeners.getListeners( UncaughtExceptionModelListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final UncaughtExceptionModelListener listener = listeners[ i ];
      listener.exceptionCaught( throwable );
    }
  }


  public Throwable[] getThrowables() {
    return throwables.toArray( new Throwable[ throwables.size() ] );
  }


  public void addUncaughtExceptionModelListener( final UncaughtExceptionModelListener uncaughtExceptionModelListener ) {
    uncaughtExceptionModelListeners.add( UncaughtExceptionModelListener.class, uncaughtExceptionModelListener );
  }


  public void removeUncaughtExceptionModelListener(
    final UncaughtExceptionModelListener uncaughtExceptionModelListener ) {
    uncaughtExceptionModelListeners.remove( UncaughtExceptionModelListener.class, uncaughtExceptionModelListener );
  }


  public void clearExceptions() {
    throwables.clear();

    final UncaughtExceptionModelListener[] listeners =
      uncaughtExceptionModelListeners.getListeners( UncaughtExceptionModelListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final UncaughtExceptionModelListener listener = listeners[ i ];
      listener.exceptionsCleared();
    }
  }


  public void exceptionsViewed() {
    final UncaughtExceptionModelListener[] listeners =
      uncaughtExceptionModelListeners.getListeners( UncaughtExceptionModelListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final UncaughtExceptionModelListener listener = listeners[ i ];
      listener.exceptionsViewed();
    }
  }
}
