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


package org.pentaho.reporting.designer.core.settings.ui;


/**
 * User: Martin Date: 01.03.2006 Time: 18:14:22
 */
public class ValidationMessage {
  public enum Severity {
    WARN,
    ERROR
  }

  private Severity severity;
  private String message;

  public ValidationMessage( final Severity severity, final String message ) {
    if ( severity == null ) {
      throw new NullPointerException();
    }
    if ( message == null ) {
      throw new NullPointerException();
    }
    this.severity = severity;
    this.message = message;
  }


  public Severity getSeverity() {
    return severity;
  }


  public String getMessage() {
    return message;
  }
}
