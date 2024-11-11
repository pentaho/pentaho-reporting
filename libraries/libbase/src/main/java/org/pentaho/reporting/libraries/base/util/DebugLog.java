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


package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides static log messages for on-going bug-hunting efforts. This removes the need to create temporary
 * log-instances that get removed later anyway.
 *
 * @author Thomas Morgner
 */
public final class DebugLog {
  /**
   * A logger.
   */
  private static final Log logger = LogFactory.getLog( DebugLog.class );

  /**
   * Logs a message using the debug-logger. By channeling all temporary log messages through this method, we can later
   * easily identify the debugger log entries so that we can remove them.
   *
   * @param message the message.
   */
  public static void log( final Object message ) {
    logger.info( message );
  }

  /**
   * Logs a message using the debug-logger. By channeling all temporary log messages through this method, we can later
   * easily identify the debugger log entries so that we can remove them.
   *
   * @param message the message.
   * @param t       the throwable to be logged.
   */
  public static void log( final Object message, final Throwable t ) {
    logger.info( message, t );
  }

  /**
   * Logs a HERE message. This is only useful as some sort of cheap-and-dirty debug-point entry.
   */
  public static void logHere() {
    logger.info( "HERE: Debug point reached" );
  }

  /**
   * Logs a HERE message along with a stack-trace to identify how we got to this point.
   */
  public static void logHereWE() {
    logger.info( "HERE: Debug point reached", new Exception( "Debug-Point reached" ) );
  }

  public static void logEnter() {
    logger.info( "HERE: Enter" );
  }

  public static void logExit() {
    logger.info( "HERE: Exit" );
  }

  /**
   * Private constructor prevents object creation.
   */
  private DebugLog() {
  }

  public static void startProfiling() {
    logger.info( "Start Profiling" );
  }

  public static void finishProfiling() {
    logger.info( "Finish Profiling" );
  }
}
