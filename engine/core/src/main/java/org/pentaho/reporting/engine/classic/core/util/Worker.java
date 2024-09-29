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


package org.pentaho.reporting.engine.classic.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple worker implementation. The worker executes a assigned workload and then sleeps until another workload is set
 * or the worker is killed.
 *
 * @author Thomas Morgner
 */
public final class Worker extends Thread {
  private static final Log logger = LogFactory.getLog( Worker.class );
  private static final int STATE_IDLE = 0;
  private static final int STATE_WORKING = 1;
  private static final int STATE_DEAD = 2;

  private final Object lock;

  /**
   * the worker's task.
   */
  private Runnable workload;

  /**
   * a flag whether the worker should exit after the processing.
   */
  private int state;

  /**
   * the time in milliseconds beween 2 checks for exit or work requests.
   */
  private final int sleeptime;

  /**
   * Creates a new worker.
   *
   * @param sleeptime
   *          the time this worker sleeps until he checks for new work.
   */
  public Worker( final int sleeptime ) {
    this.lock = new Object();
    this.sleeptime = sleeptime;
    this.setDaemon( true );
    start();
  }

  /**
   * Creates a new worker with an default infinite idle timeout.
   */
  public Worker() {
    this( 0 );
  }

  /**
   * Set the next workload for this worker.
   *
   * @param r
   *          the next workload for the worker.
   * @throws IllegalStateException
   *           if the worker is not idle.
   */
  public void setWorkload( final Runnable r ) {
    synchronized ( lock ) {
      if ( state == Worker.STATE_DEAD ) {
        throw new IllegalStateException( "Thread is dead already." );
      }

      if ( workload != null ) {
        throw new IllegalStateException( "This worker is not idle." );
      }

      workload = r;
      lock.notifyAll();
    }
  }

  /**
   * Returns the workload object.
   *
   * @return the runnable executed by this worker thread.
   */
  public synchronized Runnable getWorkload() {
    return workload;
  }

  /**
   * Kills the worker immediately. Awakens the worker if he's sleeping, so that the worker dies without delay.
   */
  public void finish() {
    synchronized ( lock ) {
      if ( state == Worker.STATE_DEAD ) {
        return;
      }

      state = Worker.STATE_DEAD;

      try {
        this.interrupt();
      } catch ( SecurityException se ) {
        // ignored
      }
    }
  }

  /**
   * Checks whether this worker has some work to do.
   *
   * @return true, if this worker has no more work and is currently sleeping.
   */
  public boolean isAvailable() {
    synchronized ( lock ) {
      return ( state == Worker.STATE_IDLE );
    }
  }

  /**
   * If a workload is set, process it. After the workload is processed, this worker starts to sleep until a new workload
   * is set for the worker or the worker got the finish() request.
   */
  public void run() {
    while ( true ) {
      final Runnable nextWorkLoad;
      synchronized ( lock ) {
        if ( workload != null ) {
          state = Worker.STATE_WORKING;
          nextWorkLoad = workload;
          workload = null;
        } else {
          nextWorkLoad = null;
        }
      }

      try {
        if ( nextWorkLoad != null ) {
          nextWorkLoad.run();
        }
      } catch ( Exception e ) {
        Worker.logger.error( "Worker caught exception on run: ", e );
      }

      synchronized ( lock ) {
        if ( state == Worker.STATE_DEAD ) {
          synchronized ( this ) {
            this.notifyAll();
          }
          return;
        }

        state = Worker.STATE_IDLE;

        try {
          // remove lock
          lock.wait( sleeptime );
        } catch ( InterruptedException ie ) {
          // ignored
        }
      }
    }

  }

  /**
   * Checks whether this worker has received the signal to finish and die.
   *
   * @return true, if the worker should finish the work and end the thread.
   */
  public boolean isFinish() {
    synchronized ( lock ) {
      return state == Worker.STATE_DEAD;
    }
  }
}
