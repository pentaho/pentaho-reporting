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
