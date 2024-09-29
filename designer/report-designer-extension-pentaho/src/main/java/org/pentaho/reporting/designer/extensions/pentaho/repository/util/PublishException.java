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


package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

/**
 * User: Martin Date: 04.08.2006 Time: 21:04:35
 */
public class PublishException extends Exception {
  public static final int ERROR_UNKNOWN = 0;
  public static final int ERROR_FILE_EXISTS = 1;
  public static final int ERROR_FAILED = 2;
  public static final int ERROR_SUCCESSFUL = 3;
  public static final int ERROR_INVALID_PASSWORD = 4;
  public static final int ERROR_INVALID_USERNAME_OR_PASSWORD = 5;

  private int errorCode;
  private int httpReason;

  public PublishException( final int errorCode ) {
    super( translateReturnValue( errorCode ) );
    this.errorCode = errorCode;
  }

  public PublishException( final int errorCode, final int httpReason ) {
    super( translateReturnValue( errorCode ) + ": " + httpReason );
    this.errorCode = errorCode;
    this.httpReason = httpReason;
  }

  public PublishException( final int errorCode, final Throwable cause ) {
    super( translateReturnValue( errorCode ), cause );
    this.errorCode = errorCode;
  }

  public int getHttpReason() {
    return httpReason;
  }

  public int getErrorCode() {
    return errorCode;
  }

  private static String translateReturnValue( final int errorCode ) {
    switch ( errorCode ) {
      case 0:
        return Messages.getInstance().getString( "PublishToServerAction.Successful" );
      case 1:
        return Messages.getInstance().getString( "PublishToServerAction.FileExistsError" );
      case 2:
        return Messages.getInstance().getString( "PublishToServerAction.Failed" );
      case 3:
        return Messages.getInstance().getString( "PublishToServerAction.Successful" );
      case 4:
        return Messages.getInstance().getString( "PublishToServerAction.InvalidPassword" );
      case 5:
        return Messages.getInstance().getString( "PublishToServerAction.InvalidUsernameOrPassword" );
      default:
        return Messages.getInstance().getString( "PublishToServerAction.Successful" );
    }
  }

}
