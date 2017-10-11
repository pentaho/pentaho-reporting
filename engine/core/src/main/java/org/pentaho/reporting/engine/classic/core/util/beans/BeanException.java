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

package org.pentaho.reporting.engine.classic.core.util.beans;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * The BeanException class signals errors when setting or reading bean properties.
 *
 * @author Thomas Morgner
 */
public class BeanException extends Exception {
  private static ThreadLocal localInstance = new ThreadLocal();
  private static volatile Boolean useCause;
  private String message;

  /**
   * DefaultConstructor.
   */
  public BeanException() {
  }

  /**
   * Creates a new BeanException with the given message and parent exception.
   *
   * @param message
   *          the message text
   * @param ex
   *          the parent exception
   */
  public BeanException( final String message, final Throwable ex ) {
    super( message, ex );
    this.message = message;
  }

  /**
   * Creates a new BeanException with the given message.
   *
   * @param message
   *          the message.
   */
  public BeanException( final String message ) {
    super( message );
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public static BeanException getInstance( final String message, final Throwable cause ) {
    if ( useCause == null ) {
      useCause =
          ( "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
              "org.pentaho.reporting.engine.classic.core.util.beans.BeanExceptionWithDetailedCause" ) ) );
    }

    if ( Boolean.TRUE.equals( useCause ) ) {
      return new BeanException( message, cause );
    }

    final BeanException o = (BeanException) localInstance.get();
    if ( o == null ) {
      final BeanException retval = new BeanException( message );
      localInstance.set( retval );
      return retval;
    }

    o.fillInStackTrace();
    o.message = message;
    o.printStackTrace();
    return o;
  }

}
