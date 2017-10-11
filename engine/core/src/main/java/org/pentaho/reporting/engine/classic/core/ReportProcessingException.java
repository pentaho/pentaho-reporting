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

package org.pentaho.reporting.engine.classic.core;

/**
 * An exception that can be thrown during report processing, if an error occurs.
 *
 * @author Thomas Morgner
 */
public class ReportProcessingException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 3416682538157508440L;

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final String message, final Throwable ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final String message, final Exception ex ) {
    this( message, (Throwable) ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public ReportProcessingException( final String message ) {
    super( message );
  }

  /**
   * Creates an exception.
   *
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final Throwable ex ) {
    super( ex );
  }

  /**
   * Creates an exception.
   *
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final Exception ex ) {
    super( ex );
  }

}
