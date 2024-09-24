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

package org.pentaho.reporting.designer.core.util.exceptions;

import java.awt.*;

/**
 * User: Martin Date: 09.02.2006 Time: 13:21:46
 */
public class ThrowableHandler implements Thread.UncaughtExceptionHandler {
  private class HandleExceptionTask implements Runnable {
    private final Throwable e;

    private HandleExceptionTask( final Throwable e ) {
      this.e = e;
    }

    public void run() {
      try {
        handle( e );
      } catch ( Throwable e ) {
        e.printStackTrace();
      }
    }
  }

  private static final ThrowableHandler instance = new ThrowableHandler();

  public static ThrowableHandler getInstance() {
    return instance;
  }


  public ThrowableHandler() {
  }


  public void handle( final Throwable throwable ) {
    UncaughtExceptionsModel.getInstance().addException( throwable );
  }


  public void uncaughtException( final Thread t, final Throwable e ) {
    if ( !EventQueue.isDispatchThread() ) {
      try {
        EventQueue.invokeLater( new HandleExceptionTask( e ) );
      } catch ( Throwable ex ) {
        ex.printStackTrace();
      }
    } else {
      handle( e );
    }
  }
}
