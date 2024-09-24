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
