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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

public class PublishExceptionTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testPublishExceptionInt() {
    PublishException exception = new PublishException( PublishException.ERROR_UNKNOWN );
    assertNotNull( exception );
    assertEquals( PublishException.ERROR_UNKNOWN, exception.getErrorCode() );
  }

  @Test
  public void testPublishExceptionIntInt() {
    PublishException exception = new PublishException( PublishException.ERROR_UNKNOWN, 404 );
    assertNotNull( exception );
    assertEquals( 404, exception.getHttpReason() );
  }

  @Test
  public void testPublishExceptionIntThrowable() {
    PublishException exception = new PublishException( PublishException.ERROR_UNKNOWN, new Throwable() );
    assertNotNull( exception );
  }

  @Test
  public void testPublishExceptionGetLocalizedMessage() {
    PublishException exception = new PublishException( PublishException.ERROR_UNKNOWN );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Successful" ), exception
        .getLocalizedMessage() );

    exception = new PublishException( PublishException.ERROR_FILE_EXISTS );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.FileExistsError" ), exception
        .getLocalizedMessage() );

    exception = new PublishException( PublishException.ERROR_FAILED );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Failed" ), exception.getLocalizedMessage() );

    exception = new PublishException( PublishException.ERROR_SUCCESSFUL );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Successful" ), exception
        .getLocalizedMessage() );

    exception = new PublishException( PublishException.ERROR_INVALID_PASSWORD );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.InvalidPassword" ), exception
        .getLocalizedMessage() );

    exception = new PublishException( PublishException.ERROR_INVALID_USERNAME_OR_PASSWORD );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.InvalidUsernameOrPassword" ), exception
        .getLocalizedMessage() );

    exception = new PublishException( 6 );
    assertNotNull( exception );
    assertEquals( Messages.getInstance().getString( "PublishToServerAction.Successful" ), exception
        .getLocalizedMessage() );

  }
}
