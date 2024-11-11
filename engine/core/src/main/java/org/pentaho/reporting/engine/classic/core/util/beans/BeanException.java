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
