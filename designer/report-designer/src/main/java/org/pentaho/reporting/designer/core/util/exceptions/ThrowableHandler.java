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
